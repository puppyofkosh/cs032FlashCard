package backend;

import java.util.List;

import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * Lets us export some flashcards to an itunes library somewhere
 * @author puppyofkosh
 *
 */
public class ItunesExporter implements Exporter{

	// Write CTOR that takes location of an itunes library file
	// as well as the itunes music directory
	
	/**
	 * Use a WavExporter to turne the cards into wavs within the user's itunes directory
	 * Then, modify the itunes library to include the files we generated
	 */
	@Override
	public void export(List<FlashCard> f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void export(FlashCardSet s) {
		// TODO Auto-generated method stub
		
	}

}
