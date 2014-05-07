package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import controller.Controller;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * A button that when triggered displays a list of sets. The user can opt to
 * add/remove card from sets by checking them.
 * 
 * ---
 * This class is messy in that it really has 2 functionalities.
 * When called with the (String, FlashCard) ctor, it will modify the flashcard to have whatever sets
 * the user selects
 * 
 * When called with the (String, Collection) ctor, it will add whatever sets have been selected to the Collection
 * it is passed.
 * 
 * We need to rewrite this into 2 separate classes each of which as one of the above functionalities.
 * 
 * -ian
 * ---
 * 
 * 
 * @author samkortchmar
 *
 */
public class SetSelectionButton extends JButton implements ActionListener {

	private static final long serialVersionUID = 1L;
	JPopupMenu _menu;
	FlashCard _card;
	Collection<FlashCardSet> _sets;

	SetSelectionButton(String text, FlashCard card) {
		super(text);
		_menu = new JPopupMenu();
		_card = card;
		addActionListener(this);
	}

	/**
	 * Gives a collection of sets for this button to modify based on which sets are selected
	 * @param text
	 * @param sets
	 */
	SetSelectionButton(String text, Collection<FlashCardSet> sets) {
		super(text);
		_menu = new JPopupMenu();
		_sets = sets;
		addActionListener(this);
	}

	class ModifySetListener implements ItemListener {
		JCheckBoxMenuItem _item;
		FlashCardSet _set;
		ModifySetListener(JCheckBoxMenuItem item, FlashCardSet set) {
			_item = item;
			_set = set;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			System.out.println("item changed");
			
			try {
				if (_item.isSelected()) {
					if (_card != null) {
						_set.addCard(_card);
						Controller.setDefaultInterval(_set.getInterval());
					}
					else if (_sets != null)
						Controller.setDefaultInterval(_set.getInterval());
						_sets.add(_set);
				} else {
					if (_card != null)
						_set.removeCard(_card);
					else if (_sets != null)
						_sets.remove(_set);
				}
				Controller.updateGUI(Controller.getCurrentTab());
			} catch (IOException ioe) {
				Controller.guiMessage("Could not modify flashcard set", true);
				_item.doClick();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		_menu = new JPopupMenu();
		Set<String> setNames = new HashSet<>();
		if (_card != null) {
			Collection<FlashCardSet> cardSets = _card.getSets();
			for(FlashCardSet set : cardSets) {
				setNames.add(set.getName());
			}
		}		
		for(FlashCardSet set : Controller.getAllSets()) {
			boolean checked = false;
			checked = checked || setNames.contains(set.getName()) || (_sets != null && _sets.contains(set));
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(set.getName(), checked);
			_menu.add(item);
			item.addItemListener(new ModifySetListener(item, set));
		}
		_menu.show(this, 0, this.getHeight() - 4);
	}
}
