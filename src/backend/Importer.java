package backend;

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
	 * Imports the flashcards associated with this object into the flashcard "library"
	 */
	void importCards();
}
