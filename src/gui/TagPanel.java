package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class TagPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	private JTextField _inputField;
	private JLabel emptyLabel = new JLabel("This card has no tags. Add some?", JLabel.CENTER);
	List<TagLabel> _tags;

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

		initInputField();
		update();
	}

	private void initInputField() {
		if (_inputField != null)
			remove(_inputField);

		_inputField = new JTextField(7);
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

		add(_inputField);
		_inputField.setVisible(_inputField.requestFocusInWindow());
	}

	public void addTag(String tag) {
		new TagLabel(tag, this).addMouseListener(this);
		update();
	}

	private void update() {
		if (_tags.isEmpty()) {
			remove(_inputField);
			add(emptyLabel);
			add(_inputField);
		} else
			remove(emptyLabel);

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
		_inputField.requestFocusInWindow();
		update();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		_inputField.setVisible(false);
		update();
	}
}
