package gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Collection;

import flashcard.FlashCard;

/**
 * Wrapper class used for drag and drop
 * @author samkortchmar
 *
 */
public class TransferableFlashCards implements Transferable {

	Collection<FlashCard> _cards;

	public TransferableFlashCards(Collection<FlashCard> cards) {
		_cards = cards;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{new DataFlavor(TransferableFlashCards.class, "FlashCard")};
	}

	public Collection<FlashCard> getFlashCards() {
		return _cards;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return true;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return this;
	}

}