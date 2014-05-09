package settings;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import utils.FlashcardConstants;
import controller.Controller;

/**
 * Class to store the settings between runs
 */
public class Settings implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final File configFile = new File(".config");

	private static final int DEFAULT_TIMEOUT = 60;
	private static final String DEFAULT_OUTPUT = "./output";
	private static final String DEFAULT_AUTHOR = "Anonymous";
	private static final Color DEFAULT_COLOR_1 = Color.CYAN;
	private static final Color DEFAULT_COLOR_2 = Color.WHITE;
	private static final String DEFAULT_HOSTNAME = FlashcardConstants.AMAZON_HOSTNAME;
	private static final String DEFAULT_PORT_NUMBER = "9888";

	private int recordingTimeout = DEFAULT_TIMEOUT;
	private String outputDestination = DEFAULT_OUTPUT;
	private String defaultAuthor = DEFAULT_AUTHOR;
	private Color mainColor = DEFAULT_COLOR_1;
	private Color secondaryColor = DEFAULT_COLOR_2;
	private String host = DEFAULT_HOSTNAME;
	private String portNumber = DEFAULT_PORT_NUMBER;

	private static Settings settings = setupSettings();

	private Settings(int recordingTimeout, String outputDestination,
			String defaultAuthor, Color mainColor, Color secondaryColor,
			String host, String portNumber) {
		this.recordingTimeout = recordingTimeout;
		this.outputDestination = outputDestination;
		this.defaultAuthor = defaultAuthor;
		this.mainColor = mainColor;
		this.secondaryColor = secondaryColor;
		this.host = host;
		this.portNumber = portNumber;
	}

	/**
	 * Tries to read the settings from the config file,
	 * and uses the default settings if it can't
	 * @return the Settings object to use
	 */
	private static Settings setupSettings() {
		try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(configFile))) {
			return (Settings) stream.readObject();
		} catch (ClassNotFoundException | IOException e) {
			return new Settings(DEFAULT_TIMEOUT, DEFAULT_OUTPUT,
					DEFAULT_AUTHOR, DEFAULT_COLOR_1, DEFAULT_COLOR_2,
					DEFAULT_HOSTNAME, DEFAULT_PORT_NUMBER);
		}
	}

	/**
	 * Saves the current settings
	 */
	private static void saveSettings() {
		configFile.delete();

		try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(configFile))) {
			stream.writeObject(settings);
		} catch (IOException e) {
			Controller.guiMessage("could not save settings", true);
		}
	}

	public static int getTimeout() {
		return settings.recordingTimeout;
	}

	public static void setTimeout(int newValue) {
		settings.recordingTimeout = newValue;
		saveSettings();
	}

	public static String getOutputDestination() {
		return settings.outputDestination;
	}

	public static void setDestination(String newValue) {
		settings.outputDestination = newValue;
		saveSettings();
	}

	public static String getDefaultAuthor() {
		return settings.defaultAuthor;
	}

	public static void setDefaultAuthor(String newValue) {
		settings.defaultAuthor = newValue;
		saveSettings();
	}

	public static Color getMainColor() {
		return settings.mainColor;
	}

	public static void setMainColor(Color newValue) {
		settings.mainColor = newValue;
		saveSettings();
		Controller.guiMessage("Color will be changed on reboot");
	}

	public static Color getSecondaryColor() {
		return settings.secondaryColor;
	}

	public static void setSecondaryColor(Color newValue) {
		settings.secondaryColor = newValue;
		saveSettings();
		Controller.guiMessage("Color will be changed on reboot");
	}

	/**
	 * @return the portNumber
	 */
	public static String getPortNumber() {
		return settings.portNumber;
	}

	/**
	 * @param portNumber the portNumber to set
	 */
	public static void setPortNumber(String portNumber) {
		settings.portNumber = portNumber;
		saveSettings();
	}

	/**
	 * @return the host
	 */
	public static String getHost() {
		return settings.host;
	}

	/**
	 * @param host the host to set
	 */
	public static void setHost(String host) {
		settings.host = host;
		saveSettings();
	}
}
