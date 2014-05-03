package gui;

import flashcard.FlashCard;
import flashcard.FlashCardSet;
import gui.IconFactory.IconType;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListSelectionListener;

import controller.Controller;

public class FlashboardPanel extends JPanel implements SourceListSelectionListener {

	/**
	 * FIXME:
	 * Bug! This class needs to have some way of reloading the resources when it is redisplayed
	 * _____________
	 * |____|___|  | 
	 * |____|___|  | <- Set Browser
	 * |____|___|__|
	 *     ^
	 *     |
	 * Flashcards
	 */

	private static final long serialVersionUID = 1L;
	List<JPanel> cardPanels;
	SetBrowser browser;
	JPanel flashboard;
	JPanel emptyPanel;
	JButton emptyButton;
	JLabel emptyLabel;
	private static int NUM_COLS = 3;
	private static int NUM_ROWS;

	/**
	 * Creates a new Flashboard Panel with nothing selected and 
	 * nothing displayed.
	 */
	public FlashboardPanel() {
		super(new BorderLayout(0,0));
		setOpaque(false);

		//We pass a reference to this so we can know when things are selected
		browser = new SetBrowser(this);
		add(browser, BorderLayout.EAST);

		//EMPTY PANEL
		//This panel is displayed when there are 1.) No cards in selected set
		//OR 2.) Nothing selected
		emptyPanel = new JPanel();
		emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));		
		emptyPanel.setBorder(BorderFactory.createEmptyBorder());
		emptyPanel.setOpaque(false);

		JLabel emptyLabel = new JLabel("Select from the right or");
		emptyLabel.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		emptyLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 25));
		emptyPanel.add(emptyLabel);

		emptyButton = IconFactory.createImageButton("Create New Set", IconType.SET, 32, 25);
		emptyButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		emptyPanel.add(emptyButton);

		//Contains the grid of cards to be laid out.
		flashboard = new JPanel();
		flashboard.setOpaque(false);
		flashboard.setBorder(BorderFactory.createEmptyBorder());
		JScrollPane scroller = new JScrollPane(flashboard,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setBorder(BorderFactory.createEmptyBorder());
		scroller.setOpaque(false);
		scroller.getViewport().setOpaque(false);

		add(scroller, BorderLayout.CENTER);
		clearFlashboard();
	}

	/**
	 * This effectively clears the flashboard. By providing no cards we
	 * guarantee the grid is not drawn and that the emptyPanel is shown.
	 */
	public void clearFlashboard() {
		updateFlashboard(new LinkedList<FlashCard>());
	}

	/**
	 * This updates the flashboard - loading in the cards
	 * @param cards
	 * And then redrawing the grid of cards.
	 */
	public void updateFlashboard(Collection<FlashCard> cards) {
		updateCards(cards);
		redrawGrid();
	}

	/**
	 * Should be called when we want to refresh the browser and the panel.
	 */
	public void onView() {
		SourceListItem currentItem = browser.getSourceList().getSelectedItem();
		browser.updateSourceList();
		browser.getSourceList().setSelectedItem(currentItem);
	}

	/**
	 * This draws the flashcards in a grid, or displays the empty panel if
	 * it cannot find one. Should be redone to handle resizing better.
	 */
	private void redrawGrid() {
		flashboard.removeAll();
		if (cardPanels == null || cardPanels.isEmpty()) {
			flashboard.setLayout(new GridBagLayout());
			flashboard.add(emptyPanel);
		} else {
			NUM_ROWS = cardPanels.size() / NUM_COLS + (cardPanels.size() % NUM_COLS == 0 ? 0 : 1);
			flashboard.setLayout(new GridLayout(NUM_ROWS, NUM_COLS));
			for(int i = 0; i < cardPanels.size(); i++) {
				flashboard.add(cardPanels.get(i));
			}
		}
		revalidate();
		repaint();
	}

	/**
	 * Generates cardPanels from the list of Flashcards it is passed and
	 * then adds them to the panels list.
	 * @param cards
	 */
	public void updateCards(Collection<FlashCard> cards) {
		cardPanels = new ArrayList<>();
		for(FlashCard card : cards) {
			JPanel cardPanel = new FlashCardPanel(card);
			cardPanels.add(cardPanel);
		}
	}
	
	/**
	 * When a different item is selected in the set browser, we display the
	 * cards form that particular set.
	 */
	@Override
	public void sourceListItemSelected(SourceListItem arg0) {
		try {
			FlashCardSet currentSet = browser.getSelectedSet();
			if (currentSet != null)
				updateFlashboard(currentSet.getAll());
		} catch (IOException e) {
			Controller.guiMessage("Could not get all the cards from the current set", true);
		}
	}
}