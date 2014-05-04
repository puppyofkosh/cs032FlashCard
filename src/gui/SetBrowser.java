package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import com.explodingpixels.macwidgets.MacIcons;
import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListControlBar;
import com.explodingpixels.macwidgets.SourceListDarkColorScheme;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.SourceListSelectionListener;
import com.explodingpixels.widgets.PopupMenuCustomizer;

import controller.Controller;
import flashcard.FlashCard;
import flashcard.FlashCardSet;
import gui.GuiConstants.TabType;
import gui.IconFactory.IconType;

/**
 * This panel is responsible for displaying the sets and their cards as a
 * SourceList - it is set up for use as a sidebar
 * @author samkortchmar
 *
 */
public class SetBrowser extends JPanel  {

	private static final long serialVersionUID = 1L;

	//These maps are used for mapping items in the sourceList to the objects
	//that they represent, and for mapping child components to their parents
	//in the hierarchy.
	private Map<SourceListItem, FlashCard> _cards;
	private Map<SourceListItem, FlashCardSet> _sets;
	private Map<SourceListItem, SourceListItem> _parents;
	private SourceListSelectionListener _parentComponent;
	private SourceListCategory setsCategory;
	private SourceList sourceList;

	public void addParentComponent(SourceListSelectionListener pt)
	{
		sourceList.addSourceListSelectionListener(pt);
	}

	public void removeParentComponent(SourceListSelectionListener pt)
	{
		sourceList.removeSourceListSelectionListener(pt);
	}

	/**
	 * Creates a new SetBrowser, preloaded with all the cards from the library.
	 */
	public SetBrowser() {
		super(new BorderLayout());
		setPreferredSize(new Dimension(200, GuiConstants.HEIGHT));
		updateSourceList();
	}

	SetBrowser(SourceListSelectionListener parentComponent) {
		this();
		_parentComponent = parentComponent;
	}	

	/**
	 * Updates the source list with all the cards from the library.
	 * Probably not a great way to do it, but the easiest for now.
	 */
	public void updateSourceList() {	
		removeAll();
		_cards = new HashMap<>();
		_sets = new HashMap<>();
		_parents = new HashMap<>();
		SourceListModel model = new SourceListModel();
		sourceList = new SourceList(model);	
		setsCategory = new SourceListCategory("All Sets");
		model.addCategory(setsCategory);

		ImageIcon setIcon = IconFactory.loadIcon(IconType.SET, 14, true);
		ImageIcon cardIcon = IconFactory.loadIcon(IconType.CARD, 14, true);

		//A very complicated looking loop that basically just iterates through
		//All sets and their cards and adds them to the sourceListModel.
		//Should really be implemented where sets are Categories and not items
		//but we wanted to use the custom icons we made.
		for(FlashCardSet set : Controller.getAllSets()) {
			SourceListItem setFolder = new SourceListItem(set.getName(), setIcon);
			try {
				_sets.put(setFolder, set);
				model.addItemToCategory(setFolder, setsCategory);
				Collection<FlashCard> setCards = set.getAll();
				setFolder.setCounterValue(setCards.size());
				for(FlashCard card : setCards) {
					SourceListItem currentItem = new SourceListItem(card.getName(), cardIcon);
					_cards.put(currentItem, card);
					_parents.put(currentItem, setFolder);
					model.addItemToItem(currentItem, setFolder);
				}
				sourceList.setExpanded(setFolder, false);
			} catch (IOException e) {
				Controller.guiMessage("Could not get cards from set: " + set.getName(), true);
			}
		}

		sourceList.setExpanded(setsCategory, true);
		//Allows keyboard interaction with setbrowser.
		//Potentially want to add another keyboard interaction for 
		//expanding/collapsing items.
		sourceList.setFocusable(true);
		sourceList.setColorScheme(new CustomColorScheme());


		//This control bar is on the bottom of the control bar and has an add/delete button on it.
		SourceListControlBar controlBar = new SourceListControlBar();
		sourceList.installSourceListControlBar(controlBar);
		controlBar.createAndAddPopdownButton(MacIcons.PLUS,
				new PopupMenuCustomizer() {
			public void customizePopup(JPopupMenu popup) {
				popup.removeAll();
				JMenuItem newCard = new JMenuItem("Create New Card", IconFactory.loadIcon(IconType.CREATE, 20, false));
				newCard.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Controller.switchTabs(TabType.CARD);
					}
				});
				JMenuItem newSet = new JMenuItem("Create New Set", IconFactory.loadIcon(IconType.SET, 20, false));
				newSet.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Controller.switchTabs(TabType.SET);
					}
				});
				popup.add(newCard);
				popup.add(newSet);
			}
		});
		controlBar.createAndAddButton(IconFactory.loadIcon(IconType.DELETE, 10, false), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SourceListItem item = sourceList.getSelectedItem();
				FlashCard card = getSelectedCard();
				if (card != null) {
					Controller.deleteCard(card);
					sourceList.getModel().removeItemFromItem(item, _parents.get(item));
					return;
				}

				FlashCardSet set = getSelectedSet();
				if (set != null) {
					Controller.deleteSet(set);
					//sourceList.getModel().removeItemFromCategory(item, setsCategory);
				}
			}
		});

		//Passes the selection event to the parentComponent
		// FIXME: can we just replace this with sourceList.addSourceListSelectionListener(_parentComponent) at the beginning?
		sourceList.addSourceListSelectionListener(new SourceListSelectionListener() {
			@Override
			public void sourceListItemSelected(SourceListItem arg0) {
				if (_parentComponent != null)
					_parentComponent.sourceListItemSelected(arg0);
			}
		});

		//This handles dragging from the sourceList into another component.
		sourceList.setTransferHandler(new SourceListTransferHandler());

		JComponent listPanel = sourceList.getComponent();
		add(listPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	public SourceList getSourceList() {
		return sourceList;
	}

	/**
	 * Returns the selected item, if it is a FlashCard. If not, returns null.
	 * @return a FlashCard corresponding to the current selected Item.
	 */
	public FlashCard getSelectedCard(SourceListItem item) {
		return _cards.get(item);
	}

	public FlashCard getSelectedCard() {
		return getSelectedCard(sourceList.getSelectedItem());
	}

	/**
	 * Returns the selected set. If the currently selected item is a FlashCard,
	 * gets the parent set of that flashcard. 
	 * @return a FlashCardSet containing the selected item, or the currently
	 * selected FlashCardSet.
	 */
	public FlashCardSet getSelectedSet(SourceListItem item) {
		FlashCardSet set = _sets.get(item);
		if (set == null) {
			set = _sets.get(_parents.get(item));
		}
		return set;
	}

	public FlashCardSet getSelectedSet() {
		return getSelectedSet(sourceList.getSelectedItem());
	}

	/**
	 * A custom implementation of a color scheme for sourceList.
	 * Not really done yet.
	 * @author samkortchmar
	 *
	 */
	private class CustomColorScheme extends SourceListDarkColorScheme {

		CustomColorScheme() {
			super();
		}

		@Override
		public Color getActiveBackgroundColor() {
			return Color.BLACK;
		}

		@Override
		public Color getInactiveBackgroundColor() {
			return Color.BLACK;
		}

		@Override
		public Color getSelectedBadgeColor() {
			return Color.YELLOW;
		}
	}

	/**
	 * A custom transfer handler for dragging and dropping cards
	 * from the sourcelist. Note that it unpacks sets and drops
	 * their cards.
	 * @author samkortchmar
	 *
	 */
	private class SourceListTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		protected Transferable createTransferable(JComponent c) {
			//Attempt to get the currently selected card.
			Collection<FlashCard> cards = new LinkedList<>();
			FlashCard card = getSelectedCard();
			if (card != null) {//then a card is selected, so let's add it
				cards.add(card);
			} else { //a set is currently selected, so let's get its cards.
				FlashCardSet set = getSelectedSet();
				if (set != null) {
					try {
						cards = set.getAll();
					} catch (IOException e) {
						Controller.guiMessage("Could not get cards in the set", true);
					}
				}
			}
			return new TransferableFlashCards(cards);
		}
	}
}