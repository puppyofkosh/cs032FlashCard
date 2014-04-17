package gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import flashcard.FlashCardStub;

public class FlashboardPanel extends JPanel{

	List<JPanel> cardPanels;
	private static int NUM_COLS = 3;
	private static int NUM_ROWS;
	private static String EXAMPLE_PATH = "Example FileSystem/Application Data/CARDS/ABRAHAM LINCOLN'S BIRTHDAY/";


	public FlashboardPanel(int numberOfBoxes) {
		initCardPanels(numberOfBoxes);
		NUM_ROWS = cardPanels.size() / NUM_COLS + (cardPanels.size() % NUM_COLS == 0 ? 0 : 1);
		setLayout(new GridLayout(NUM_ROWS, NUM_COLS));
		for(int i = 0; i < cardPanels.size(); i++) {
			add(cardPanels.get(i));
		}
	}

	private void initCardPanels(int num) {
		cardPanels = new ArrayList<>(num);
		for(int i = 0; i < num; i++) {
			JPanel card = new FlashCardPanel(new FlashCardStub(EXAMPLE_PATH));
			cardPanels.add(card);
		}
	}
}
