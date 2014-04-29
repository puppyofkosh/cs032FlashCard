package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListDarkColorScheme;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.SourceListSelectionListener;

import controller.Controller;
import flashcard.FlashCard;
import flashcard.FlashCardSet;
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

	SourceList sourceList;

	/**
	 * Creates a new SetBrowser, preloaded with all the cards from the library.
	 */
	SetBrowser() {
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
		SourceListCategory setsCategory = new SourceListCategory("All Sets");
		model.addCategory(setsCategory);
		for(FlashCardSet set : Controller.getAllSets()) {
			SourceListItem setFolder = new SourceListItem(set.getName(), IconFactory.loadIcon(IconType.SET, 14));
			try {
				_sets.put(setFolder, set);
				model.addItemToCategory(setFolder, setsCategory);
				Collection<FlashCard> setCards = set.getAll();
				setFolder.setCounterValue(setCards.size());
				for(FlashCard card : setCards) {
					SourceListItem currentItem = new SourceListItem(card.getName(), IconFactory.loadIcon(IconType.CARD, 14));
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
		sourceList.setFocusable(true);
		sourceList.setColorScheme(new CustomColorScheme());
		sourceList.addSourceListSelectionListener(new SourceListSelectionListener() {
			@Override
			public void sourceListItemSelected(SourceListItem arg0) {
				if (_parentComponent != null)
					_parentComponent.sourceListItemSelected(arg0);
			}
		});


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
	public FlashCard getSelectedCard() {
		return _cards.get(sourceList.getSelectedItem());
	}

	/**
	 * Returns the selected set. If the currently selected item is a FlashCard,
	 * gets the parent set of that flashcard. 
	 * @return a FlashCardSet containing the selected item, or the currently
	 * selected FlashCardSet.
	 */
	public FlashCardSet getSelectedSet() {
		FlashCardSet set = _sets.get(sourceList.getSelectedItem());
		if (set == null) {
			set = _sets.get(_parents.get(sourceList.getSelectedItem()));
		}
		return set;
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


}


