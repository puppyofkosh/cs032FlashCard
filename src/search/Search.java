package search;

import java.util.List;

import backend.Resources;

import database.FlashCardDatabase;
import flashcard.FlashCard;

/**
 * Interface that lets us perform a search for some cards
 * @author puppyofkosh
 *
 */
public interface Search {
	
	public List<FlashCard> search(Resources db);
}
