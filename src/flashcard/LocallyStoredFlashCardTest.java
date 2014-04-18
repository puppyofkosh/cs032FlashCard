package flashcard;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import audio.DiscAudioFile;

public class LocallyStoredFlashCardTest {
	
	public static final int questionLength = 30764;
	public static final int answerLength = 200694;

	public LocallyStoredFlashCardTest()
	{
		// Write us some default data
		LocallyStoredFlashCard.Data data = new LocallyStoredFlashCard.Data();
		data.question = new DiscAudioFile("data/flashcard-test/hi-there.wav");
		data.answer = new DiscAudioFile("data/flashcard-test/acronym.wav");
		data.tags = Arrays.asList("Tag A", "Tag B");
		data.interval = 5;
		data.pathToFile = "files/test-card/";
		data.name = "test-card";
		
		LocallyStoredFlashCard card = new LocallyStoredFlashCard(data);
		SimpleFactory.writeCard(card);
	}
	
	@Test
	public void testCreation() throws IOException {
		LocallyStoredFlashCard.Data data = new LocallyStoredFlashCard.Data();
		data.question = new DiscAudioFile("data/flashcard-test/hi-there.wav");
		data.answer = new DiscAudioFile("data/flashcard-test/acronym.wav");
		data.tags = Arrays.asList("Tag A", "Tag B");
		data.interval = 5;
		data.pathToFile = "files/test-card/";
		data.name = "test-card";
		
		LocallyStoredFlashCard card = new LocallyStoredFlashCard(data);

		assertTrue(card.getQuestionAudio().getRawBytes().length == questionLength);
		assertTrue(card.getAnswerAudio().getRawBytes().length == answerLength);
		assertTrue(card.getPath().equals("files/test-card/"));
		
		SimpleFactory.writeCard(card);
	}
	
	@Test
	public void testLoading() throws IOException
	{
		FlashCard card = SimpleFactory.readCard("files/test-card/");
		assertTrue(card.getQuestionAudio().getRawBytes().length == questionLength);
		assertTrue(card.getAnswerAudio().getRawBytes().length == answerLength);
		assertTrue(card.getInterval() == 5);
		assertTrue(card.getPath().equals("files/test-card/"));
		assertTrue(card.getName().equals("test-card"));
		assertTrue(card.getTags().equals(Arrays.asList("Tag A", "Tag B")));
	}
	
	@Test
	public void testChanging() throws IOException
	{
		FlashCard card = SimpleFactory.readCard("files/test-card/");
		
		// Check preconditions (we havent done anything yet)
		assertTrue(card.getQuestionAudio().getRawBytes().length == questionLength);
		assertTrue(card.getAnswerAudio().getRawBytes().length == answerLength);
		assertTrue(card.getInterval() == 5);
		assertTrue(card.getName().equals("test-card"));
		assertTrue(card.getTags().equals(Arrays.asList("Tag A", "Tag B")));
		
		// Change something
		card.addTag("Tag C");
		card.setInterval(6);
		
		// Check all other conditions (make sure the file wasn't corrupted)
		assertTrue(card.getQuestionAudio().getRawBytes().length == questionLength);
		assertTrue(card.getAnswerAudio().getRawBytes().length == answerLength);
		assertTrue(card.getInterval() == 6);
		assertTrue(card.getPath().equals("files/test-card/"));
		assertTrue(card.getName().equals("test-card"));
		assertTrue(card.getTags().equals(Arrays.asList("Tag A", "Tag B", "Tag C")));
		
		// Now open the same flashcard again and make sure the values are the same
		FlashCard secondCard = SimpleFactory.readCard("files/test-card/");
		assertTrue(secondCard.getQuestionAudio().getRawBytes().length == questionLength);
		assertTrue(secondCard.getAnswerAudio().getRawBytes().length == answerLength);
		assertTrue(secondCard.getInterval() == 6);
		assertTrue(secondCard.getName().equals("test-card"));
		assertTrue(secondCard.getTags().equals(Arrays.asList("Tag A", "Tag B", "Tag C")));
	}

}
