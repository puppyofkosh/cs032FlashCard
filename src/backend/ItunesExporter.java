package backend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import audio.WavFileConcatenator;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * Lets us export some flashcards to an itunes library somewhere
 * 
 * Peter
 * 
 * @author puppyofkosh
 *
 */
public class ItunesExporter implements Exporter{

	// Write CTOR that takes location of an itunes library file
	// as well as the itunes music directory
	
	private WavFileConcatenator wavConcat;
	private File playlist;
	private static String itunesAddLocation;
	
	
	public ItunesExporter(File playlist) throws IOException {
		wavConcat = new WavFileConcatenator(itunesAddLocation);
		this.playlist = playlist;
	}
	 
	public ItunesExporter(String playlistLocation, String playlistName) throws IOException {
		this(new File(playlistLocation, playlistName + ".m3u"));
	}
	
	/**
	 * Use a WavExporter to turne the cards into wavs within the user's itunes directory
	 * Then, modify the itunes library to include the files we generated
	 * @throws IOException 
	 */
	@Override
	public synchronized void export(List<FlashCard> f) throws IOException {
		try (FileWriter writer = new FileWriter(playlist)) {
			for (FlashCard card : f) {
				wavConcat.concatenate(card);
				if (playlist.exists())
					writer.write("\n");
				writer.write(card.getName() + ".wav");
			}
		}
		
	}

	@Override
	public void export(FlashCardSet s) throws IOException {
		
		export(new ArrayList<>(s.getAll()));
	}

}
