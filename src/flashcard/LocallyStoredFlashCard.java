package flashcard;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import utils.Writer;
import audio.AudioFile;


/**
 * Somewhat legit implementation of a flashcard that stores its data in memory
 * and on file, only writing/reading to file when updates are made
 * @author puppyofkosh
 *
 */
public class LocallyStoredFlashCard implements FlashCard{

	public static class Data
	{
		public String name = "", sets = "", pathToFile = "";
		public String questionText = "", answerText = "";
		public List<String> tags = Arrays.asList();
		public AudioFile question, answer;
		public int interval = 0;
	}
	
	private Data data;
	
	public LocallyStoredFlashCard(Data data)
	{
		this.data = data;
	}
	
	
	@Override
	public String getName() {
		return data.name;
	}

	// FIXME: Implement
	@Override
	public Collection<FlashCardSet> getSets() {
		return null;
	}

	@Override
	public Collection<String> getTags() {
		return data.tags;
	}

	@Override
	public void addTag(String tag) throws IOException {
		data.tags.add(tag);
		SimpleFactory.writeMetadata(this);
	}

	@Override
	public int getInterval() {
		return data.interval;
	}

	@Override
	public void setInterval(int interval) throws IOException {
		data.interval = interval;
		Writer.out("Changing interval");
		SimpleFactory.writeMetadata(this);
	}

	@Override
	public String getPath() {
		return data.pathToFile;
	}

	@Override
	public AudioFile getQuestionAudio() {
		return data.question;
	}

	@Override
	public AudioFile getAnswerAudio() {
		return data.answer;
	}
	
	@Override
	public String toString()
	{
		return "Flashcard at " + data.pathToFile;
	}

}
