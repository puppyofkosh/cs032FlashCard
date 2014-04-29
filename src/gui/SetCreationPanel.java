package gui;

import flashcard.FlashCardSet;
import gui.IconFactory.IconType;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListSelectionListener;

public class SetCreationPanel extends GenericPanel implements ActionListener, SourceListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField setNameField, authorTextField;
	private JSpinner spinnerInterval;
	private JButton btnContinue;
	private TagPanel tags;
	private SetBrowser setBrowser;
	private CardCreationPanel recordPanel;


	@Override
	public void setControlledPanel(JPanel panel) {
		super.setControlledPanel(panel);
		panel.add(recordPanel, "record panel");

		recordPanel.setControlledPanel(panel);
	}

	@Override
	public void setControlledLayout(CardLayout layout)	{
		super.setControlledLayout(layout);
		recordPanel.setControlledLayout(layout);
	}

	/**
	 * Create the panel.
	 */
	public SetCreationPanel() {
		setBorder(BorderFactory.createEmptyBorder());
		setLayout(new BorderLayout(0, 0));
		setOpaque(false);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder());
		mainPanel.setOpaque(false);

		
		JLabel lblAssignSet = new JLabel("Create a new set or select one from the list on the right", SwingConstants.LEADING);
		lblAssignSet.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		JPanel pane = new JPanel();
		pane.setOpaque(false);
		pane.add(lblAssignSet);
		mainPanel.add(pane);

		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		headerPanel.setOpaque(false);
		mainPanel.add(headerPanel);

		JLabel lblNewLabel = new JLabel("Set Name");
		headerPanel.add(lblNewLabel);

		setNameField = new JTextField(10);
		setNameField.addActionListener(this);
		headerPanel.add(setNameField);
		
		spinnerInterval = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
		headerPanel.add(spinnerInterval);
		
		JLabel lblAuthor = new JLabel("Author");
		headerPanel.add(lblAuthor);
		


		authorTextField = new JTextField(10);
		authorTextField.addActionListener(this);
		headerPanel.add(authorTextField);

		JPanel tagPanel = new JPanel(new BorderLayout());
		tagPanel.setOpaque(false);
		mainPanel.add(tagPanel);

		tags = new TagPanel("Add Global Tags Here");
		JScrollPane scroller = new JScrollPane(tags);
		scroller.setOpaque(false);
		scroller.getViewport().setOpaque(false);
		scroller.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		tagPanel.add(scroller, BorderLayout.CENTER);

		JPanel continuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		continuePanel.setBackground(Color.BLACK);
		btnContinue = new ImageButton("Continue to Card Creation", IconFactory.loadIcon(IconType.CARD, 36));
		btnContinue.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
		btnContinue.addActionListener(this);
		btnContinue.setBorder(BorderFactory.createEmptyBorder());
		
		continuePanel.add(btnContinue);
		mainPanel.add(continuePanel, BorderLayout.SOUTH);

		recordPanel = new CardCreationPanel();
		add(mainPanel, BorderLayout.CENTER);
		
		setBrowser = new SetBrowser(this);
		add(setBrowser, BorderLayout.EAST);
	}
	
	private void populateFields(FlashCardSet set) {
		setNameField.setText(set.getName());
		spinnerInterval.setValue(set.getInterval());
		authorTextField.setText(set.getAuthor());
		Collection<String> setTags = set.getTags();
		if (setTags == null)
			setTags = new LinkedList<>();
			
		tags.setTags(setTags);
		revalidate();
		repaint();
	}
	
	public void update() {
		setBrowser.updateSourceList();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == setNameField) {

		} else if (e.getSource() == authorTextField) {

		} else if (e.getSource() == btnContinue) {
			controlledLayout.show(controlledPanel, "record panel");
		}
	}

	@Override
	public void sourceListItemSelected(SourceListItem arg0) {
		populateFields(setBrowser.getSelectedSet());
	}
}
