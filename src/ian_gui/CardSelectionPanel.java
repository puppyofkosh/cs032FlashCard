package ian_gui;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;

public class CardSelectionPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtSearchField;

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
		
		JButton btnNewButton = new JButton("Select!");
		add(btnNewButton);

	}

}
