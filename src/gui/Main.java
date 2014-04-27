package gui;

import java.awt.EventQueue;
import java.util.Arrays;

import javax.swing.JFrame;

public class Main {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					final TagPanel box = new TagPanel(Arrays.asList(new String[] {"Hat", "Cat", "Dog", "Wanderlust", "Sleep"}));
					
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
