package backend;

import java.util.List;

import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * Stores all "meta"-information about available cards and sets
 * 
 * This class is pretty core to the application. The GUI will
 * use it to display info about the cards, while the searcher will
 * also be using it
 * @author puppyofkosh
 *
 */
public interface Resources {
	// Get names of flashcards with given tag-Used by search function
	// 
	List<String> getFlashCardsByTag(String tag);
	
	/**
	 * Used by search function
	 * @param name
	 * @return
	 */
	List<FlashCard> getFlashCardsByName(String name);

	//List<FlashCard> getBySet(FlashCardSet s);
	
	/**
	 * Used for ?
	 * @param setName
	 * @return
	 */
	FlashCardSet getSetByName(String setName);
	
	
	/**
	 * Definitely used by GUI to display "library" of available sets
	 * @return
	 */
	List<FlashCardSet> getAllSets();
	
	/**
	 * Used by GUI to display "library" of available cards
	 * @return
	 */
	List<FlashCard> getAllCards();
}
// Interfaces for backend we haven't written yet:
// Recorder, TTS thing, Importer, important GUI stuff
//
//