package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import utils.FlashcardConstants;
import utils.Writer;
import flashcard.FlashCard;

@SuppressWarnings("serial")
public class CardTablePanel extends JPanel {

	private List<FlashCard> cards;
	private JTable searchTable;
	private DefaultTableModel searchTableModel;

	private List<FlashCard> selectedCards = new LinkedList<>();


	/**
	 * Create the panel.
	 */
	public CardTablePanel() {
		setLayout(new BorderLayout(5, 5));
		searchTableModel = new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		searchTable = new JTable(searchTableModel);
		searchTable.getTableHeader().setReorderingAllowed(false);
		searchTable.setGridColor(Color.BLACK);
		searchTable.setEnabled(true);
		searchTable.setAutoscrolls(true);
		JScrollPane scrollPane = new JScrollPane(searchTable);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);
		setBorder(BorderFactory.createRaisedSoftBevelBorder());
		updateTable();
	}

	public CardTablePanel(List<FlashCard> cards) {
		this();
		updateCards(cards);
		updateTable();
	}


	public void updateSelectedCards() {
		selectedCards = new LinkedList<>();
		for(int index : searchTable.getSelectedRows()) {
			selectedCards.add(cards.get(index));
		}
	}

	public void updateCards(List<FlashCard> cards) {
		this.cards = cards;
		updateTable();
	}

	public List<FlashCard> getSelectedCards() {
		updateSelectedCards();
		return selectedCards;
	}

	private void updateTable() {
		if (cards == null || cards.size() == 0) {
			populateTableModel(new String[0][0], FlashcardConstants.DEFAULT_TABLE_COLUMNS);
			return;
		}
		ArrayList<String[]> data = new ArrayList<>();
		Iterator<FlashCard> cardItr = cards.iterator();
		while(cardItr.hasNext()) {
			FlashCard currentCard = cardItr.next();
			data.add(new String[] {
					currentCard.getName(),
					Integer.toString(currentCard.getInterval()),
					Writer.condenseCollection(currentCard.getSets()),
					Writer.condenseCollection(currentCard.getTags())});
		}
		populateTableModel(data.toArray(new String[data.size()][]), FlashcardConstants.DEFAULT_TABLE_COLUMNS);
	}

	public void populateTableModel(String[][] data, String[] columns) {
		searchTableModel.setDataVector(data, columns);
	}
}
