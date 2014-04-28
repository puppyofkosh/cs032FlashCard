package database;

import java.io.File;
import java.io.IOException;

import utils.FlashcardConstants;
import utils.Writer;
import flashcard.FlashCard;

/**
 * Uses a database to keep track of information about available cards. When
 * calling write or delete, it will write or delete from the database as well as
 * on disk
 * 
 * @author puppyofkosh
 * 
 */
public class DatabaseFactory {

	private static FlashCardDatabase db = new FlashCardDatabase(
			FlashcardConstants.DB_DIR, FlashcardConstants.DB_FILE);

	public static void deleteCard(FlashCard card) {
		try {
			String path = card.getPath();
			
			File question = new File(path + FlashcardConstants.QUESTION_FILE);
			question.delete();
			
			File answer = new File(path + FlashcardConstants.ANSWER_FILE);
			answer.delete();
			
			File dir = new File(path);
			dir.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.deleteCard(card);
	}

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
			Writer.writeAudioFile(path, card.getQuestionAudio().getStream(),
					true);
			Writer.writeAudioFile(path, card.getAnswerAudio().getStream(),
					false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return db.writeCard(card);
	}

	public static FlashCardDatabase getResources() {
		return db;
	}
}
