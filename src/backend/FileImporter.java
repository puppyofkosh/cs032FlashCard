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

import utils.TSVLineParser;
import client.Client;
import audio.AudioFile;
import audio.MemoryAudioFile;
import audio.TextToSpeechReader;
import flashcard.FlashCard;
import flashcard.FlashCardFactory;

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
	FlashCardFactory factory;
	
	List<FlashCard> cards = new ArrayList<>();
	
	public FileImporter(File file, TextToSpeechReader ttsReader, FlashCardFactory factory)
	{
		this.file = file;
		this.ttsReader = ttsReader;
		this.factory = factory;
	}
	
	/**
	 * FIXME: What happens when this is called twice?
	 * @throws IOException
	 */
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
					// FIXME: Make these into memory audio files
					//AudioFile question = ttsReader.read(entry.get("question"));
					//AudioFile answer = ttsReader.read(entry.get("answer"));
				
					AudioFile question = new MemoryAudioFile(ttsReader.read("Hi thar").getRawBytes());
					AudioFile answer = new MemoryAudioFile(ttsReader.read("hallo thar i am a cat").getRawBytes());
					//Client.play(question.getRawBytes());
					FlashCard f = factory.create("Generic name", question, answer, 5, Arrays.asList("nags"), "set");
					this.cards.add(f);
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
		return cards;
	}

}
