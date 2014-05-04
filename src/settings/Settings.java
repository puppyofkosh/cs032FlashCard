package settings;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import controller.Controller;

public class Settings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final File configFile = new File(".config");
	
	private static final int DEFAULT_TIMEOUT = 60;
	private static final String DEFAULT_OUTPUT = "output";
	private static final String DEFAULT_AUTHOR = "Me";
	private static final Color DEFAULT_COLOR_1 = Color.CYAN;
	private static final Color DEFAULT_COLOR_2 = Color.BLACK;
	
	private int recordingTimeout;
	private String outputDestination;
	private String defaultAuthor;
	private Color mainColor;
	private Color secondaryColor;
	
	private static Settings settings = setupSettings();
	
	
	private Settings(int recordingTimeout, String outputDestination, String defaultAuthor, Color mainColor, Color secondaryColor) {
		this.recordingTimeout = recordingTimeout;
		this.outputDestination = outputDestination;
		this.defaultAuthor = defaultAuthor;
		this.mainColor = mainColor;
		this.secondaryColor = secondaryColor;
	}
	
	private static Settings setupSettings() {
		try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(configFile))) {
			return (Settings) stream.readObject();
		} catch (ClassNotFoundException | IOException e) {
			return new Settings(DEFAULT_TIMEOUT, DEFAULT_OUTPUT, DEFAULT_AUTHOR, DEFAULT_COLOR_1, DEFAULT_COLOR_2);
		}
	}
	
	private static void saveSettings() {
		
		configFile.delete();
		
		try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(configFile))) {
			stream.writeObject(settings);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	}
	
	public static Color getSecondaryColor() {
		return settings.secondaryColor;
	}
	
	public static void setSecondaryColor(Color newValue) {
		settings.secondaryColor = newValue;
		saveSettings();
	}
}
