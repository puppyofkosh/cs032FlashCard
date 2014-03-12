package backend;

import java.util.List;

import flashcard.FlashCard;
import flashcard.FlashCardSet;

public interface Exporter {
	
	// Exporter e = new ItunesExporter(myituneslibrary place)
	// Exporter e = new NetworkExporter(url)
	// Exporter e = new FlatFileExporter(folder)
	
	void export(List<FlashCard> f);
	void export(FlashCardSet s);
}
