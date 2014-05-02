package search;

import java.io.Serializable;
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
	private String path = "";
	public FilePathSearch(String filePath)
	{
		path = filePath;
	}
	
	@Override
	public List<FlashCard> search(Resources db) {
		return db.getFlashCardsByPath(path);
	}

}
