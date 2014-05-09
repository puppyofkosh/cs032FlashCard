package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import utils.Writer;
import flashcard.FlashCard;
import gui.IconFactory.IconType;

@SuppressWarnings("serial")
public class CardTablePanel extends JPanel {

	private List<FlashCard> cards;
	private JTable searchTable;
	private DefaultTableModel searchTableModel;

	private List<FlashCard> selectedCards = new LinkedList<>();

	CardTablePanel(String title) {
		this();
		setTitle(title);
	}
	
	public CardTablePanel(List<FlashCard> cards) {
		this();
		updateDisplayedCards(cards);
		updateTable();
	}

	/**
	 * Create the panel.
	 */
	public CardTablePanel() {
		setLayout(new BorderLayout(0,0));
		cards = new ArrayList<>();
		setOpaque(false);


		searchTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		searchTable = new JTable(searchTableModel);
		searchTable.setDragEnabled(true);
		searchTable.setDropMode(DropMode.INSERT_ROWS);
		searchTable.setTransferHandler(new SearchTableTransferHandler()); 

		searchTable.setFillsViewportHeight(true);
		searchTable.getTableHeader().setReorderingAllowed(false);
		searchTable.setGridColor(Color.BLACK);
		searchTable.setEnabled(true);
		searchTable.setAutoscrolls(true);
		JScrollPane scrollPane = new JScrollPane(searchTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);
		addMouseListener(createRightClickMenu());
		searchTable.addMouseListener(createRightClickMenu());
		updateTable();
	}

	public void setTitle(String title) {
		setBorder(new TitledBorder(BorderFactory.createEmptyBorder(), title,
				TitledBorder.LEFT, TitledBorder.TOP,
				new Font(Font.MONOSPACED, Font.PLAIN, 20),
				GuiConstants.PRIMARY_FONT_COLOR));
	}

	public List<FlashCard> getAllCards() {
		return Collections.unmodifiableList(cards);
	}

	/**
	 * Gets all cards currently selected.
	 */
	private void updateSelectedCards() {
		selectedCards = new ArrayList<>();
		for(int index : searchTable.getSelectedRows()) {
			selectedCards.add(cards.get(index));
		}
	}

	public void updateDisplayedCards(List<FlashCard> cards) {
		this.cards = cards;
		updateTable();
	}

	public List<FlashCard> getSelectedCards() {
		updateSelectedCards();
		return selectedCards;
	}

	private void updateTable() {
		if (cards == null || cards.size() == 0) {
			populateTableModel(new String[0][0], GuiConstants.DEFAULT_TABLE_COLUMNS);
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
		populateTableModel(data.toArray(new String[data.size()][]), GuiConstants.DEFAULT_TABLE_COLUMNS);
	}

	/**
	 * Creates a right click menu - contains only "Remove Cards".
	 * @return
	 */
	private PopupListener createRightClickMenu() {
		JMenuItem delete = new JMenuItem("Remove Cards", IconFactory.loadIcon(IconType.DELETE, 12, false));
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeSelectedCards();
			}
		});
		return new PopupListener(delete);
	}

	/**
	 * Removes the selected cards from the table.
	 */
	private void removeSelectedCards() {
		cards.removeAll(getSelectedCards());
		updateTable();
	}


	private void populateTableModel(String[][] data, String[] columns) {
		searchTableModel.setDataVector(data, columns);
	}

	/**
	 * Handles all drop operations into/from the table.
	 * @author samkortchmar
	 *
	 */
	private class SearchTableTransferHandler extends TransferHandler {

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		protected Transferable createTransferable(JComponent c) {
			//Attempt to get the currently selected card.
			Collection<FlashCard> cards = getSelectedCards();
			return new TransferableFlashCards(cards);
		}

		private final DataFlavor acceptedDF = 
				new DataFlavor(TransferableFlashCards.class, "FlashCard");

		/**
		 * Checks if this is a type of import we support. Currently
		 * only accepts dropping TransferableFlashCards.
		 */
		public boolean canImport(TransferSupport support) {
			// we'll only support drops (not clipboard paste)
			if (support.isDrop()) {
				for(DataFlavor df: support.getDataFlavors()) {
					return df.equals(acceptedDF);
				}
			}
			return false;
		}

		/**
		 * Handles actually importing the data.
		 */
		public boolean importData(TransferSupport support) {
			// if we can't handle the import, say so
			if (!canImport(support)) {
				return false;
			}


			// fetch the drop location
			JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();

			int row = dl.getRow();
			// fetch the data and bail if this fails
			TransferableFlashCards tfc;
			try {
				tfc = (TransferableFlashCards) support.getTransferable().getTransferData(acceptedDF);
			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				return false;
			}

			Collection<FlashCard> newCards = tfc.getFlashCards();
			//Used for handling the row placement of the cards.
			int i = 0;

			//We iterate through the collection.
			Iterator<FlashCard> itr = newCards.iterator();
			while(itr.hasNext()) {
				FlashCard newCard = itr.next();
				if (!cards.contains(newCard)) {
					cards.add(row + i, newCard);
					String[] rowData = new String[] {newCard.getName(), 
							Integer.toString(newCard.getInterval()),
							Writer.condenseCollection(newCard.getSets()),
							Writer.condenseCollection(newCard.getTags())};
					searchTableModel.insertRow(row + i, rowData);
					i++;
				}
			}


			Rectangle rect = searchTable.getCellRect(row + i, 0, false);
			if (rect != null) {
				searchTable.scrollRectToVisible(rect);
			}
			return true;
		}
	}
}
