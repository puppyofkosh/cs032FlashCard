package backend;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import controller.Controller;

import utils.TSVLineParser;
import audio.AudioFile;
import audio.MemoryAudioFile;
import audio.TextToSpeechReader;
import database.DatabaseFactory;
import flashcard.FlashCard;
import flashcard.FlashCardSet;
import flashcard.SerializableFlashCard;
import flashcard.SimpleSet;

/**
 * Turn a file, with some known formatting into a list of flsahcards
 * 
 * @author puppyofkosh
 * 
 */
public class FileImporter implements Importer {

	// Write ctor that takes filename, delimiter, and perhaps also a TTS object
	// or flashcard factory
	// FIXME: Maybe use bufferedreader not scanner

	File file;
	TextToSpeechReader ttsReader;

	List<FlashCard> cards = new ArrayList<>();

	public FileImporter(File file, TextToSpeechReader ttsReader)
			throws IOException {
		if (!file.getName().endsWith(".tsv")) {
			throw new IOException("Bad file");
		}

		this.file = file;
		this.ttsReader = ttsReader;
	}

	/**
	 * FIXME: What happens when this is called twice?
	 * 
	 * @throws IOException
	 */
	public void importCards() throws IOException {

		FlashCardSet set = new SimpleSet(file.getName());
		try (Scanner reader = new Scanner(new FileReader(file))) {
			TSVLineParser parser = new TSVLineParser(new String[] { "question",
					"answer" });

			int lineNumber = 0;
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				Map<String, String> entry = parser.read(line);

				if (entry != null && entry.get("question") != null
						&& entry.get("answer") != null) {
					// FIXME: Make these into memory audio files
					AudioFile question = ttsReader.read(entry.get("question"));
					question = new MemoryAudioFile(question.getRawBytes());

					AudioFile answer = ttsReader.read(entry.get("answer"));
					answer = new MemoryAudioFile(answer.getRawBytes());

					SerializableFlashCard.Data data = new SerializableFlashCard.Data();
					data.setAnswer(answer);
					data.setQuestion(question);

					try {
						data.name = Controller
								.parseInput(entry.get("question"));
					} catch (IOException e) {
						data.name = "Imported" + lineNumber;
					}
					// FIXME: These options should be set beforehand for the
					// FileImporter, and applied to all the flashcards
					data.interval = 5;
					data.tags = Arrays.asList();

					data.pathToFile = SerializableFlashCard
							.makeFlashCardPath(data);

					// Save the card
					FlashCard f = new SerializableFlashCard(data);
					set.addCard(f);
					// f = DatabaseFactory.writeCard(f);

					this.cards.add(f);
				}
				lineNumber++;
			}
		}

		DatabaseFactory.writeSet(set);
	}

	@Override
	public List<FlashCard> getCardList() throws IOException {
		return cards;
	}

}
