package backend;

import java.util.List;

import flashcard.FlashCard;

public interface Searcher {
	
	// Needs to be initialized with a Resources obj
	
	List<FlashCard> search(String query);
}
