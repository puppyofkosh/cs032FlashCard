package gui;

import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.Controller;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

public class Main {

	
	/**
	 * Currently demos drag n drop.
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				//Should really have started doing this earlier.
				dragAndDropDemo();
			}
		});
	}
	
	public static void dragAndDropDemo() {
		try {
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JPanel p = new JPanel();
			p.add(new SetBrowser());
			CardTablePanel ctp = new CardTablePanel();
			List<FlashCard> cards = new LinkedList<>();
			for(FlashCardSet set : Controller.getAllSets()) {
				cards.addAll(set.getAll());
			}
			ctp.updateCards(cards);
			p.add(ctp);
			
			JTextField area = new JTextField(10);
			area.setDragEnabled(true);
			p.add(area);
			frame.add(p);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}