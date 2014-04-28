package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
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
	}
	
	private static String imgFolder = "./res/img/";

	/** Returns an ImageIcon, or null if the path was invalid. */
	private static Icon createImageIcon(String path) {
		File file = new File(path);
		if(file.exists()) {
			try {
				return new ImageIcon(ImageIO.read(file));
			} catch (IOException e) {}
		}
		//If the file doesn't exist or can't be read, we print an error message
		Controller.guiMessage("Couldn't find file: " + path, true);
		return loadMissingIcon();
	}

	public static Icon loadIcon(IconType type) {
		switch (type) {
		case CARD:
			return createImageIcon(imgFolder + "Card Icon Inverted.png");
		case DELETE:
			return createImageIcon(imgFolder + "delete x.png");
		case PLAY:
			return createImageIcon(imgFolder + "Play Button.png");
		case RECORD:
			return createImageIcon(imgFolder + "Record Button.png");
		case SET:
			return createImageIcon(imgFolder + "Set Icon Inverted.png");
		case STOP:
			return createImageIcon(imgFolder + "Stop Button.png");
		default:
			Controller.guiMessage("Not a valid icon type", true);
			return loadMissingIcon();
		}
	}

	public static Icon loadIcon(IconType type, int size) {
		Icon current = loadIcon(type);
		if (current instanceof ImageIcon) {
			Image img = ((ImageIcon) current).getImage();
			Image newimg = img.getScaledInstance(size, size,  java.awt.Image.SCALE_SMOOTH ) ;  
			current = new ImageIcon(newimg);
		} else {
			Controller.guiMessage("Could not resize icon", true);
		}
		return current;
	}

	private static Icon loadMissingIcon() {
		try {
			return IconFactory.MissingIcon.class.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}

	public class MissingIcon implements Icon{

		private int width = 32;
		private int height = 32;

		private BasicStroke stroke = new BasicStroke(4);

		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2d = (Graphics2D) g.create();

			g2d.setColor(Color.WHITE);
			g2d.fillRect(x +1 ,y + 1,width -2 ,height -2);

			g2d.setColor(Color.RED);

			g2d.setStroke(stroke);
			g2d.drawLine(x +10, y + 10, x + width -10, y + height -10);
			g2d.drawLine(x +10, y + height -10, x + width -10, y + 10);

			g2d.dispose();
		}

		public int getIconWidth() {
			return width;
		}

		public int getIconHeight() {
			return height;
		}
	}
}
