package database;

import java.io.File;
import java.io.IOException;

import backend.Resources;
import utils.FlashcardConstants;
import utils.Writer;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * Keeps around a single database for the whole application to use
 * 
 * @author puppyofkosh
 * 
 */
public class DatabaseFactory {

	private static FlashCardDatabase db = new FlashCardDatabase(
			FlashcardConstants.DB_DIR, FlashcardConstants.DB_FILE);

	public static void deleteCard(FlashCard card) {
		db.deleteCard(card);
	}

	public static FlashCard writeCard(FlashCard card) {
		return db.writeCard(card);
	}

	public static FlashCardSet writeSet(FlashCardSet s) {
		return db.writeSet(s);
	}

	public static void deleteSet(FlashCardSet s) {
		db.deleteSet(s);
	}

	public static Resources getResources() {
		return db;
	}
}
