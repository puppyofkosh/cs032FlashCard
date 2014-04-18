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
import flashcard.LocallyStoredFlashCard;
import flashcard.SimpleFactory;
import flashcard.LocallyStoredFlashCard.Data;

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
					AudioFile question = ttsReader.read(entry.get("question"));
					question = new MemoryAudioFile(question.getRawBytes());

					AudioFile answer = ttsReader.read(entry.get("answer"));
					answer = new MemoryAudioFile(answer.getRawBytes());

					//FlashCard f = factory.create(entry.get("answer"), question, answer, 5, Arrays.asList("nags"), "set");
					LocallyStoredFlashCard.Data data = new LocallyStoredFlashCard.Data();
					data.answer = answer;
					data.question = question;
					
					// FIXME: For now use question as the name
					data.name = entry.get("question");
					
					// FIXME: These options should be set beforehand for the FileImporter, and applied to all the flashcards
					data.interval = 5;
					data.tags = Arrays.asList();
					data.sets = "set";
					
					data.pathToFile = LocallyStoredFlashCard.makeFlashCardPath(data);
					
					// Save the card
					FlashCard f = new LocallyStoredFlashCard(data);
					SimpleFactory.writeCard(f);
					
					this.cards.add(f);
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public List<FlashCard> getCardList() throws IOException {
		// TODO Auto-generated method stub
		return cards;
	}

}
