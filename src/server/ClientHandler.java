package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import flashcard.FlashCardStub;

/**
 * Encapsulate IO for the given client {@link Socket}, with a group of
 * other clients in the given {@link ClientPool}.
 */
public class ClientHandler extends Thread {
	private ClientPool _pool;
	private Socket _client;
	private BufferedReader _input;
	private ObjectOutputStream _cardStream;
	private boolean _running;
	private String FILEPATH = "./Example FileSystem/Application Data/CARDS/ABRAHAM LINCOLN'S BIRTHDAY/";
	
	/**
	 * Constructs a {@link ClientHandler} on the given client with the given pool.
	 * 
	 * @param pool a group of other clients to chat with
	 * @param client the client to handle
	 * @throws IOException if the client socket is invalid
	 * @throws IllegalArgumentException if pool or client is null
	 */
	public ClientHandler(ClientPool pool, Socket client) throws IOException {
		if (pool == null || client == null) {
			throw new IllegalArgumentException("Cannot accept null arguments.");
		}
		
		_pool = pool;
		_client = client;
		_pool.add(this);
		_input = new BufferedReader(new InputStreamReader(_client.getInputStream()));
		_cardStream = new ObjectOutputStream(_client.getOutputStream());
	}
	
	/**
	 * Send and receive data from the client. The first line received will be
	 * interpreted as the client's user-name.
	 */
	public void run() {
		String user;
		_running = true;
		try {
			user = _input.readLine();
			_pool.broadcast("-- User " + user + " logged in. --", this);
			while (_running) {
				String msg = _input.readLine();
				if (msg == null || msg.length() == 0 || msg.equalsIgnoreCase("logoff")) {
					break;
				} else if (msg.equalsIgnoreCase("flashcard!")) {
					FlashCardStub flashcard = new FlashCardStub(FILEPATH);
					_cardStream.writeObject(flashcard);
					_cardStream.flush();
				}
				_pool.broadcast(user + ": " + msg, this);
			}
			_pool.broadcast("System: User: " + user + " logged off.", this);
			send("");
			kill();
		} catch (IOException e) {
			err("ERROR reading from client");
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a string to the client via the socket
	 * 
	 * @param message text to send
	 */
	public void send(String message) {
		try {
			_cardStream.writeObject(message);
			_cardStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Close this socket and its related streams.
	 * 
	 * @throws IOException Passed up from socket
	 */
	public void kill() throws IOException {
		out("Killing Client Handler");
		_running = false;
		_input.close();
		_cardStream.close();
		_client.close();
		_pool.remove(this); //remove this from the pool since we are killing it.
	}
	
	
	/**	
	 * Utilities for printing.
	 * @param strs
	 */
		void out(Object...strs) {
			System.out.println(composeString(strs));
		}
		
		void err(Object...strs) {
			System.err.println(composeString(strs));
		}
		
		String composeString(Object...strs) {
			String s = "" + strs[0];
			for (int i = 1; i < strs.length; i++) {
				s += (strs[i] +" ");
			}
			return s;
		}
}