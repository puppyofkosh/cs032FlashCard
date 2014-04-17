package gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import backend.ResourcesStub;
import flashcard.FlashCard;
import flashcard.FlashCardStub;
import flashcard.SimpleFactory;

public class FlashboardPanel extends JPanel{

	/**
	 * FIXME:
	 * Bug! This class needs to have some way of reloading the resources when it is redisplayed
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<JPanel> cardPanels;
	private static int NUM_COLS = 3;
	private static int NUM_ROWS;
	private static String EXAMPLE_PATH = "Example FileSystem/Application Data/CARDS/ABRAHAM LINCOLN'S BIRTHDAY/";


	public FlashboardPanel(int numberOfBoxes) {
		//initCardPanels(ResourcesStub.getAllCards());
		
		// Initialize using the factory that reads from resources.ian
		initCardPanels(SimpleFactory.getStaticResources().getAllCards());
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
