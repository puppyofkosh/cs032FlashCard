package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import flashcard.FlashCard;

/**
 * Don't think we use this - gonna throw it in deprecated and see if
 * anyone notices.
 * @author samkortchmar
 *
 */
public class CardSelectionPanel extends GenericPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtSearchField;
	private CardTablePanel cardTable;
	// Name of screen to show when continue button is pushed
	private String continueDestination = "DEFAULT";

	
	/**
	 * Create the panel.
	 * 
	 */
	public CardSelectionPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel searchPanel = new JPanel();
		add(searchPanel);
		
		txtSearchField = new JTextField();
		txtSearchField.setText("Search field");
		searchPanel.add(txtSearchField);
		txtSearchField.setColumns(10);
		
		cardTable = new CardTablePanel();
		add(cardTable);
		
		JButton btnContinue = new JButton("Select!");
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cardTable.updateSelectedCards();
				controlledLayout.show(controlledPanel, continueDestination);
				//TODO - how do we want to pass along all selected cards?
				
			}
		});
		add(btnContinue);
	}

	public String getContinueDestination() {
		return continueDestination;
	}

	private void setContinueDestination(String continueDestination) {
		this.continueDestination = continueDestination;
	}

	public void updateCards(List<FlashCard> allCards) {
		cardTable.updateCards(allCards);
	}

}
