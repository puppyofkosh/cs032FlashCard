package database;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
	private SerializableFlashCard.Data testData;

	public DatabaseTest() throws ClassNotFoundException, SQLException,
			IOException {
		FlashCardDatabase.clear("data/", "testdb");
		FlashCardDatabase.initialize("data/", "testdb");
		db = new FlashCardDatabase("data/", "testdb");

		// Set up a test card
		testData = new SerializableFlashCard.Data();
		testData.setQuestion(new MemoryAudioFile(new DiscAudioFile(
				"data/flashcard-test/hi-there.wav")));
		testData.setAnswer(new MemoryAudioFile(new DiscAudioFile(
				"data/flashcard-test/acronym.wav")));
		testData.tags = Arrays.asList("Tag A", "Tag B");
		testData.interval = 5;
		testData.pathToFile = "files/test-card/";
		testData.name = "test-card";

		testCard = new SerializableFlashCard(testData);
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

		// Write the card to DB, make sure everything came out okay
		FlashCard dbCard = db.writeCard(testCard);

		assertTrue(dbCard.getQuestionAudio().getRawBytes().length == questionLength);
		assertTrue(dbCard.getAnswerAudio().getRawBytes().length == answerLength);
		assertTrue(dbCard.getTags().equals(testCard.getTags()));
		assertTrue(dbCard.getInterval() == testCard.getInterval());
		
		// Check equals operators
		assertTrue(dbCard.sameMetaData(testCard));
		assertTrue(testCard.sameMetaData(dbCard));
		
		assertTrue(db.getCardByUniqueId(testCard.getUniqueId()) != null);
		assertTrue(db.getCardByUniqueId(UUID.randomUUID()) == null);
		
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
	
	/**
	 * Test having one card in 2 sets
	 * @throws IOException 
	 */
	@Test
	public void testMultipleContainment() throws IOException
	{
		FlashCardSet presidents = new SimpleSet("presidents");		
		
		// Now, if we change set the changes will go in the DB
		
		// Try adding a card for example
		testData.pathToFile = "files/abraham/";
		testData.name = "abroham";
		FlashCard abrahamCard = new SerializableFlashCard(testData);
		presidents.addCard(abrahamCard);
		
		// Write the set we just described up there and replace our reference to it with a set that will reflect what's in the database
		presidents = db.writeSet(presidents);
		
		// Now add the same abraham card to a different set
		FlashCardSet capitals = new SimpleSet("state capitals");
		capitals.addCard(abrahamCard);
		capitals = db.writeSet(capitals);
		
		// Make sure the abraham card has 2 sets according to the database
		List<FlashCard> abrohamCards = db.getFlashCardsByName("abroham");
		assertTrue(abrohamCards.size() == 1);
		FlashCard dbCard = abrohamCards.get(0);
		
		assertTrue(dbCard.getSets().size() == 2);
		
		// Try deleting one of the sets. The card that's contained in both sets should remain.
		

		db.deleteSet(capitals);
		
		assertTrue(dbCard.getSets().size() == 1);
		
		
		assertTrue(db.getAllSets().size() == 1);
		assertTrue(db.getAllCards().size() == 1);
		
		// DElete the second set. The abroham card should now not belong to any set, so it should be gone
		db.deleteSet(presidents);
		assertTrue(db.getAllSets().size() == 0);
		assertTrue(db.getAllCards().size() == 0);
			
		db.close();
	}
	
	@Test
	public void testSets() throws IOException
	{
		

		SimpleSet set = new SimpleSet("state capitals");
		set.addTag("Tag A");
		set.addCard(testCard);
		set.setAuthor("Ian");
		set.setInterval(5);
		
		
		FlashCardSet dbSet = db.writeSet(set);
		
		// Check to make sure the card was written properly
		assertTrue(db.getAllCards().size() == 1);
		assertTrue(db.getAllSets().size() == 1);
		
		// Check the equals operator between a simple set and a db set
		assertTrue(dbSet.sameMetaData(set));
		assertTrue(set.sameMetaData(dbSet));
		
		assertTrue(dbSet.getName().equals("state capitals"));		
		assertTrue(dbSet.getTags().contains("Tag A"));
		assertTrue(dbSet.getAll().size() == 1);
		assertTrue(dbSet.getAuthor().equals("Ian"));
		assertTrue(dbSet.getInterval() == 5);
		
		// Test changing the name
		dbSet.setName("chicken");
		assertTrue(dbSet.getName().equals("chicken"));
		dbSet.setName("state capitals");
		
		// Test adding tags
		dbSet.addTag("Tag B");
		assertTrue(dbSet.getTags().contains("Tag B"));
		
		dbSet.removeTag("Tag A");
		assertTrue(!dbSet.getTags().contains("Tag A"));
		
		// Make sure the set contains the right cards
		for (FlashCard card : dbSet.getAll())
		{
			assertTrue(card.getName().equals("test-card"));
			assertTrue(card.getInterval() == 5);
		}

		
		assertTrue(dbSet.getAll().size() == 1);
				
		// Test the getters/setters
		dbSet.setAuthor("bob");
		assertTrue(dbSet.getAuthor().equals("bob"));
		
		dbSet.setInterval(11);
		assertTrue(dbSet.getInterval() == 11);
		
		dbSet.setTags(Arrays.asList("Tag C", "Tag D"));
		assertTrue(dbSet.getTags().size() == 2);
		assertTrue(dbSet.getTags().contains("Tag C") && dbSet.getTags().contains("Tag D"));
		
		
		dbSet.removeCard(testCard);
		
		assertTrue(dbSet.getAll().size() == 0);
		
		dbSet.addCard(testCard);
		
		assertTrue(dbSet.getAll().size() == 1);
		
		// Try to delete the set
		db.deleteSet(dbSet);
		
		assertTrue(db.getAllSets().size() == 0);
		
		// After deleting a set, all of the "lonely" cards (cards without sets) should be deleted as well
		assertTrue(db.getAllCards().size() == 0);
		
		// PUT THIS AT THE END OF EVERY TEST
		db.close();
	}
	
	@Test
	public void testSearching() throws IOException
	{

		
		SimpleSet set = new SimpleSet("state capitals");
		set.addTag("global-tag");
		set.addCard(testCard);
		set.setAuthor("Ian");
		set.setInterval(5);
		
		FlashCardSet dbSet = db.writeSet(set);
		
		assertTrue(db.getFlashCardsWithTag("global-tag").size() == 1);
		
		db.close();
	}

}
