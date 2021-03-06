package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import protocol.CardListResponse;
import protocol.MetaDataCardRequest;
import protocol.MetaDataResponse;
import protocol.NetworkedFlashCard;
import protocol.ParametrizedCardRequest;
import protocol.Request;
import protocol.Response;
import protocol.UploadCardsRequest;
import protocol.UploadCardsResponse;
import utils.Writer;
import database.DatabaseFactory;
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
	public ClientHandler(ClientPool pool, Socket clientSocket) throws IOException {
		if (pool == null || clientSocket == null)
			throw new IllegalArgumentException("Cannot accept null arguments.");

		_client = clientSocket;
		_pool = pool;

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
			Writer.out("Running has ceased. Goodbye.");
		} catch(IOException | ClassNotFoundException e) {
			Writer.out("-- Client exited. --");
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
		Writer.out("Processing Request...\n");
		switch (req.getType()) {
		case CARD_LIST:
		{
			ParametrizedCardRequest clR = (ParametrizedCardRequest) req;
			List<FlashCard> cards = clR.getSearchParameters().search(DatabaseFactory.getResources());
			System.out.println(cards + " matched search");
			respond(new CardListResponse(cards));
			return;
		}
		case ALL:
			Writer.out("Returning all cards");
			respond(new CardListResponse(DatabaseFactory.getResources().getAllCards()));
			return;
		case SET_LIST:
			break;
		case UPLOAD:
			Writer.out("Cards being uploaded");
			UploadCardsRequest ulR = (UploadCardsRequest) req;
			for(FlashCard  card : ulR.getCardsForUpload()) {
				Writer.out("Writing card - " + card.getName());

				card = DatabaseFactory.writeCard(card);

				Writer.out("Adding card to library " +  card.getName());
			}
			Writer.out("Done");
			respond(new UploadCardsResponse(true));
			return;
		case ALL_META_DATA:
			Writer.out("ALL Meta data request");
			List<NetworkedFlashCard> cardData = new ArrayList<>();
			for (FlashCard f: DatabaseFactory.getResources().getAllCards())
			{
				try {
					cardData.add(new NetworkedFlashCard(f, f.getPath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println(cardData.size());
			respond(new MetaDataResponse(cardData));
			return;
		case PARAMATERIZED_META_DATA:
		{
			Writer.out("ALL Meta data request");
			MetaDataCardRequest clR = (MetaDataCardRequest) req;
			List<FlashCard> cards = clR.getSearchParameters().search(DatabaseFactory.getResources());
			List<NetworkedFlashCard> networkedCards = new ArrayList<>();
			for (FlashCard f : cards)
			{
				try {
					networkedCards.add(new NetworkedFlashCard(f, f.getPath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			System.out.println(cards + " matched search");
			respond(new MetaDataResponse(networkedCards));
			return;
		}
		default:
			//not much we can do with an invalid request
			throw new IllegalArgumentException("Unsupported request type");
		}
	}

	private void respond(Response r) {
		Writer.out("Responding");
		_responseQueue.add(r);
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
			Writer.err("ERROR killing client handler.\n", e.getMessage());	
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
				while (_running) {
					if (!_responseQueue.isEmpty()) {
						_output.writeObject(_responseQueue.poll());
						_output.flush();
					}
				}
			} catch (IOException e) {
				if (!_running)
					Writer.out("Connection closed. No more responses will be sent.");
				else
					Writer.err("ERROR writing response in push thread");
			}
		}
	}
}
