package gui;

import flashcard.FlashCard;
import flashcard.FlashCardSet;
import flashcard.SerializableFlashCard;
import gui.GuiConstants.TabType;
import gui.IconFactory.IconType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListSelectionListener;

import controller.Controller;

public class FlashboardPanel extends JPanel implements SourceListSelectionListener, Browsable {

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
	private static int NUM_COLS = 3;
	private static int NUM_ROWS;

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

		JLabel emptyLabel = new JLabel("Select from the right or");
		emptyLabel.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		emptyLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 25));
		emptyPanel.add(emptyLabel);

		JButton emptyButton = IconFactory.createImageButton("Create New Set", IconType.SET, 32, 25, true);
		emptyButton.setBorderPainted(true);
		emptyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.switchTabs(TabType.CREATE);
			}
		});

		//		// Start us off with 50 panels
		//		for (int i = 0; i < 50; i++) {
		//				freePanels.add(new FlashCardPanel(SerializableFlashCard.getEmptyCard()));
		//		}

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
	}

	/**
	 * When a different item is selected in the set browser, we display the
	 * cards form that particular set.
	 */
	@Override
	public void sourceListItemSelected(SourceListItem arg0) {
		if (_setBrowser == null)
			return;
		
		try {
			FlashCardSet currentSet = _setBrowser.getSelectedSet();
			if (currentSet != null)
				updateFlashboard(currentSet.getAll());

		} catch (IOException e) {
			Controller.guiMessage("Could not get all the cards from the current set", true);
		}
	}


	/**
	 * Called every time this panel is shown
	 */
	@Override
	public void showSetBrowser(SetBrowser browser) {
		_setBrowser = browser;
		add(_setBrowser, BorderLayout.EAST);
		_setBrowser.addParentComponent(this);
		revalidate();
		repaint();		
	}

	@Override
	public void removeSetBrowser() {		
	}
}