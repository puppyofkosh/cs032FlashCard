package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import utils.Writer;

/**
 * A server, listening for incoming connections and passing them
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
		Writer.out("Server running");
		while (_running) {
			try {
				Socket clientConn = _socket.accept();
				Writer.out("-- New client connection --");
				new ClientHandler(_clients, clientConn).start();
			} catch (IOException e) {
				if(e instanceof SocketException && _running == false) {
					Writer.out("The server socket has been closed.");
					return;
				}
				Writer.err("ERROR connecting to client (SERVER)");
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
		Writer.out("Killing server");
		_running = false;
		_clients.killall();
		_socket.close();
		System.exit(0);
	}
}

