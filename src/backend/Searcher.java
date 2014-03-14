package backend;

import java.util.List;

import flashcard.FlashCard;


/***
 * Allow us to perform search on the Resources
 * 
 * ian
 * 
 * @author puppyofkosh
 *
 */
public interface Searcher {
	
	//Searcher(SearchType ...s);
	//Might use these enums:	SearchType.TAG, SearchType.NAME, SearchType.LOCAL, SearchType.DB)
	// Needs to be initialized with a Resources obj
	
	/**
	 * Grab all flashcards considered to be relevant to the query
	 * @param query
	 * @return
	 */
	List<FlashCard> search(String query);
}
