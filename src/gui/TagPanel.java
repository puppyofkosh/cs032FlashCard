package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import utils.Writer;
import controller.Controller;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

public class TagPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	private JTextField _inputField;
	List<TagLabel> _tags;
	private FlashCard _card;

	private String _inputHint = "Input Tags";
	private JLabel _emptyLabel;
	private boolean _global, _inputFieldDisabled;

	TagPanel(List<String> tags, String text, boolean global) {
		super(new WrapLayout(WrapLayout.CENTER, 1, 1));

		if (text == null) text = "No Tags";
		if (tags == null) tags = new LinkedList<>();
		_global = global;

		setBorder(BorderFactory.createEmptyBorder());
		addMouseListener(this);
		setOpaque(false);
		setEmptyText(text);
		setTags(tags);
	}

	TagPanel(String text) {
		this(null, text, false);
	}
	TagPanel() {
		this(null, null, false);
	}

	TagPanel(String text, boolean global) {
		this(null, text, global);
	}

	TagPanel(FlashCard card, String text) {
		this(text);
		_card = card;
		for(String tag : _card.getTags()) {
			addTag(tag, false);
		}
		for(FlashCardSet set : _card.getSets()) {
			for(String setTag : set.getTags()) {
				addTag(setTag, true);
			}
		}
	}

	public void reinitialize(FlashCard card) {
		removeAll();
		initInputField();
		_card = card;
		_tags = new LinkedList<>();
		for(String tag : _card.getTags()) {
			addTag(tag, false);
		}
		for(FlashCardSet set : _card.getSets()) {
			for(String setTag : set.getTags()) {
				addTag(setTag, true, false);
			}
		}
		if (_tags.isEmpty())
			add(_emptyLabel);
	}

	TagPanel(FlashCard card) {
		this();
		_card = card;
		for(String tag : _card.getTags()) {
			addTag(tag, false);
		}
		for(FlashCardSet set : _card.getSets()) {
			for(String setTag : set.getTags()) {
				addTag(setTag, true, false);
			}
		}
	}

	public void disableInputField() {
		_inputFieldDisabled = true;
	}

	/**
	 * Used if we want to reinitialize with a card's data and then not have the panel edit the card anymore
	 */
	public void ignoreCard() {
		_card = null;
	}

	/**
	 * Adds the text box.
	 */
	private void initInputField() {
		if (_inputField != null)
			remove(_inputField);

		_inputField = new JTextField(7);
		_inputField.setForeground(Color.GRAY);
		_inputField.setText(_inputHint);
		_inputField.setForeground(Color.BLACK);
		_inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == _inputField) {
					addTag(_inputField.getText(), _global);
					_inputField.setText("");
				}
			}
		});
		_inputField.addMouseListener(this);
		_inputField.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				_inputField.selectAll();
				_inputField.requestFocusInWindow();
			}
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});

		add(_inputField);
		_inputField.setVisible(false);
	}

	public void setPanelType(boolean global) {
		_global = global;
	}

	public void addTag(String tag, boolean global, boolean deleteable) {
		try {
			tag = Writer.parseInput(tag);
		} catch (IOException e1) {
			Controller.guiMessage("Invalid tag: " + tag, true);

			return;
		}

		if (!_tags.contains(tag) && !tag.equals(_inputHint)) {
			try {
				if (!global)
					Controller.addTag(_card, tag);
			} catch (IOException e) {
				Controller.guiMessage("Could not add tag to card", true);
				return;
			}
			new TagLabel(tag, this, global, deleteable).addMouseListener(this);
			update();
		} else {
			Controller.guiMessage(String.format("Your input: \"%s\" is an invalid tag", tag), true);
		}
	}


	public void addTag(String tag, boolean global) {
		addTag(tag, global, true);
	}

	public void setEmptyText(String text) {
		if (_emptyLabel != null)
			remove(_emptyLabel);

		_emptyLabel = new JLabel(text);
		_emptyLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
	}

	private void update() {
		if (_tags.isEmpty()) {
			if (_inputField !=null)
				remove(_inputField);
			if (_emptyLabel != null)
				add(_emptyLabel);
			if (!_inputFieldDisabled)
				add(_inputField);
		} else {
			if (_emptyLabel != null)
				remove(_emptyLabel);
		}
		revalidate();
		repaint();
	}

	/**
	 * This gets all the tags, regardless of their global status.
	 * @return
	 */
	public List<String> getTags() {
		LinkedList<String> strings = new LinkedList<>();
		for(TagLabel tag : _tags) {
			strings.add(tag.getText());
		}
		return strings;
	}

	/**
	 * This gets all global tags or all card-specific tags, depending on the
	 * @param global - what kind of tags we want.
	 * @return
	 */
	public List<String> getTags(boolean global) {
		LinkedList<String> strings = new LinkedList<>();
		for(TagLabel tag : _tags) {
			if (tag._global == global)
				strings.add(tag.getText());
		}
		return strings;
	}


	public void setTags(Collection<String> newTags, boolean global, boolean deletable) {
		removeAll();
		_tags = new LinkedList<>();
		initInputField();
		for(String tag :  newTags) {
			addTag(tag, global, deletable);
		}
		update();
	}

	public void setTags(Collection<String> newTags) {
		setTags(newTags, false, true);
	}

	public void setTags(Collection<String> newTags, boolean global) {
		setTags(newTags, global, false);
	}


	public void clear() {
		_tags = new LinkedList<>();
		update();
	}

	public void deleteTag(TagLabel tag) {
		remove(tag);
		_tags.remove(tag);
		try {
			Controller.removeTag(_card, tag.getText());
		} catch (IOException e) {
			Controller.guiMessage("Could not remove tag from card", true);
		}
		update();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {
		if (!_inputFieldDisabled) {
			_inputField.setVisible(true);
			remove(_emptyLabel);
			revalidate();
			repaint();
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (!_inputFieldDisabled) {
			if (_inputField.isFocusOwner())
				return;
			_inputField.setVisible(false);
			update();
		}
	}
}
