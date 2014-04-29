package flashcard;
import java.io.IOException;
import java.util.Collection;

/**
 * sam
 * @author puppyofkosh
 *
 */
public interface FlashCardSet {
	
	public String getName();
	
	/**
	 * We can use this method to get all flash cards in the FlashCardSet.
	 * This method will be called by a wide variety of other classes - 
	 * for example, it will be used by the GUI to represent flashcards, as
	 * @return - a Collection of FlashCards.
	 */
	Collection<FlashCard> getAll() throws IOException;
	
	/**
	 * Add a card to the set
	 */
	public void addCard(FlashCard f) throws IOException;
	
	/**
	 * Adds a "global" tag to the FlashCardSet - i.e. a tag that applies for
	 * all cards in the set. This method affects the buffered FlashCardSet
	 * as well as its on-disk representation. It also adds the tag and all
	 * cards in the set to the tagIndex file.
	 * @param tag - the global tag to be added.
	 * @throws IOException - if there is trouble with the requisite on-disk files 
	 */
	void addTag(String tag) throws IOException;
	
		
	
	
	
	
	/**
	 * Removes a "global" tag from the FlashCardSet. This method affects the
	 * buffered FlashCardSet as well as its on-disk representation. It also 
	 * removes the tag and all cards in the set from the tagIndex file.
	 * @param tag - the global tag to be removed.
	 * @throws IOException - if there is trouble with the requisite on-disk files 
	 */
	void removeTag(String tag) throws IOException;
	
	/**
	 * Displays all the tags for the specific FlashCard set. Draws information
	 * exclusively from the buffered FlashCardSet
	 * @return - a list of the strings in question.
	 */
	Collection<String> getTags();
}