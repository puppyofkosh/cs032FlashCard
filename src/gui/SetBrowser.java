package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

import com.explodingpixels.macwidgets.MacIcons;
import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListContextMenuProvider;
import com.explodingpixels.macwidgets.SourceListControlBar;
import com.explodingpixels.macwidgets.SourceListDarkColorScheme;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.SourceListSelectionListener;
import com.explodingpixels.widgets.PopupMenuCustomizer;

import controller.Controller;
import flashcard.AbstractFlashCard;
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

	//Because of the way we go about refreshing this, we need to store the
	//so we don't drop them on refresh listeners.
	private Set<SourceListSelectionListener> listeners = new HashSet<>();


	/**
	 * Adds a sourceList selection listener to the sourceList.
	 * @param pt
	 */
	public void addParentComponent(SourceListSelectionListener pt) {
		listeners.add(pt);
		sourceList.addSourceListSelectionListener(pt);
	}

	/**
	 * removes sourceList selection listener from the sourceList.
	 * @param pt
	 */
	public void removeParentComponent(SourceListSelectionListener pt) {
		listeners.remove(pt);
		sourceList.removeSourceListSelectionListener(pt);
	}

	/**
	 * Creates a new SetBrowser, with no cards or listeners initialized.
	 */
	public SetBrowser() {
		super(new BorderLayout());
		setPreferredSize(new Dimension(200, GuiConstants.HEIGHT));
		initializeSourceList();
		sourceList.setSourceListContextMenuProvider(createRightClickMenu());
	}

	SetBrowser(SourceListSelectionListener parentComponent) {
		this();
		_parentComponent = parentComponent;
	}	


	/**
	 * Refresh the sourceList when we need to. This populates it with all center.
	 */
	public void updateSourceList() {
		removeAll();
		_cards = new HashMap<>();
		_sets = new HashMap<>();
		_parents = new HashMap<>();

		SourceListModel model = sourceList.getModel();
		model.removeCategory(setsCategory);

		setsCategory = new SourceListCategory("All Sets");
		model.addCategory(setsCategory);

		ImageIcon setIcon = IconFactory.loadIcon(IconType.SET, GuiConstants.SOURCELIST_ICON_SIZE, true);
		ImageIcon cardIcon = IconFactory.loadIcon(IconType.CARD, GuiConstants.SOURCELIST_ICON_SIZE, true);

		//A very complicated looking loop that iterates through
		//All sets and their cards and adds them to the sourceListModel.
		//Should really be implemented where sets are Categories and not items
		//but we wanted to use the custom icons we made.
		for(FlashCardSet set : Controller.getAllSets()) {
			SourceListItem setFolder = new SourceListItem(set.getName(), setIcon);
			try {
				_sets.put(setFolder, set);
				model.addItemToCategory(setFolder, setsCategory);

				// Sort the cards by name
				List<FlashCard> setCards =new ArrayList<>(set.getAll());
				Collections.sort(setCards, new AbstractFlashCard.NameComparator());

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

		JComponent listPanel = sourceList.getComponent();
		add(listPanel, BorderLayout.CENTER);

		JLabel label = new JLabel("View Sets Here");
		label.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		label.setOpaque(true);
		label.setBackground(Color.BLACK);
		label.setHorizontalAlignment(SwingConstants.CENTER);

		add(label, BorderLayout.NORTH);
		revalidate();
		repaint();
	}

	/**
	 * Sets the selected item to the newIndex. More complicated than it should
	 * be because of the way the sourceList works.
	 * @param newIndex
	 */
	public void setSelection(int newIndex) {
		SourceListItem set;
		SourceListItem selected = sourceList.getSelectedItem();
		if (selected == null)
			return;
		List<SourceListItem> children = selected.getChildItems();
		if (children == null || children.isEmpty()) {
			set = _parents.get(selected);
			children = set.getChildItems();
		} else {
			set = selected;
		}
		SourceListItem newSelectedItem = children.get(newIndex);
		sourceList.setSelectedItem(newSelectedItem);
		sourceList.scrollItemToVisible(newSelectedItem);
	}

	/**
	 * Attempts to get the selected index of the current item.
	 * If its a set, it will return -1, or if nothing is selected it will
	 * return -1;
	 * @return
	 */
	public int getSelectedIndex() {
		SourceListItem parent;
		SourceListItem selected = sourceList.getSelectedItem();
		if (selected == null)
			return -1;
		List<SourceListItem> children = selected.getChildItems();
		if (children == null || children.isEmpty()) {
			parent = _parents.get(selected);
			if (parent == null)
				return -1;
			children = parent.getChildItems();
		} else {
			parent = selected;
		}
		int selectedIndex =  children.indexOf(selected);
		return selectedIndex;
	}

	/**
	 * Updates the source list with all the cards from the library.
	 * Probably not a great way to do it, but the easiest for now.
	 * FIXME: This doesn't actually "update" the list, but rather replaces it.
	 */
	public void initializeSourceList() {
		removeAll();
		_cards = new HashMap<>();
		_sets = new HashMap<>();
		_parents = new HashMap<>();
		SourceListModel model = new SourceListModel();
		sourceList = new SourceList(model);	

		for (SourceListSelectionListener l : listeners)
			sourceList.addSourceListSelectionListener(l);

		setsCategory = new SourceListCategory("All Sets");
		model.addCategory(setsCategory);
		sourceList.setExpanded(setsCategory, true);

		//Allows keyboard interaction with setbrowser.
		//Potentially want to add another keyboard interaction for 
		//expanding/collapsing items.
		sourceList.setFocusable(true);
		sourceList.setColorScheme(new CustomColorScheme());

		sourceList.installSourceListControlBar(createDefaultControlBar());

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
		updateSourceList();
	}

	/**
	 * Creates a control bar for the bottom of the source list.
	 * @return
	 */
	private SourceListControlBar createDefaultControlBar() { 
		//This control bar is on the bottom of the control bar and has an add/delete button on it.
		SourceListControlBar controlBar = new SourceListControlBar();
		controlBar.createAndAddPopdownButton(MacIcons.PLUS,
				new PopupMenuCustomizer() {
			public void customizePopup(JPopupMenu popup) {
				popup.removeAll();
				JMenuItem newCard = new JMenuItem("Create New Card", IconFactory.loadIcon(IconType.CREATE, 20, false));
				newCard.setForeground(Color.BLACK);
				newCard.setBackground(Color.BLACK);
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
				FlashCard card = getSelectedCard();
				if (card != null) {
					Controller.deleteCard(card);
					return;
				}

				FlashCardSet set = getSelectedSet();
				if (set != null) {
					Controller.deleteSet(set);
				}
			}
		});
		return controlBar;
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

	/**
	 * Returns the selected item, if it is a FlashCard. If not, returns null.
	 * @return a FlashCard corresponding to the current selected Item.
	 */
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

	private SourceListContextMenuProvider createRightClickMenu() {
		SourceListContextMenuProvider menuProvider = new SourceListContextMenuProvider() {

			@Override
			public JPopupMenu createContextMenu() {
				//If it is a category or empty space selected, do nothing.
				return null;
			}

			@Override
			public JPopupMenu createContextMenu(SourceListItem arg0) {
				sourceList.setSelectedItem(arg0);
				JPopupMenu menu = new JPopupMenu();
				JMenuItem item1 = new JMenuItem("Edit");
				menu.add(item1);
				JMenuItem item2 = new JMenuItem("Delete");
				menu.add(item2);
				item1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						FlashCard card = getSelectedCard();
						if (card != null)
							Controller.editCard(card);
						else {
							FlashCardSet set = getSelectedSet();
							if (set == null)
								return;

							Controller.editSet(set);
						}
					}
				});
				item2.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						FlashCard card = getSelectedCard();
						if (card != null)
							Controller.deleteCard(card);
						else {
							FlashCardSet set = getSelectedSet();
							if (set == null)
								return;

							Controller.deleteSet(set);

						}
					}
				});
				return menu;

			}

			@Override
			public JPopupMenu createContextMenu(SourceListCategory arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return menuProvider;
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