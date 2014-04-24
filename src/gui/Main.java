package gui;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import flashcard.SimpleFactory;

public class Main {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					FlashCardPanel p = new FlashCardPanel(SimpleFactory.readCard("files/Fighting Irish/"));
					frame.add(p);
//					frame.add(new ImageToggleButton(new ImageIcon("./res/img/Play Button.png"), new ImageIcon("./res/img/Stop Button.png"), "play", "stop"));
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
