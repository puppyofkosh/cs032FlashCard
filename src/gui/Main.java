package gui;

import java.awt.EventQueue;
import java.io.IOException;
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
				frame.add(new SetBrowser());
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public static JComponent setManagementButton() {
		for (FlashCardSet set : Controller.getAllSets()) {
			try {
				for(FlashCard card : set.getAll()) {
					return new SetSelectionButton("EXAMPLE", card);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}


	public static JComponent flashboardDemo() {
		return new FlashboardPanel();
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
			ctp.updateDisplayedCards(cards);
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