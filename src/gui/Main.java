package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Main {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					final TagPanel box = new TagPanel(Arrays.asList(new String[] {"Hat", "Cat", "Dog", "Wanderlust", "Sleep"}));
					final JTextField test = new JTextField(20);
					
					final JPanel panel = new JPanel(new BorderLayout());
					panel.add(box, BorderLayout.CENTER);
					panel.add(test, BorderLayout.NORTH);
					frame.add(panel);

					test.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
								box.addTag(test.getText());
								test.setText("");
							}
						});
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
