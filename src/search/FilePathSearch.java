package search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import backend.Resources;
import flashcard.FlashCard;

/**
 * Search for a card based on its path (used when the client wants a specific
 * card on the server and knows what path it is stored in on the server.) The
 * client should send a paramaterized card request with one of these, and the
 * server will give the card in that path back
 * 
 * @author puppyofkosh
 * 
 */
public class FilePathSearch implements Search, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> paths;

	/**
	 * Initialized on client side, typically
	 * 
	 * @param filePaths
	 */
	public FilePathSearch(List<String> filePaths) {
		paths = filePaths;
	}

	/**
	 * Run on server side, typically
	 */
	@Override
	public List<FlashCard> search(Resources db) {
		List<FlashCard> cards = new ArrayList<>();
		for (String path : paths) {
			cards.addAll(db.getFlashCardsByPath(path));
		}
		return cards;
	}

}
