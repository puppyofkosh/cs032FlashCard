package backend;

import java.io.IOException;
import java.util.List;

import audio.WavFileConcatenator;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * Lets us export a flashcard to a single wav file in a given directory
 * 
 * peter
 * 
 * @author puppyofkosh
 *
 */
public class WavFileExporter implements Exporter{

	//private String destination;
	private WavFileConcatenator wavConcat;
	
	public WavFileExporter(String destination) throws IOException {
		wavConcat = new WavFileConcatenator(destination);
	}
	
	public void changeDestination(String destination) {
		wavConcat.changeDestination(destination);
	}
	
	@Override
	public void export(List<FlashCard> f) throws IOException {
		// TODO Auto-generated method stub
		for (FlashCard card : f) {
			wavConcat.concatenate(card);
		}
	}

	@Override
	public void export(FlashCardSet s) throws IOException {
		// TODO Auto-generated method stub
		for (FlashCard card : s.getAll()) {
			wavConcat.concatenate(card);
		}
	}
	
}
