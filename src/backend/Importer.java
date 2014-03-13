package backend;

import java.io.IOException;
import java.util.List;

import flashcard.FlashCard;

/**
 * An interface for importing cards
 * 
 * Implementations will include:
 * FileImporter -> import flashcards from a local file
 * QuizletImporter -> import flashcards from quizlet
 * DatabaseImporter -> import flashcards from online database
 */
public interface Importer {

	/**
	 * Ctor should initialize so that there is a "source"-whether it be a url or file path or whatever
	 */
	
	
	/**
	 * Imports the flashcards associated with this object into the flashcard "library"
	 */
	void importCards();

	
	/**
	 * Get the cards requested for import

	 * @return Cards requested from the source
	 * @throws IOException - error reading from source
	 */
	List<FlashCard> getCardList() throws IOException;
}
