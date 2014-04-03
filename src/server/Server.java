package server;
import java.io.*;
import java.net.*;

/**
 * A chat server, listening for incoming connections and passing them
 * off to {@link ClientHandler}s.
 */
public class Server extends Thread {

	private int _port;
	private ServerSocket _socket;
	private ClientPool _clients;
	private boolean _running;

	/**
	 * Initialize a server on the given port. This server will not listen until
	 * it is launched with the start() method.
	 * 
	 * @param port
	 * @throws IOException
	 */
	public Server(int port) throws IOException {
		if (port <= 1024) {
			throw new IllegalArgumentException("Ports under 1024 are reserved!");
		}
		
		_port = port;
		_clients = new ClientPool();
		
		_socket = new ServerSocket(_port);
	}

	/**
	 * Wait for and handle connections indefinitely.
	 */
	public void run() {
		_running = true;
		while (_running) {
			try {
				Socket clientConn = _socket.accept();
				out("-- New client connection --");
				new ClientHandler(_clients, clientConn).start();
			} catch (IOException e) {
				if(e instanceof SocketException && _running == false) {
					out("The server socket has been closed.");
					return;
				}
				err("ERROR connecting to client (SERVER)");
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
		out("Killing server");
		_running = false;
		_clients.killall();
		_socket.close();
		System.exit(0);
	}
	
	/* ==========================================
	 * Exxxtra meths 
	 ============================================*/
	
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

