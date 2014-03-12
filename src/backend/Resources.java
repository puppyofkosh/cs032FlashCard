package backend;

import java.util.List;

import flashcard.FlashCard;
import flashcard.FlashCardSet;

public interface Resources {
	// Get names of flashcards with given tag
	List<String> getFlashCardsByTag(String tag);
	List<FlashCard> getFlashCardsByName(String name);
	
	List<FlashCard> getBySet(FlashCardSet s);
	
	FlashCardSet getSetByName(String setName);
	
	List<FlashCardSet> getAllSets();
}
// Interfaces for backend we haven't written yet:
// Recorder, TTS thing, Importer, important GUI stuff
//
//