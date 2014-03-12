package backend;

import java.util.List;

import flashcard.FlashCard;
import flashcard.FlashCardSet;


/**
 * "Exports" flashcards to a given media/destination
 * Implementers will be (at least):
 * FlatFileExporter -> Put flashcards into single mp3 files
 * DatabaseExporter -> Put flashcard into given database
 * ItunesExporter -> Export file to mp3 and put in an itunes lib
 * 
 * @author puppyofkosh
 *
 */
public interface Exporter {
	
	// Exporters should be initialized with "where" the things
	// they'll be exporting will go
	// Exporter e = new ItunesExporter(myituneslibrary place)
	// Exporter e = new NetworkExporter(url type info)
	// Exporter e = new FlatFileExporter(folder)
	
	/**
	 * Export a bunch of flashcards to given destination. This would
	 * be called when a user is in the export window/workflow
	 * @param f
	 */
	void export(List<FlashCard> f);
	
	/**
	 * Export a set of flashcards-also used while in the export
	 * window/workflow
	 * @param s
	 */
	void export(FlashCardSet s);
}
