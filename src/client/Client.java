package client;
import gui.Frontend;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.UIManager;

import protocol.CardListResponse;
import protocol.Request;
import protocol.Response;
import utils.Writer;

/**
 * A Client Class that sends and receives messages from and to the server.
 */
public class Client extends Thread {
	

	private Socket _socket;
	private volatile boolean _running, hasServer;
	private int _port;
	private ObjectOutputStream _output;
	private ObjectInputStream _input;
	private ReceiveThread _thread;
	private String _hostName;
	private Queue<Request> _requests;
	private Frontend _frontend;
	
	/**
	 * Constructs a Client with the given port.
	 * @param port the port number the client will connect to
	 */
	public Client(String hostName, int port, Frontend frontend) {
		_port = port;
		_hostName = hostName;
		_running = false;
		_frontend = frontend;
	}

	/**
	 * Starts the Client, so it connects to the sever.
	 * It will set up all the necessary requirements, 
	 * and then launch the GUI.
	 */
	public void connect() {
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		int num_attempts = 0;
		while(!_running) {
			
			if (num_attempts > 7) {
				Writer.err("ERROR: Server unavailable. Max number of reconnection attempts reached.");
				return;
			}
			num_attempts++;
			
			try {
				//host is localhost or IP if an IP address is specified
				_socket = new Socket(_hostName, _port);
				_output = new ObjectOutputStream(_socket.getOutputStream());
				_input = new ObjectInputStream(_socket.getInputStream());
				
				_running = true;

				_requests = new LinkedList<>();

				_thread = new ReceiveThread();
				_thread.start();

				run();
			}
			catch (IOException ex) {
				Writer.out("ERROR: Can't connect to server");
				_frontend.guiMessage("Server unavailable!");
				hasServer = false;
				try {
					Thread.sleep(2500);
					_frontend.guiMessage("Attempting to reconnect...");
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					Writer.err("ERROR trying to reconnect to server");
					kill();
					return;
				}
			}
		}
	}

	public void run() {
		while (_running && !_socket.isClosed()) {
			if (!_requests.isEmpty() && hasServer) {
				try {
					Writer.debug("Sending Request");
					_output.writeObject(_requests.poll());
					_output.flush();
				} catch (IOException e) {
					Writer.out("Server closed");
					kill();
				}
			}
		}
	}

	/**
	 * Shuts down the client closing all the connections.
	 */
	public void kill() {
		Writer.out("Attempting to kill client.");
		_running = false;
		try {
			_input.close();
			_output.close();
			_socket.close();
			_thread.join();
		} catch (IOException e) {
			Writer.err("ERROR closing streams and/or socket");
		} catch (InterruptedException e) {
			Writer.err("ERROR joining receive thread");
		}
	}

	/**
	 * A method that sends a message to the server.
	 * @param message that will be sent to the server for broadcasting.
	 * @throws IOException 
	 */
	public void request(Request r) {
			_requests.add(r);
	}
	
	public boolean serverReady() {
		return hasServer;
	}
	
	/**
	 * A thread that will receive the messages sent by the server to
	 * display to the user.
	 */
	class  ReceiveThread extends Thread {
		public void run() {
			while(_running) {
				try {
					Response received = (Response) _input.readObject();
					Writer.debug("Response received");
					processResponse(received);
				} catch (IOException e) {
					if (_running == false || e instanceof EOFException) {
						Writer.out("Server has closed.");
						_frontend.guiMessage("WARNING: Server unavailable", 7);
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
	 * @param resp - the response to be processed
	 */
	public void processResponse(Response resp) {
		switch (resp.getType()) {
		case SORTED_CARDS:
			CardListResponse cLR = (CardListResponse) resp;
			cLR.getSortedCards();
			break;
		case SORTED_SETS:
			break;
		default:
			break;
		}
	}

}