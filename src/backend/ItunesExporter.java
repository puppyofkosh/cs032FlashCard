package backend;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import audio.WavFileConcatenator;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * exports cards into a playlist
 */
public class ItunesExporter implements Exporter{
	private File playlist;
	
	/**
	 * Creates an ItunesExporter with a chosen playlist file
	 * @param playlist the file that will hold the playlist (should be .m3u)
	 */
	public ItunesExporter(File playlist) {
		this.playlist = playlist;
	}
	
	/**
	 * Creates an ItunesExporter with a playlist based on the inputs
	 * @param playlistLocation the folder to contain the playlist
	 * @param playlistName the name of the playlist
	 */
	public ItunesExporter(String playlistLocation, String playlistName) {
		this(new File(playlistLocation, playlistName + ".m3u"));
	}
	
	/**
	 * Turns the cards into wavs
	 * Then, add the exported cards to the generated playlist
	 * @throws IOException if an error in I/O occurs
	 */
	@Override
	public synchronized void export(List<FlashCard> f) throws IOException {
		try (FileWriter writer = new FileWriter(playlist)) {
			for (FlashCard card : f) {
				//adds a new line before each new file
				if (playlist.exists())
					writer.write("\n");
				// concatenate returns the file, which is used to get the absolute path
				writer.write(WavFileConcatenator.concatenate(card).getAbsolutePath());
			}
		}
	}

	@Override
	public void export(FlashCardSet s) throws IOException {
		
		export(new ArrayList<>(s.getAll()));
	}
}
