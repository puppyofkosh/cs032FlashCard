package server;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import utils.FlashcardConstants;
import utils.Print;
import flashcard.FlashCard;
import flashcard.FlashCardStub;

/**
 * A chat server, listening for incoming connections and passing them
 * off to {@link ClientHandler}s.
 */
public class Server extends Thread {

	private int _port;
	private ServerSocket _socket;
	private ClientPool _clients;
	private boolean _running;
	private volatile Map<String, FlashCard> _cardLibrary;

	/**
	 * Initialize a server on the given port. This server will not listen until
	 * it is launched with the start() method.
	 * 
	 * @param port
	 * @throws IOException
	 */
	public Server(int port) throws IOException {
		initCardLibrary();
		if (port <= 1024) {
			throw new IllegalArgumentException("Ports under 1024 are reserved!");
		}

		_port = port;
		_clients = new ClientPool();
		_socket = new ServerSocket(_port);
	}

	/**
	 * Initializes the card library with cards from file. Will probably be changed
	 * once the SQL database is up and running.
	 */
	private void initCardLibrary() {
		
		File dir = new File(FlashcardConstants.CARDS_FOLDER);
		if (_cardLibrary == null) {
			_cardLibrary = new HashMap<>();
		}
		
		String[] cardPaths = dir.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		
		for(int i = 0; i < cardPaths.length; i++) {
			FlashCard card = new FlashCardStub(cardPaths[i]);
			_cardLibrary.put(card.getName(), card);
		}
	}

	/**
	 * Wait for and handle connections indefinitely.
	 */
	public void run() {
		_running = true;
		Print.out("Server running");
		while (_running) {
			try {
				Socket clientConn = _socket.accept();
				Print.out("-- New client connection --");
				new ClientHandler(_clients, clientConn, _cardLibrary).start();
			} catch (IOException e) {
				if(e instanceof SocketException && _running == false) {
					Print.out("The server socket has been closed.");
					return;
				}
				Print.err("ERROR connecting to client (SERVER)");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Stop waiting for connections, close all connected clients, and close
	 * this server's {@link ServerSocket}.
	 * 
	 * @throws IOException if any socket is invalid.
	 */
	public void kill() throws IOException {
		Print.out("Killing server");
		_running = false;
		_clients.killall();
		_socket.close();
		System.exit(0);
	}
}

