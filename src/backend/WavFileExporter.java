package backend;

import java.io.IOException;
import java.util.List;

import audio.WavFileConcatenator;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * Lets us export a flashcard to a single wav file in a given directory
 * 
 * @author puppyofkosh
 *
 */
public class WavFileExporter implements Exporter{
	@Override
	public void export(List<FlashCard> f) throws IOException {
		for (FlashCard card : f) {
			WavFileConcatenator.concatenate(card);
		}
	}

	@Override
	public void export(FlashCardSet s) throws IOException {
		for (FlashCard card : s.getAll()) {
			WavFileConcatenator.concatenate(card);
		}
	}
	
}
