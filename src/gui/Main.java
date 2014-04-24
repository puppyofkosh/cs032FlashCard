package gui;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import flashcard.SimpleFactory;

public class Main {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					FlashCardPanel p = new FlashCardPanel(SimpleFactory.readCard("files/Fighting Irish/"));
					TagPanel box = new TagPanel(Arrays.asList(new String[] {"Hat", "Cat", "Dog", "Wanderlust", "Sleep"}));
					frame.add(box);
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
