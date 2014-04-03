package client;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * A Client Class that sends and receives messages from and to the server.
 */
public class Client {

	private Socket _socket;
	private boolean _running;
	private int _port;
	private BufferedReader _input;
	private PrintWriter _output;
	private ReceiveThread _thread;

	/**
	 * Constructs a Client with the given port.
	 * 
	 * @param port the port number the client will connect to
	 */
	public Client(int port) {
		_port = port;
	}
	
	/**
	 * Starts the Client, so it connects to the sever.
	 * It will set up all the necessary requirements, 
	 * before sending and receiving messages.
	 */
	public void start() {
		try {
			_socket = new Socket("localhost", _port);
			_input = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
			_output = new PrintWriter(_socket.getOutputStream(), true);
			_running = true;
			run();
		}
		catch (IOException ex) {
			err("ERROR: Can't connect to server");
		}
	}

	/**
	 * Starts a thread that will listen to messages sent by the
	 * server. It will use the main thread to send messages to the server.
	 */
	private void run() {
		// Listen for any commandline input; quit on "exit" or emptyline
		_thread = new ReceiveThread();
		_thread.start();
		Scanner scanner = new Scanner(System.in);
		String line = "";
		while (scanner.hasNextLine() && !_socket.isClosed()) {
			line = scanner.nextLine();
			try {
				if (line.length() == 0 || line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("logoff")) {
					out("Command to exit received.");
					send("");
					break;
				}
				send(line);
			} catch (IOException e) {
				err("Could not send input to client handler");
				e.printStackTrace();
			}
		}
		scanner.close();
		this.kill();
	}

	/**
	 * A method that sends a message to the server.
	 * 
	 * @param message that will be sent to the server for broadcasting.
	 * @throws IOException 
	 */
	public void send(String message) throws IOException {
		//out("Sending message: "+message);
		_output.println(message);
		_output.flush();
	}

	/**
	 * Shuts down the client closing all the connections.
	 */
	public void kill() {
		out("Attempting to kill client.");
		_running = false;
		try {
			_input.close();
			_output.close();
			_socket.close();
			_thread.join();
		} catch (IOException | InterruptedException e) {
			if (e instanceof IOException) err("ERROR closing streams and/or socket");
			else err("ERROR joining receive thread");
			e.printStackTrace();
		}
	}

	/**
	 * A thread that will receive the messages sent by the server to
	 * display to the user.
	 */
	class  ReceiveThread extends Thread {
        public void run() {
        	while(_running) {
        		try {
        			String str = _input.readLine();
        			if (str == null || str.length() == 0) break;
        			//out("Message received!");
        			out(str);
				} catch (IOException e) {
					if (_running == false) {
						err("Error message:", e.getMessage());
						out("Prob just closed the stream via client's kill()");
						return;
					}
					err("ERROR reading line from socket or write to STD_OUT");
					e.printStackTrace();
				}
        	}
        }
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
