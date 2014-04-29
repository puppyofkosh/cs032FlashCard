package database;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Test;

import audio.DiscAudioFile;
import audio.MemoryAudioFile;
import flashcard.FlashCard;
import flashcard.FlashCardSet;
import flashcard.SerializableFlashCard;
import flashcard.SimpleSet;

public class DatabaseTest {

	private FlashCardDatabase db;

	public static final int questionLength = 30764;
	public static final int answerLength = 200694;

	private SerializableFlashCard testCard;

	public DatabaseTest() throws ClassNotFoundException, SQLException,
			IOException {
		FlashCardDatabase.clear("data/", "testdb");
		FlashCardDatabase.initialize("data/", "testdb");
		db = new FlashCardDatabase("data/", "testdb");

		// Set up a test card
		SerializableFlashCard.Data data = new SerializableFlashCard.Data();
		data.question = new MemoryAudioFile(new DiscAudioFile(
				"data/flashcard-test/hi-there.wav"));
		data.answer = new MemoryAudioFile(new DiscAudioFile(
				"data/flashcard-test/acronym.wav"));
		data.tags = Arrays.asList("Tag A", "Tag B");
		data.interval = 5;
		data.pathToFile = "files/test-card/";
		data.name = "test-card";

		testCard = new SerializableFlashCard(data);
	}

	/**
	 * Write a card to database, make sure everything was written properly and
	 * that we can change values
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testWritingToDb() throws IOException, InterruptedException {

		System.out.println("First tes");
		// Write the card to DB, make sure everything came out okay
		FlashCard dbCard = db.writeCard(testCard);

		assertTrue(dbCard.getQuestionAudio().getRawBytes().length == questionLength);
		assertTrue(dbCard.getAnswerAudio().getRawBytes().length == answerLength);
		assertTrue(dbCard.getPath().equals("files/test-card/"));
		assertTrue(dbCard.getTags().equals(testCard.getTags()));
		assertTrue(dbCard.getInterval() == testCard.getInterval());

		// Now screw around with the card, try changing some things
		dbCard.setInterval(6);
		assertTrue(dbCard.getInterval() == 6);

		dbCard.addTag("Tag C");
		assertTrue(dbCard.getTags().size() == 3);

		dbCard.removeTag("Tag C");
		assertTrue(dbCard.getTags().size() == 2);

		db.deleteCard(testCard);

		assertTrue(db.getAllCards().size() == 0);

		// PUT THIS AT THE END OF EVERY TEST
		db.close();
	}
	
	@Test
	public void testSets() throws IOException
	{
		System.out.println("Set test");
		SimpleSet set = new SimpleSet("state capitals");
		set.addTag("Tag A");
		set.addCard(testCard);
		
		FlashCardSet dbSet = db.writeSet(set);

		assertTrue(db.getAllCards().size() == 1);
		assertTrue(db.getAllSets().size() == 1);
		
		
		assertTrue(dbSet.getName().equals("state capitals"));		
		assertTrue(dbSet.getTags().contains("Tag A"));
		assertTrue(dbSet.getAll().size() == 1);
		
		dbSet.addTag("Tag B");
		assertTrue(dbSet.getTags().contains("Tag B"));
		
		dbSet.removeTag("Tag A");
		assertTrue(!dbSet.getTags().contains("Tag A"));
		
		for (FlashCard card : dbSet.getAll())
		{
			assertTrue(card.getName().equals("test-card"));
			assertTrue(card.getInterval() == 5);
		}

		
		assertTrue(dbSet.getAll().size() == 1);
				
		db.deleteSet(dbSet);
		
		assertTrue(db.getAllSets().size() == 0);
		
		// PUT THIS AT THE END OF EVERY TEST
		db.close();
	}
/*
	@Test
	public void testWritingToDisk() throws IOException {
		// Write the card to DB, make sure everything came out okay
		FlashCard dbCard = DatabaseFactory.writeCard(testCard);

		assertTrue(dbCard.getQuestionAudio().getRawBytes().length == questionLength);
		assertTrue(dbCard.getAnswerAudio().getRawBytes().length == answerLength);
		assertTrue(dbCard.getPath().equals("files/test-card/"));
		assertTrue(dbCard.getTags().equals(testCard.getTags()));
		assertTrue(dbCard.getInterval() == testCard.getInterval());

		// Now screw around with the card, try changing some things
		dbCard.setInterval(6);
		assertTrue(dbCard.getInterval() == 6);

		dbCard.addTag("Tag C");
		assertTrue(dbCard.getTags().size() == 3);

		dbCard.removeTag("Tag C");
		assertTrue(dbCard.getTags().size() == 2);

		DatabaseFactory.deleteCard(testCard);

		assertTrue(db.getAllCards().size() == 0);

		db.close();
	}
	*/
}
