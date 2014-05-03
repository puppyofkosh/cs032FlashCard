package gui;

import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import controller.Controller;

public class IconFactory {

	public enum IconType {
		STOP,
		PLAY,
		RECORD,
		DELETE,
		SET,
		CARD,
		FLASHBOARD,
		EXPORT,
		IMPORT,
		CREATE,
		DATABASE,
		QUIZLET
	}

	private static String imgFolder = "./res/img/";

	/** Returns an ImageIcon, or null if the path was invalid. */
	private static ImageIcon createImageIcon(String path) {
		File file = new File(imgFolder + path);
		if(file.exists()) {
			try {
				return new ImageIcon(ImageIO.read(file));
			} catch (IOException e) {}
		}
		//If the file doesn't exist or can't be read, we print an error message
		Controller.guiMessage("Couldn't find file: " + path, true);
		return loadMissingIcon();
	}

	public static ImageIcon loadIcon(IconType type, boolean inverted) {
		switch (type) {
		case CARD:
			return createImageIcon("Card Icon" + (inverted ? " Inverted.png" : ".png"));
		case DELETE:
			return createImageIcon("delete x" + (inverted ? " Inverted.png" : ".png"));
		case PLAY:
			return createImageIcon("Play Button" + (inverted ? " Inverted.png" : ".png"));
		case RECORD:
			return createImageIcon("Record Button" + (inverted ? " Inverted.png" : ".png"));
		case SET:
			return createImageIcon("Set Icon" + (inverted ? " Inverted.png" : ".png"));
		case STOP:
			return createImageIcon("Stop Button.png"+ (inverted ? " Inverted.png" : ".png"));
		case CREATE:
			return createImageIcon("Create Icon" + (inverted ? " Inverted.png" : ".png"));
		case EXPORT:
			return createImageIcon("Export Icon" + (inverted ? " Inverted.png" : ".png"));
		case FLASHBOARD:
			return createImageIcon("Flash Logo" + (inverted ? " Inverted.png" : ".png"));
		case IMPORT:
			return createImageIcon("Import Icon" + (inverted ? " Inverted.png" : ".png"));
		case DATABASE:
			return createImageIcon("Database Icon" + (inverted ? " Inverted.png" : ".png"));
		case QUIZLET:
			return createImageIcon("Quizlet Icon" + (inverted ? " Inverted.png" : ".png"));
		default:
			Controller.guiMessage("Not a valid icon type", true);
			return loadMissingIcon();
		}
	}

	public static ImageButton createImageButton(String text, IconType type, int iconSize, int fontSize, boolean inverted) {
		ImageButton b = new ImageButton(text, loadIcon(type, iconSize, inverted));
		b.setFont(new Font(Font.MONOSPACED, Font.PLAIN, fontSize));
		return b;
	}

	public static ImageButton createImageButton(String text, IconType type, int size, boolean inverted) {
		return createImageButton(text, type, size, size * 4 / 5, inverted);
	}


	public static ImageIcon loadIcon(IconType type, int size, boolean inverted) {
		ImageIcon current = loadIcon(type, inverted);
		Image img = (current).getImage();
		Image newimg = img.getScaledInstance(size, size,  java.awt.Image.SCALE_SMOOTH ) ;  
		current = new ImageIcon(newimg);
		return current;
	}

	private static ImageIcon loadMissingIcon() {
		return createImageIcon("Missing Icon.png");
	}

}
