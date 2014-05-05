package search;

import java.util.List;

import backend.AutoCorrector;
import backend.Resources;

import database.FlashCardDatabase;
import flashcard.FlashCard;

/**
 * Interface that lets us perform a search for some cards
 * @author puppyofkosh
 *
 */
public interface Search {

	// FIXME: This is a pretty shitty way of doing auto correct imo
	public static AutoCorrector corrector = new AutoCorrector();
	
	
	public List<FlashCard> search(Resources db);
}
