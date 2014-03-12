package backend;

import java.util.List;

import flashcard.FlashCard;
import flashcard.FlashCardSet;

public interface Resources {
	// Get names of flashcards with given tag
	List<String> getByTag(String tag);
	List<FlashCard> getByName(String name);
	
	List<FlashCard> getBySet(FlashCardSet s);
	
	List<FlashCardSet> getAllSets();
}
