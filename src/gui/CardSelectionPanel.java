package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;

public class CardSelectionPanel extends GenericPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtSearchField;
	
	// Name of screen to show when continue button is pushed
	private String continueDestination = "DEFAULT";

	
	/**
	 * Create the panel.
	 */
	public CardSelectionPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel searchPanel = new JPanel();
		add(searchPanel);
		
		txtSearchField = new JTextField();
		txtSearchField.setText("Search field");
		searchPanel.add(txtSearchField);
		txtSearchField.setColumns(10);
		
		String[] data = {"a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d", "a", "b", "c", "d"};
		JList<String> dataList = new JList<>(data);
		JScrollPane scrollPane = new JScrollPane(dataList);
		add(scrollPane);
		
		JButton btnContinue = new JButton("Select!");
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, continueDestination);
			}
		});
		add(btnContinue);
	}


	public String getContinueDestination() {
		return continueDestination;
	}


	public void setContinueDestination(String continueDestination) {
		this.continueDestination = continueDestination;
	}

}
