package gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import backend.ResourcesStub;
import flashcard.FlashCard;
import flashcard.FlashCardStub;

@SuppressWarnings("serial")
public class FlashboardPanel extends JPanel{

	List<JPanel> cardPanels;
	private static int NUM_COLS = 3;
	private static int NUM_ROWS;
	private static String EXAMPLE_PATH = "Example FileSystem/Application Data/CARDS/ABRAHAM LINCOLN'S BIRTHDAY/";


	public FlashboardPanel(int numberOfBoxes) {
		initCardPanels(ResourcesStub.getAllCards());
		NUM_ROWS = cardPanels.size() / NUM_COLS + (cardPanels.size() % NUM_COLS == 0 ? 0 : 1);
		setLayout(new GridLayout(NUM_ROWS, NUM_COLS));
		for(int i = 0; i < cardPanels.size(); i++) {
			add(cardPanels.get(i));
		}
	}

	private void initCardPanels(List<FlashCard> cards) {
		cardPanels = new ArrayList<>();
		for(FlashCard card : cards) {
			JPanel cardPanel = new FlashCardPanel(card);
			cardPanels.add(cardPanel);
		}
	}
}
