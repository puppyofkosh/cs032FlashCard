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
		QUIZLET,
		DATABASE
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

	public static ImageIcon loadIcon(IconType type) {
		switch (type) {
		case CARD:
			return createImageIcon("Card Icon Inverted.png");
		case DELETE:
			return createImageIcon("delete x.png");
		case PLAY:
			return createImageIcon("Play Button.png");
		case RECORD:
			return createImageIcon("Record Button.png");
		case SET:
			return createImageIcon("Set Icon Inverted.png");
		case STOP:
			return createImageIcon("Stop Button.png");
		case CREATE:
			return createImageIcon("Create Icon Inverted.png");
		case EXPORT:
			return createImageIcon("Export Icon Inverted.png");
		case FLASHBOARD:
			return createImageIcon("Flash Logo Inverted.png");
		case IMPORT:
			return createImageIcon("Import Icon Inverted.png");
		case QUIZLET:
			return createImageIcon("Quizlet Icon Inverted.png");
		case DATABASE:
			return createImageIcon("Database Icon Inverted.png");
		default:
			Controller.guiMessage("Not a valid icon type", true);
			return loadMissingIcon();
		}
	}

	public static ImageButton createImageButton(String text, IconType type, int size) {
		ImageButton b = new ImageButton(text, loadIcon(type, size));
		b.setFont(new Font(Font.MONOSPACED, Font.PLAIN, size));
		return b;
	}

	public static ImageIcon loadIcon(IconType type, int size) {
		ImageIcon current = loadIcon(type);
		Image img = (current).getImage();
		Image newimg = img.getScaledInstance(size, size,  java.awt.Image.SCALE_SMOOTH ) ;  
		current = new ImageIcon(newimg);
		return current;
	}

	private static ImageIcon loadMissingIcon() {
		return createImageIcon("Missing Icon.png");
	}

}
