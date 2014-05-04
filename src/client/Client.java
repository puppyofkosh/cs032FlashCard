package client;

import flashcard.FlashCard;
import gui.ClientFrontend;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import protocol.AllCardsRequest;
import protocol.CardListResponse;
import protocol.ConnectionSuccessfulResponse;
import protocol.MetaDataCardRequest;
import protocol.MetaDataResponse;
import protocol.NetworkedFlashCard;
import protocol.ParametrizedCardRequest;
import protocol.Request;
import protocol.Response;
import protocol.UploadCardsRequest;
import protocol.UploadCardsResponse;
import search.FilePathSearch;
import search.SearchParameters;
import utils.Writer;

public class Client extends SwingWorker<Response, Response> {

	private Socket _socket;
	private volatile boolean _running, _hasServer;
	private int _port;
	private ObjectOutputStream _output;
	private ObjectInputStream _input;
	private ReceiveThread _thread;
	private String _hostName;
	private Queue<Request> _requests;
	private ClientFrontend _frontend;
	private int numRequests;
	
	private volatile String _guiMessage = "";
	private volatile boolean _guiMessageChanged = false;
	
	private void setGuiMessage(String s)
	{
		_guiMessage = s;
		_guiMessageChanged = true;
	}
	private String processGuiMessage()
	{
		_guiMessageChanged = false;
		return _guiMessage;
	}
	
	
	/**
	 * Constructs a Client with the given port.
	 * 
	 * @param port
	 *            the port number the client will connect to
	 */
	public Client(String hostName, int port, ClientFrontend frontend) {
		_port = port;
		_hostName = hostName;
		_running = false;
		_frontend = frontend;
		_requests = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Starts the Client, so it connects to the sever. It will set up all the
	 * necessary requirements, and then launch the GUI.
	 * THIS IS CALLED IN a separate thread. It should not be modifying GUI stuff
	 */
	private void connect(int maxAttempts) {
		int num_attempts = 0;
		while (!_running) {

			if (num_attempts > maxAttempts) {
				Writer.err("ERROR: Server unavailable. Max number of reconnection attempts reached.");
				return;
			}
			num_attempts++;

			try {
				Writer.out("Client Connecting");
				// host is localhost or IP if an IP address is specified
				_socket = new Socket(_hostName, _port);
				_output = new ObjectOutputStream(_socket.getOutputStream());
				_input = new ObjectInputStream(_socket.getInputStream());

				_running = true;
				_hasServer = true;

				_thread = new ReceiveThread();
				_thread.start();

			} catch (IOException ex) {
				Writer.out("ERROR: Can't connect to server");
				setGuiMessage("Server unavailable!");
				_hasServer = false;
				try {
					Thread.sleep(2500);
					setGuiMessage("Attempting to reconnect...");
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					Writer.err("ERROR trying to reconnect to server");
					kill();
					return;
				}
			}
		}
	}

	@Override
	public Response doInBackground() {
		connect(0);
		// Hack: After connecting return an empty response to show the frontened
		// we're connected
		publish(new ConnectionSuccessfulResponse());

		while (_running && !_socket.isClosed()) {
			setProgress(33);
			if (!_requests.isEmpty() && _hasServer) {
				try {
					Thread.sleep(1000);
					setProgress(40);
					Writer.out("Sending Request");
					setProgress(50);
					_output.writeObject(_requests.poll());
					_output.flush();
					setProgress(75);
					Writer.out("Sent Request", numRequests);
					numRequests++;
				} catch (IOException e) {
					e.printStackTrace();
					Writer.out("??Server closed");
				} catch (InterruptedException e) {
				}
			}
		}
		kill();
		return null;
	}

	/**
	 * Shuts down the client closing all the connections.
	 */
	public void kill() {
		_running = false;
		_frontend.displayConnectionStatus(false);
		try {
			if (_input != null)
				_input.close();
			if (_output != null)
				_output.close();
			if (_socket != null)
				_socket.close();
			if (_thread != null)
				_thread.join();
		} catch (IOException e) {
			Writer.err("ERROR closing streams and/or socket");
		} catch (InterruptedException e) {
			Writer.err("ERROR joining receive thread");
		}
	}

	/**
	 * A method that sends a message to the server.
	 * 
	 * @param message
	 *            that will be sent to the server for broadcasting.
	 * @throws IOException
	 */
	public void request(Request r) {
		_requests.add(r);
	}

	public boolean isConnected() {
		return _hasServer && _running;
	}

	// TODO - these methods should go somewhere else probably?
	public void requestAllCards() {
		request(new AllCardsRequest());
	}

	public void uploadCards(List<FlashCard> cards) {
		Writer.out("Upload cards command");
		request(new UploadCardsRequest(cards));
	}

	public void requestCard(String input) {
		if (input.length() == 0)
			requestAllMetaData();
		// requestAllCards();
		else
			request(new MetaDataCardRequest(new SearchParameters(input)));
			//request(new ParametrizedCardRequest(new SearchParameters(input)));
	}

	public void requestFullCard(List<NetworkedFlashCard> nc) {
		List<String> ids = new ArrayList<>();
		for (NetworkedFlashCard card : nc)
		{
			ids.add(card.getIdentifier());
		}
		request(new ParametrizedCardRequest(new FilePathSearch(ids)));
	}

	public void requestAllMetaData() {
		request(new MetaDataResponse.MetaDataRequest());
	}

	/**
	 * A thread that will receive the messages sent by the server to display to
	 * the user.
	 */
	class ReceiveThread extends Thread {
		public void run() {
			while (_running) {
				try {
					Object o = _input.readObject();
					System.out.println(o);
					Response received = (Response) o;
					Writer.out("Response received");
					publish(received);
				} catch (IOException e) {
					if (_running == false || e instanceof EOFException) {
						Writer.out("Server has closed.");
						return;
					} else if (e instanceof SocketException) {
						Writer.err("Server unavailable. Please try again later");
						break;
					}
					Writer.err("ERROR reading line from socket or write to STD_OUT");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Process a response from the queue. Figures out which kind of response it
	 * is and then acts accordingly.
	 * 
	 * @param resp
	 *            - the response to be processed
	 */
	private void processResponse(Response resp) {
		switch (resp.getType()) {
		case CONNECTION_SUCCESSFUL:
			_frontend.displayConnectionStatus(true);
			return;
		case SORTED_CARDS:
			Writer.out("Received sorted cards response");
			CardListResponse cLR = (CardListResponse) resp;
			_frontend.updateLocallyStoredCards(cLR.getSortedCards());
			return;
		case SORTED_SETS:
			break;
		case UPLOAD:
			UploadCardsResponse ucR = (UploadCardsResponse) resp;
			_frontend.guiMessage("Upload "
					+ (ucR.confirmed() ? "Successful" : "Failed"));
			System.out.println("RECEIVED");
			return;
		case META_DATA:
			MetaDataResponse mdR = (MetaDataResponse) resp;
			System.out.println(mdR.getSortedCards().size());

			_frontend.updateCardsForImport(mdR.getSortedCards());
			// _frontend.update(mdR.getSortedCards());
			return;
		default:
			break;
		}
	}

	@Override
	protected void process(List<Response> chunks) {
		for (Response r : chunks)
		{
			processResponse(r);
		}
		if (_guiMessage.length() > 0)
			_frontend.guiMessage(_guiMessage);
	}

}
