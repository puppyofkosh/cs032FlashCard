package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.Writer;

import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListSelectionListener;

import controller.Controller;
import flashcard.FlashCard;
import flashcard.FlashCardSet;
import gui.GuiConstants.TabType;
import gui.IconFactory.IconType;

public class FlashboardPanel extends JPanel implements SourceListSelectionListener, Browsable, ActionListener {

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
	List<FlashCardPanel> cardPanels = new ArrayList<>();
	SetBrowser _setBrowser;
	JPanel flashboard;
	JPanel emptyPanel;
	private static int NUM_COLS = 2;
	private static int NUM_ROWS;
	private int currentIndex;
	JButton back,next, emptyButton;

	public static final int MAX_FLASH_CARDS = 4;

	FlashCardSet currentSet;
	// Keep track of panels we've already created but wish to recycle, as creating FlashCardPanels is extremely expensive
	public Queue<FlashCardPanel> freePanels = new LinkedList<>();


	/**
	 * Creates a new Flashboard Panel with nothing selected and 
	 * nothing displayed.
	 */
	public FlashboardPanel() {
		super(new BorderLayout(0,0));
		setOpaque(false);
		addComponentListener(new SetBrowserComponentListener(this));

		//EMPTY PANEL
		//This panel is displayed when there are 1.) No cards in selected set
		//OR 2.) Nothing selected
		emptyPanel = new JPanel();
		emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));		
		emptyPanel.setBorder(BorderFactory.createEmptyBorder());
		emptyPanel.setOpaque(false);
		emptyPanel.setBackground(GuiConstants.CARD_BACKGROUND);

		JLabel emptyLabel = new JLabel("Select from the right or");
		emptyLabel.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		emptyLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 25));
		emptyPanel.add(emptyLabel);

		emptyButton = IconFactory.createImageButton("Create New Set", IconType.SET, 32, 25, true);
		emptyButton.setBorderPainted(true);
		emptyButton.addActionListener(this);
		emptyPanel.add(emptyButton);

		//Contains the grid of cards to be laid out.
		JPanel mainPanel = new JPanel(new BorderLayout(0,0));
		mainPanel.setOpaque(false);
		flashboard = new JPanel();
		flashboard.setOpaque(false);
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.add(flashboard, BorderLayout.CENTER);
		mainPanel.add(createBottomBar(), BorderLayout.SOUTH);
		clearFlashboard();
	}

	/**
	 * A useful display bar that also allows you to cycle through flash cards in a set.
	 * @return
	 */
	public JPanel createBottomBar() {
		JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 0));
		bottomBar.setBackground(GuiConstants.SET_TAG_COLOR);
		JLabel infoText = new JLabel("Browse and play your sets here");
		infoText.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		infoText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		bottomBar.add(infoText);
		back = new JButton("Back");
		back.addActionListener(this);
		back.setBackground(Color.BLACK);
		back.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		bottomBar.add(back);
		next = new JButton("Next");
		next.addActionListener(this);
		next.setBackground(Color.BLACK);
		next.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		bottomBar.add(next);

		return bottomBar;
	}

	/**
	 * This effectively clears the flashboard. By providing no cards we
	 * guarantee the grid is not drawn and that the emptyPanel is shown.
	 */
	public void clearFlashboard() {
		updateFlashboard(new LinkedList<FlashCard>());
	}

	/**
	 * This pages the flashboard forward MAX_FLASH_CARDS cards.
	 */
	public void page(boolean forward) {
		//We want to show a page containing successive cards.
		try {
			int pageIndex = currentIndex + (forward ? MAX_FLASH_CARDS : (MAX_FLASH_CARDS * -1));
			if (currentSet == null)
				return;
			int maxCards = 	currentSet.getAll().size();
			if (pageIndex < 0 || pageIndex >= maxCards) {
				pageIndex = (pageIndex % maxCards + maxCards) % maxCards;
				//Java wraps mods to negative so we need this.
			}

			_setBrowser.setSelection(pageIndex);
		} catch (IOException e) {
			Controller.guiMessage("Error paging: could not get cards", true);
		}
	}

	/**
	 * Draw the flash board such that the first thing drawn is the given card
	 * @param card
	 */
	public void updateFlashboardToShow(FlashCard card)
	{
		if (currentSet != null)
		{
			try
			{	
				int cardIndex = currentSet.getAll().indexOf(card);
				List<FlashCard> allCards = currentSet.getAll();
				List<FlashCard> toDraw = new ArrayList<>(allCards.subList(cardIndex, allCards.size()));
				// Fill the list if possible
				for (FlashCard f: allCards)
				{
					if (toDraw.size() < MAX_FLASH_CARDS && allCards.size() >= MAX_FLASH_CARDS)
						toDraw.add(f);
				}

				updateFlashboard(toDraw);
			}
			catch (IOException e)
			{
				Controller.guiMessage("Error with database: " + e.getMessage());
			}
		}
	}

	/**
	 * This updates the flashboard - loading in the cards
	 * @param cards
	 * And then redrawing the grid of cards.
	 */
	public void updateFlashboard(List<FlashCard> cards) {

		Collection<FlashCard> toDraw = null;
		if (cards.size() > MAX_FLASH_CARDS)
		{
			toDraw = cards.subList(0, MAX_FLASH_CARDS);
		}
		else
		{
			toDraw = cards;
		}
		updateCards(toDraw);

		redrawGrid();
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
		// All the panels being used before are now up for grabs and can be recycled
		freePanels.addAll(cardPanels);
		cardPanels = new ArrayList<>();
		for(FlashCard card : cards) {
			// Try to use a recycled panel if any are available. If not, we must create a new panel ourselves.
			FlashCardPanel cardPanel = null;
			if (freePanels.size() > 0) {
				cardPanel = freePanels.poll();
				cardPanel.reinitialize(card);
			}
			else {
				cardPanel = new FlashCardPanel(card);
			}
			cardPanels.add(cardPanel);
		}
	}

	public void update() {
		if (_setBrowser != null)
			_setBrowser.updateSourceList();
		try {
			if (currentSet != null)
				updateFlashboard(currentSet.getAll());
		} catch (IOException e) {
			e.printStackTrace();
			Controller.guiMessage("Could not read cards from set", true);
		}
	}

	/**
	 * When a different item is selected in the set browser, we display the
	 * cards form that particular set.
	 */
	@Override
	public void sourceListItemSelected(SourceListItem arg0) {
		try {
			if (_setBrowser != null)
			{
				currentIndex = _setBrowser.getSelectedIndex();
				Writer.out(currentIndex);
				// The SetBrowser doesn't always have a selected set (even if the flashboard is displaying the cards for a certain set)
				// We don't want to overwrite currentSet unless if we know the new value won't be null
				FlashCardSet selectedSet = _setBrowser.getSelectedSet();
				// selectedSet != currentSet => Make sure we don't call update unless the set has changed
				if (selectedSet != null && selectedSet != currentSet)
				{
					currentSet = selectedSet;
					updateFlashboard(currentSet.getAll());
				}

				FlashCard selectedCard = _setBrowser.getSelectedCard();
				if (selectedCard != null)
				{
					updateFlashboardToShow(selectedCard);
				}
			}
		} catch (IOException e) {
			Controller.guiMessage("Could not get all the cards from the current set", true);
		}
	}

	@Override
	public void showSetBrowser(SetBrowser browser) {
		_setBrowser = browser;
		_setBrowser.addParentComponent(this);
		add(_setBrowser, BorderLayout.EAST);
		revalidate();
		repaint();
	}

	@Override
	public void removeSetBrowser() {
		_setBrowser = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == back) {
			page(false);
		} else if (e.getSource() == next) {
			page(true);
		} else if (e.getSource() == emptyButton) {
			Controller.switchTabs(TabType.SET);
		}
	}
}