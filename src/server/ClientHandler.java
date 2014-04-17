package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import protocol.Request;
import protocol.Response;
import utils.Print;
import flashcard.FlashCard;
/**
 * A thread for handling client connections to the server.
 * @author skortchm
 */
public class ClientHandler extends Thread {
	private Socket _client;
	private ObjectInputStream _input;
	private ObjectOutputStream _output;
	private ClientPool _pool;
	private boolean _running = false;
	private volatile Map<String, FlashCard> _serverCardLibrary;
	private PushThread _pushThread;
	ConcurrentLinkedQueue<Response> _responseQueue;


	/**
	 * Default constructor.
	 * Sets up this handler to be ready to receive requests 
	 * from and write responses to the client.
	 * @param pool - the shared pool of currently running client handlers on this server.
	 * @param clientSocket - the connection to the client
	 * @param backend - the backend which will compute and return the results of the requests sent from the client.
	 * @throws IOException if the client connection is unable to open an input or output stream.
	 */
	public ClientHandler(ClientPool pool, Socket clientSocket, Map<String, FlashCard> cardLibrary) throws IOException {
		if (pool == null || clientSocket == null)
			throw new IllegalArgumentException("Cannot accept null arguments.");

		_client = clientSocket;
		_pool = pool;
		_serverCardLibrary = cardLibrary;
		
		_responseQueue = new ConcurrentLinkedQueue<>();

		_output = new ObjectOutputStream(_client.getOutputStream());
		_input = new ObjectInputStream(_client.getInputStream());

		_pool.add(this);
	}


	/**
	 * Process requests from the client and
	 * respond with the data resulting from the request.
	 */
	public void run() {
		_running = true;
		_pushThread = new PushThread();
		_pushThread.start();
		try {
			Request req;
			while (_running) {
				req = (Request) _input.readObject();
				processRequest(req);
			}
			Print.out("Running has ceased. Goodbye.");
		} catch(IOException | ClassNotFoundException e) {
			Print.out("-- Client exited. --");
		} finally {
			kill();
		}
	}


	/**
	 * Processes the parametric request and query the backend for
	 * some type of data based on the type of the request. 
	 * Then wraps the data received from the backend in a Response
	 * object and returns that object to be written to the client.   
	 * @param req - the request to process
	 * @return
	 * A response containing the data received from the backend.
	 */
	private void processRequest(Request req) {
		Print.debug("Processing Request...\n", req);
		switch (req.getType()) {
		case CARD_LIST:
			CardListRequest clR = (CardListRequest) req;
			break;
		case SET_LIST:
			break;
		default:
			//not much we can do with an invalid request
			throw new IllegalArgumentException("Unsupported request type");

		}
	}

	/**
	 * Kills this handler and cleans up its additional resources.
	 * 
	 * @throws IOException when the client's connection or data streams 
	 * is either already closed or cannot be closed for some reason.
	 */
	public void kill() {
		try {
			_running = false;
			_pool.remove(this);
			_input.close();
			_output.close();
			_client.close();			
		} catch (IOException e) {
			Print.err("ERROR killing client handler.\n", e.getMessage());	
		}
	}

	/**
	 * A very important class - responsible for pushing thread responses to the client
	 *
	 */
	private class PushThread extends Thread {

		@Override
		public void run() {
			try {
				if (_b.isDone()) {
					//if backend finished some time in the past, send the client connection response.
					_output.writeObject(new ClientConnectionResponse(_b.getInitialWays(), MapFactory.getTrafficMap(), Constants.MINIMUM_LATITUDE, Constants.MAXIMUM_LATITUDE, Constants.MINIMUM_LONGITUDE, Constants.MAXIMUM_LONGITUDE));
					_output.flush();
				} 
				//if backend is not yet done, it will send the client connection response 
				//when it finishes initializing.
				_output.writeObject(new ServerStatus(_b.isDone()));
				_output.flush();
				
				while (_running) {
					if (!_responseQueue.isEmpty()) {
						_output.writeObject(_responseQueue.poll());
						_output.flush();
					}
				}
			} catch (IOException e) {
				if (!_running)
					Print.out("Connection closed. No more responses will be sent.");
				else
					Print.err("ERROR writing response in push thread");
			}
		}
	}
}
