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
		QUIZLET,
		SETTINGS
	}

	private static String imgFolder = "./res/img/";

	private static ImageIcon PLAY_ICON = createImageIcon("Play Button.png");
	private static ImageIcon PLAY_ICON_INVERTED = createImageIcon("Play Button Inverted.png");

	private static ImageIcon STOP_ICON = createImageIcon("Stop Button.png");
	private static ImageIcon STOP_ICON_INVERTED = createImageIcon("Stop Button Inverted.png");

	private static ImageIcon RECORD_ICON = createImageIcon("Record Button.png");
	private static ImageIcon RECORD_ICON_INVERTED = createImageIcon("Record Button Inverted.png");

	private static ImageIcon DELETE_ICON = createImageIcon("Delete Icon.png");
	private static ImageIcon DELETE_ICON_INVERTED = createImageIcon("Delete Icon Inverted.png");

	private static ImageIcon CARD_ICON = createImageIcon("Card Icon.png");
	private static ImageIcon CARD_ICON_INVERTED = createImageIcon("Card Icon Inverted.png");

	private static ImageIcon SET_ICON = createImageIcon("Set Icon.png");
	private static ImageIcon SET_ICON_INVERTED = createImageIcon("Set Icon Inverted.png");

	private static ImageIcon CREATE_ICON = createImageIcon("Create Icon.png");
	private static ImageIcon CREATE_ICON_INVERTED = createImageIcon("Create Icon Inverted.png");

	private static ImageIcon EXPORT_ICON = createImageIcon("Export Icon.png");
	private static ImageIcon EXPORT_ICON_INVERTED = createImageIcon("Export Icon Inverted.png");

	private static ImageIcon FLASHBOARD_ICON = createImageIcon("Flash Logo.png");
	private static ImageIcon FLASHBOARD_ICON_INVERTED = createImageIcon("Flash Logo Inverted.png");

	private static ImageIcon IMPORT_ICON = createImageIcon("Import Icon.png");
	private static ImageIcon IMPORT_ICON_INVERTED = createImageIcon("Import Icon Inverted.png");

	private static ImageIcon DATABASE_ICON = createImageIcon("Database Icon.png");
	private static ImageIcon DATABASE_ICON_INVERTED = createImageIcon("Database Icon Inverted.png");

	private static ImageIcon QUIZLET_ICON = createImageIcon("Quizlet Icon.png");
	private static ImageIcon QUIZLET_ICON_INVERTED = createImageIcon("Quizlet Icon Inverted.png");

	private static ImageIcon SETTINGS_ICON = createImageIcon("Settings Icon.png");
	private static ImageIcon SETTINGS_ICON_INVERTED = createImageIcon("Settings Icon Inverted.png");

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
		case STOP:
			return  inverted ? STOP_ICON_INVERTED : STOP_ICON;
		case PLAY:
			return inverted ? PLAY_ICON_INVERTED : PLAY_ICON;
		case RECORD:
			return inverted ? RECORD_ICON_INVERTED : RECORD_ICON;
		case DELETE:
			return inverted ? DELETE_ICON_INVERTED : DELETE_ICON;
		case CARD:
			return inverted ? CARD_ICON_INVERTED : CARD_ICON;
		case SET:
			return inverted ? SET_ICON_INVERTED : SET_ICON;
		case CREATE:
			return inverted ? CREATE_ICON_INVERTED : CREATE_ICON;
		case EXPORT:
			return inverted ? EXPORT_ICON_INVERTED : EXPORT_ICON;
		case FLASHBOARD:
			return inverted ? FLASHBOARD_ICON_INVERTED : FLASHBOARD_ICON;
		case IMPORT:
			return inverted ? IMPORT_ICON_INVERTED : IMPORT_ICON;
		case DATABASE:
			return inverted ? DATABASE_ICON_INVERTED : DATABASE_ICON;
		case QUIZLET:
			return inverted ? QUIZLET_ICON_INVERTED : QUIZLET_ICON;
		case SETTINGS:
			return inverted ? SETTINGS_ICON_INVERTED : SETTINGS_ICON;
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
