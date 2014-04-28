package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.Controller;
import flashcard.FlashCard;

public class TagPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	private JTextField _inputField;
	List<TagLabel> _tags;
	private FlashCard _card;
	
	private String inputHint = "Input Tags";
	private JLabel emptyLabel;

	TagPanel() {
		this(new LinkedList<String>());
	}

	TagPanel(List<String> tags) {
		super(new WrapLayout(WrapLayout.LEADING, 1, 1));
		addMouseListener(this);
		setOpaque(false);
		_tags = new LinkedList<>();
		for(String tag :  tags) 
			addTag(tag);
		
		setEmptyText("No Tags");

		initInputField();
		update();
	}

	TagPanel(FlashCard card) {
		super(new WrapLayout(WrapLayout.LEADING, 1, 1));
		addMouseListener(this);
		setOpaque(false);
		_card = card;
		for(String tag : _card.getTags())
			addTag(tag);
	}

	private void initInputField() {
		if (_inputField != null)
			remove(_inputField);

		_inputField = new JTextField(7);
		_inputField.setForeground(Color.GRAY);
		_inputField.setText(inputHint);
		_inputField.setForeground(Color.BLACK);
		_inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == _inputField) {
					addTag(_inputField.getText());
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
		_inputField.setVisible(_inputField.requestFocusInWindow());
	}

	public void addTag(String tag) {
		if (!_tags.contains(tag) && Controller.verifyInput(tag) && !tag.equals(inputHint)) {
			try {
				Controller.addTag(_card, tag);
			} catch (IOException e) {
				Controller.guiMessage("Could not add tag to card", true);
				return;
			}
			new TagLabel(tag, this).addMouseListener(this);
			update();
		} else {
			Controller.guiMessage(String.format("Your input: \"%s\" is an invalid tag", tag), true);
		}
	}
	
	public void setEmptyText(String text) {
		if (emptyLabel != null)
			remove(emptyLabel);
		
		emptyLabel = new JLabel(text);
		emptyLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}


	private void update() {
		if (_tags.isEmpty()) {
			remove(_inputField);
			add(emptyLabel);
			add(_inputField);
		} else {
			if (emptyLabel != null)
				remove(emptyLabel);
		}

		revalidate();
		repaint();
	}

	public List<String> getTags() {
		LinkedList<String> strings = new LinkedList<>();
		for(TagLabel tag : _tags) {
			strings.add(tag.getText());
		}
		return strings;
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
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		_inputField.setVisible(true);
		remove(emptyLabel);
		revalidate();
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		_inputField.setVisible(false);
		update();
	}
}
