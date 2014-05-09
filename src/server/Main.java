package server;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	
	// Change the port number if the port number is already being used.
	private static final int DEFAULT_PORT = 9850;
	public static void main(String[] args) throws IOException {
		// Launch a  server on the default port.
		int port = DEFAULT_PORT;
		if (args.length != 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				// Ignore it.
			}
		}

		Server server = new Server(port);
		server.start();

		// Listen for any commandline input; quit on "exit" or emptyline
		Scanner scanner = new Scanner(System.in);
		String line = null;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (line.length() == 0 || line.equalsIgnoreCase("exit")) {
				server.kill();
				scanner.close();
				System.exit(0);
			}
		}
	}
}

