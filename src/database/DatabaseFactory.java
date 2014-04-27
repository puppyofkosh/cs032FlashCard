package database;

import java.io.File;
import java.io.IOException;

import backend.Resources;
import utils.FlashcardConstants;
import utils.Writer;
import flashcard.FlashCard;

public class DatabaseFactory {

	private static FlashCardDatabase db = new FlashCardDatabase(
			FlashcardConstants.DB_DIR, FlashcardConstants.DB_FILE);

	public static FlashCard writeCard(FlashCard card) {
		
		String path;
		try {
			path = card.getPath();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		if (path == null || path.isEmpty()) {
			System.out.println("Invalid path");
			return null;
		}
		// Create the directory for the card if it doesn't exist yet
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdir();
		
		try {
			// FIXME: This breaks stuff!
			Writer.writeAudioFile(path, card.getQuestionAudio().getStream(), true);
			Writer.writeAudioFile(path, card.getAnswerAudio().getStream(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return db.writeCard(card);
		
		
	}

	public static Resources getResources() {
		return db;
	}
}
