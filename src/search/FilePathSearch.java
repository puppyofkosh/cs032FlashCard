package search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import backend.Resources;
import flashcard.FlashCard;

/**
 * Search for a card based on its path
 * @author puppyofkosh
 *
 */
public class FilePathSearch implements Search, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> paths;
	public FilePathSearch(List<String> filePaths)
	{
		paths = filePaths;
	}
	
	@Override
	public List<FlashCard> search(Resources db) {
		List<FlashCard> cards = new ArrayList<>();
		for (String path : paths)
		{
			cards.addAll(db.getFlashCardsByPath(path));
		}
		return cards;
	}

}
