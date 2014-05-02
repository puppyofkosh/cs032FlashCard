package gui;

import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.Controller;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

public class Main {

	
	/**
	 * Currently demos export
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.add(autocorrectDemo());
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	public static JComponent autocorrectDemo() {
		JPanel p = new JPanel();
		Controller.loadAutoCorrectTree();
		p.add(new AutoFillField("TYPE HERE", 1));
		return p;
	}
	
	public static JComponent exportDemo() {
		return new ExportPanel();
	}
	
	public static JComponent dragAndDropDemo() {
		try {
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
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}