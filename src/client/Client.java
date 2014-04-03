package client;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import audio.AudioConstants;
import flashcard.FlashCardStub;

/**
 * A Client Class that sends and receives messages from and to the server.
 */
public class Client {

	private Socket _socket;
	private boolean _running;
	private int _port;
	private PrintWriter _output;
	private ObjectInputStream _cardStream;
	private ReceiveThread _thread;
	private volatile RECEIVE_MODE mode = RECEIVE_MODE.TEXT;
	private enum RECEIVE_MODE {CARD, TEXT};
	private String IP;

	/**
	 * Constructs a Client with the given port.
	 * 
	 * @param port the port number the client will connect to
	 */
	public Client(String IPAddress, int port) {
		_port = port;
		IP = IPAddress;
	}
	
	/**
	 * Starts the Client, so it connects to the sever.
	 * It will set up all the necessary requirements, 
	 * before sending and receiving messages.
	 */
	public void start() {
		try {
			_socket = new Socket(InetAddress.getByName(IP), _port);
			_cardStream = new ObjectInputStream(_socket.getInputStream());
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
				} else if (line.equalsIgnoreCase("flashcard!")) {
					out("Receive mode switched");
					this.mode = RECEIVE_MODE.CARD;
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
			_cardStream.close();
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
        			Object received = _cardStream.readObject();
        			if (mode == RECEIVE_MODE.CARD) {
        			FlashCardStub flashcard = (FlashCardStub) received;
        			if (flashcard == null) break;
        			out(flashcard.toString());
        			out("Received card: ", flashcard.getName(), "\nPlaying question: ");
        			byte[] qBytes = flashcard.getQuestionAudio().getRawBytes();
        			play(qBytes);
        			byte[] aBytes = flashcard.getAnswerAudio().getRawBytes();
        			play(aBytes);
        			mode = RECEIVE_MODE.TEXT;
        			} else if (mode == RECEIVE_MODE.TEXT) {
        				String msg = (String) received;
        				out(msg);
        			}
				} catch (IOException e) {
					if (_running == false) {
						err("Error message:", e.getMessage());
						out("Prob just closed the stream via client's kill()");
						return;
					}
					err("ERROR reading line from socket or write to STD_OUT");
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
        	}
        }
    }
	
	public void play (byte[] audioBuffer) {
		try {
        InputStream input = new ByteArrayInputStream(audioBuffer);
        final AudioFormat format = AudioConstants.TTSREADER;
        final AudioInputStream ais = new AudioInputStream(input, format, audioBuffer.length /format.getFrameSize());
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine sline = (SourceDataLine)AudioSystem.getLine(info);
        sline.open(format);
        sline.start();              
        int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
        byte buffer2[] = new byte[bufferSize];
        ais.read( buffer2, 0, buffer2.length);
        sline.write(buffer2, 0, buffer2.length);
        sline.flush();
        sline.drain();
        sline.stop();
        sline.close();  
        buffer2 = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

	}
	public void play(AudioInputStream stream) {
		try {
		Clip clip = AudioSystem.getClip();
		clip.open(stream);
		clip.start();
		} catch(Exception e) {
			e.printStackTrace();
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
