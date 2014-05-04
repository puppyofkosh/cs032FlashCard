package gui;

import flashcard.FlashCardSet;
import gui.IconFactory.IconType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
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

import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListSelectionListener;

import controller.Controller;

public class SetCreationPanel extends GenericPanel implements ActionListener, SourceListSelectionListener, Browsable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField setNameField, authorTextField;
	private JSpinner spinnerInterval;
	private JButton btnContinue;
	private TagPanel tags;
	private SetBrowser _setBrowser;
	private String authorName = "";
	boolean editing;

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

		JLabel lblAssignSet = new JLabel("Create a new set or choose one to edit from the list on the right", SwingConstants.LEADING);
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

		setNameField = new JTextField(10);
		setNameField.addActionListener(this);
		headerPanel.add(setNameField);

		spinnerInterval = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
		spinnerInterval.setValue(0);
		JSpinner.DefaultEditor editor = ((JSpinner.DefaultEditor) spinnerInterval.getEditor());
		editor.getTextField().setColumns(2);
		editor.getTextField().setEditable(false);

		headerPanel.add(spinnerInterval);

		JLabel lblAuthor = new JLabel("Author");
		headerPanel.add(lblAuthor);


		authorTextField = new JTextField(10);
		authorTextField.setName(authorName);
		authorTextField.addActionListener(this);
		headerPanel.add(authorTextField);
		authorTextField.setText(Settings.getDefaultAuthor());

		JPanel tagPanel = new JPanel(new BorderLayout(0,0));
		tagPanel.setOpaque(false);
		box.add(tagPanel);

		tags = new TagPanel("Add Global Tags Here", true);
		JScrollPane scroller = new JScrollPane(tags);
		scroller.setOpaque(false);
		scroller.getViewport().setOpaque(false);
		scroller.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		tagPanel.add(scroller, BorderLayout.CENTER);

		JPanel continuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		continuePanel.setBackground(Color.BLACK);
		btnContinue = new ImageButton("Continue to Card Creation", IconFactory.loadIcon(IconType.CARD, 36, true));
		btnContinue.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
		btnContinue.addActionListener(this);

		continuePanel.add(btnContinue);

		mainPanel.add(continuePanel, BorderLayout.SOUTH);
		add(mainPanel, BorderLayout.CENTER);

	}

	private void populateFields(FlashCardSet set) {
		if (set == null)
			return;

		setNameField.setText(set.getName());
		spinnerInterval.setValue(set.getInterval());
		authorTextField.setText(set.getAuthor());
		Collection<String> setTags = set.getTags();
		if (setTags == null) {
			setTags = new LinkedList<>();
		}
		tags.setTags(setTags);
		revalidate();
		repaint();
	}

	public void update() {
		if (_setBrowser != null)
		{
			_setBrowser.updateSourceList();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == setNameField) {

		} else if (e.getSource() == authorTextField) {

		} else if (e.getSource() == btnContinue) {
			try {
				spinnerInterval.commitEdit();
			} catch (ParseException e1) {
				Controller.guiMessage("Could not parse new spinner value", true);
			}
			int interval = (int) spinnerInterval.getValue();
			String nameInput;
			try {
				nameInput = Controller.parseInput(setNameField.getText());
			} catch (IOException e1) {
				nameInput = "Set";
			}
			if (!Controller.verifyInput(nameInput))
				nameInput = Controller.parseCardName(nameInput);

			try {
				authorName = Controller.parseInput(authorTextField.getText());
			} catch (IOException e1) {
				authorName = "Anonymous";
			}

			List<String> setTags = tags.getTags(true);
			if (editing) {
				editSet(_setBrowser.getSelectedSet(), nameInput, authorName, interval, setTags);
			} else {
				newSet(nameInput, authorName, interval, setTags);
			}
			_setBrowser.updateSourceList();
		}
	}

	private void editSet(FlashCardSet set, String name, String author, int interval, List<String> tags ) {
		set.setAuthor(author);
		set.setInterval(interval);
		try {
			set.setTags(tags);
		} catch (IOException e) {
			Controller.guiMessage("Could not modify set tags", true);
		}
	}

	private void newSet(String name, String author, int interval, List<String> tags ) {
		Controller.createSet(name, author, tags, interval);
	}

	@Override
	public void sourceListItemSelected(SourceListItem arg0) {
		if (_setBrowser != null) {
			populateFields(_setBrowser.getSelectedSet());
		}
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
	 * When the window is no longer shown, don't subscribe to updates from the set browser
	 */
	@Override
	public void removeSetBrowser() {
		_setBrowser.removeParentComponent(this);
	}

}
