package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import audio.AudioFile;
import audio.TextToSpeechReader;

import util.TSVLineParser;

import flashcard.FlashCard;

/**
 * Turn a file, with some known formatting into a list of flsahcards
 * @author puppyofkosh
 *
 */
public class FileImporter implements Importer{

	// Write ctor that takes filename, delimiter, and perhaps also a TTS object
	// or flashcard factory
	// FIXME: Maybe use bufferedreader not scanner
	
	File file;
	TextToSpeechReader ttsReader;
	
	List<FlashCard> cards = new ArrayList<>();
	
	public FileImporter(File file, TextToSpeechReader ttsReader)
	{
		this.file = file;
		this.ttsReader = ttsReader;
	}
	
	public void importCards() throws IOException
	{
		
		try (Scanner reader = new Scanner(new FileReader(file)))
		{
	
			TSVLineParser parser = new TSVLineParser(new String[]{"question", "answer"});
			
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				Map<String, String> entry = parser.read(line);
				
				if (entry != null && entry.get("question") != null && entry.get("answer") != null)
				{
					AudioFile question = ttsReader.read(entry.get("question"));
					AudioFile answer = ttsReader.read(entry.get("answer"));
					
					// FIXME: Finish this, create a flashcard from it
					
				}
			}
		}
		catch(IOException e)
		{
			
		}
	}

	@Override
	public List<FlashCard> getCardList() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
