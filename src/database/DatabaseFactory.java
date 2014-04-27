package database;

import backend.Resources;
import utils.FlashcardConstants;
import flashcard.FlashCard;

public class DatabaseFactory{

	private static FlashCardDatabase db = new FlashCardDatabase(FlashcardConstants.DB_DIR, FlashcardConstants.DB_FILE);
	
	public static FlashCard writeCard(FlashCard card) {
		return db.writeCard(card);
	}
	
	public static Resources getResources()
	{
		return db;
	}
}
