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
 * @author samkortchmar
 *
 */
public class SetSelectionButton extends JButton implements ActionListener {

	private static final long serialVersionUID = 1L;
	JPopupMenu _menu;
	FlashCard _card;

	SetSelectionButton(String text, FlashCard card) {
		super(text);
		_menu = new JPopupMenu();
		addActionListener(this);
	}

	class SetListener implements ItemListener {
		JCheckBoxMenuItem _item;
		FlashCardSet _set;
		FlashCard _card;
		SetListener(JCheckBoxMenuItem item, FlashCardSet set, FlashCard card) {
			_item = item;
			_set = set;
			_card = card;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			try {
				if (_item.isSelected())
					_set.addCard(_card);
				else
					_set.removeCard(_card);
			} catch (IOException ioe) {
				Controller.guiMessage("Could not modify flashcard set", true);
				_item.doClick();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Collection<FlashCardSet> cardSets = _card.getSets();
		Set<String> setNames = new HashSet<>();
		for(FlashCardSet set : cardSets) {
			setNames.add(set.getName());
		}
		for(FlashCardSet set : Controller.getAllSets()) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(set.getName(), setNames.contains(set.getName()));
			_menu.add(item);
			item.addItemListener(new SetListener(item, set, _card));
		}
		_menu.show(this, 0, this.getHeight());
	}
}
