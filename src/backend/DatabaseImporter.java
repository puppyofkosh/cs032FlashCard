package backend;

import java.io.IOException;
import java.util.List;

import flashcard.FlashCard;


/**
 * Import a requested list of cards (by id or name or something)
 * 
 * Implementation for quizlet will be written by Ian
 * Implementation for loading a file with a bunch of words/definitions will also be written by ian
 * 
 * @author puppyofkosh
 *
 */
public class DatabaseImporter implements Importer{

	// Ctor should take args regarding where to get the data
	
	// Not necessary?
	@Override
	public void importCards() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<FlashCard> getCardList() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
