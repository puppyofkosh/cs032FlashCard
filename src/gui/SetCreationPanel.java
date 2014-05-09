package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import settings.Settings;
import utils.Writer;

import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListSelectionListener;

import controller.Controller;
import flashcard.FlashCardSet;

public class SetCreationPanel extends GenericPanel implements ActionListener,
SourceListSelectionListener, Browsable {

	private static final long serialVersionUID = 1L;
	private JTextField _setNameField, _authorTextField;
	private JSpinner _spinnerInterval;
	private JButton _editSet, _clear, _newSet;
	private TagPanel _tags;
	private SetBrowser _setBrowser;
	private String _authorName = "";

	/**
	 * Create the panel.
	 */
	public SetCreationPanel() {
		addComponentListener(new SetBrowserComponentListener(this));

		setBorder(BorderFactory.createEmptyBorder());
		setLayout(new BorderLayout(0, 0));
		setOpaque(false);
		JPanel mainPanel = new JPanel(new BorderLayout(0,0));
		mainPanel.setOpaque(false);
		Box box = new Box(BoxLayout.Y_AXIS);
		mainPanel.add(box, BorderLayout.CENTER);

		JLabel lblAssignSet = new JLabel("Create a new set or choose one to edit", SwingConstants.LEADING);
		lblAssignSet.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		pane.setOpaque(false);
		pane.add(lblAssignSet);
		box.add(pane);

		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		headerPanel.setOpaque(false);
		box.add(headerPanel);

		JLabel lblNewLabel = new JLabel("Set Name");
		headerPanel.add(lblNewLabel);

		_setNameField = new JTextField(10);
		_setNameField.addActionListener(this);
		headerPanel.add(_setNameField);

		_spinnerInterval = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
		_spinnerInterval.setValue(0);
		JSpinner.DefaultEditor editor = ((JSpinner.DefaultEditor) _spinnerInterval.getEditor());
		editor.getTextField().setColumns(2);
		editor.getTextField().setEditable(false);

		headerPanel.add(_spinnerInterval);

		JLabel lblAuthor = new JLabel("Author");
		headerPanel.add(lblAuthor);


		_authorTextField = new JTextField(10);
		_authorTextField.addActionListener(this);
		headerPanel.add(_authorTextField);
		_authorTextField.setText(Settings.getDefaultAuthor());

		JPanel tagPanel = new JPanel(new BorderLayout(0,0));
		tagPanel.setOpaque(false);
		box.add(tagPanel);

		_tags = new TagPanel("Add Global Tags Here", true);
		JScrollPane scroller = new JScrollPane(_tags);
		scroller.setOpaque(false);
		scroller.getViewport().setOpaque(false);
		scroller.setViewportBorder(null);
		scroller.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		tagPanel.add(scroller, BorderLayout.CENTER);


		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5,0));
		buttonPanel.setBackground(GuiConstants.SET_TAG_COLOR);
		_editSet = new JButton("Edit Set");
		_editSet.setBackground(Color.BLACK);
		_editSet.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		_editSet.addActionListener(this);
		_clear = new JButton("Clear");
		_clear.setBackground(Color.BLACK);
		_clear.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		_clear.addActionListener(this);
		_newSet = new JButton("Create New Set");
		_newSet.addActionListener(this);
		_newSet.setBackground(Color.BLACK);
		_newSet.setForeground(GuiConstants.PRIMARY_FONT_COLOR);


		buttonPanel.add(_editSet);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(_clear);
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(_newSet);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		add(mainPanel, BorderLayout.CENTER);
	}

	public void populateFields(FlashCardSet set) {
		if (set == null)
			return;

		_setNameField.setText(set.getName());
		_spinnerInterval.setValue(set.getInterval());
		_authorTextField.setText(set.getAuthor());
		Collection<String> setTags = set.getTags();
		if (setTags == null) {
			setTags = new LinkedList<>();
		}
		_tags.setTags(setTags, true, true);
		revalidate();
		repaint();
	}

	public void update() {
		if (_setBrowser != null) {
			_setBrowser.updateSourceList();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _setNameField) {

		} else if (e.getSource() == _authorTextField) {

		} else if (e.getSource() == _clear) {
			clear();
		} else if (e.getSource() == _editSet || e.getSource() == _newSet) {
			try {
				_spinnerInterval.commitEdit();
			} catch (ParseException e1) {
				Controller
				.guiMessage("Could not parse new spinner value", true);
			}
			int interval = (int) _spinnerInterval.getValue();
			String nameInput;
			try {
				nameInput = Writer.parseInput(_setNameField.getText());
			} catch (IOException e1) {
				nameInput = "Set";
			}

			try {
				_authorName = Writer.parseInput(_authorTextField.getText());
			} catch (IOException e1) {
				_authorName = "Anonymous";
			}

			List<String> setTags = _tags.getTags(true);
			if (e.getSource() == _editSet) {
				editSet(_setBrowser.getSelectedSet(), nameInput, _authorName,
						interval, setTags);
			} else {
				newSet(nameInput, _authorName, interval, setTags);
			}
			_setBrowser.updateSourceList();
			_setBrowser.addParentComponent(this);
			clear();
		}
	}

	private void clear() {
		_setNameField.setText("");
		_authorTextField.setText(_authorName);
		_spinnerInterval.setValue(0);
		_tags.setTags(new LinkedList<String>(), true);
	}

	private void editSet(FlashCardSet set, String name, String author,
			int interval, List<String> tags) {
		if (set == null) {
			Controller.guiMessage("No Set Selected", true);
			return;
		}
		set.setName(name);
		set.setAuthor(author);
		set.setInterval(interval);
		try {
			set.setTags(tags);
		} catch (IOException e) {
			Controller.guiMessage("Could not modify set tags", true);
		}
	}

	private void newSet(String name, String author, int interval,
			List<String> tags) {
		Controller.createSet(name, author, tags, interval);
	}

	@Override
	public void sourceListItemSelected(SourceListItem arg0) {
		if (_setBrowser != null) {
			populateFields(_setBrowser.getSelectedSet());
		}
	}

	public void setAuthorName(String newName) {
		_authorName = newName;
		_authorTextField.setText(_authorName);
	}

	@Override
	public void showSetBrowser(SetBrowser browser) {
		_setBrowser = browser;
		_setBrowser.addParentComponent(this);
		add(_setBrowser, BorderLayout.EAST);
		revalidate();
		repaint();
	}

	/**
	 * When the window is no longer shown, don't subscribe to updates from the
	 * set browser
	 */
	@Override
	public void removeSetBrowser() {
		if (_setBrowser != null)
		{
			_setBrowser.removeParentComponent(this);
		}
	}

}
