package gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import database.DatabaseFactory;
import flashcard.FlashCard;

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


	public FlashboardPanel() {
		super();
		// Initialize using the factory that reads from resources.ian
		updateFlashboard();
		setOpaque(true);
		setBackground(GuiConstants.CARD_BACKGROUND);
	}
	
	public void updateFlashboard() {
		updateFlashboard(DatabaseFactory.getResources().getAllCards());
	}

	public void updateFlashboard(List<FlashCard> cards) {
		updateCards(cards);
		redrawGrid();
	}
	
	private void redrawGrid() {
		removeAll();
		NUM_ROWS = cardPanels.size() / NUM_COLS + (cardPanels.size() % NUM_COLS == 0 ? 0 : 1);
		setLayout(new GridLayout(NUM_ROWS, NUM_COLS));
		setBorder(BorderFactory.createEmptyBorder());
		for(int i = 0; i < cardPanels.size(); i++) {
			add(cardPanels.get(i));
		}
		revalidate();
		repaint();
	}
	
	public void updateCards(List<FlashCard> cards) {
		cardPanels = new ArrayList<>();
		for(FlashCard card : cards) {
			JPanel cardPanel = new FlashCardPanel(card);
			cardPanels.add(cardPanel);
		}
	}
}
