package flashcard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import audio.AudioFile;
import audio.MemoryAudioFile;

import flashcard.SimpleFactory.FlashCardData;


/**
 * Refers to a flashcard file on disk
 * 
 * FIXME: Uses serialization!
 * 
 * @author puppyofkosh
 *
 */
public class SimpleFlashCard implements FlashCard {

	// FIXME: Check for non null loadData()!
	private FlashCardData loadData()
	{
		return factory.getData(path);
	}
	
	private void saveData(FlashCardData data)
	{
		factory.saveData(path, data);
	}
	
	
	private String path;
	private SimpleFactory factory;
	
	public SimpleFlashCard(String path, SimpleFactory fac)
	{
		this.path = path;
		this.factory = fac;
	}
	
	@Override
	public String getName() {
		FlashCardData data = loadData();
		return data.name;
		
	}

	@Override
	public Collection<FlashCardSet> getSets() {
		// FIXME: Write this
		FlashCardData data = loadData();
		Collection<FlashCardSet> sets = new ArrayList<>();
		for (String s : data.setNames)
		{
			sets.add(new SimpleSet(s));
		}
		return sets;
	}

	@Override
	public Collection<String> getTags() {
		FlashCardData data = loadData();
		return data.tags;
	}

	@Override
	public void addTag(String tag) throws IOException {
		FlashCardData data = loadData();
		data.tags.add(tag);
		saveData(data);
	}

	@Override
	public int getInterval() {
		FlashCardData data = loadData();
		return data.interval;
	}

	@Override
	public void setInterval(int interval) throws IOException {
		FlashCardData data = loadData();
		data.interval = interval;
		saveData(data);
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public AudioFile getQuestionAudio() {
		FlashCardData data = loadData();
		
		return new MemoryAudioFile(data.questionBytes);
	}

	@Override
	public AudioFile getAnswerAudio() {
		FlashCardData data = loadData();
		
		return new MemoryAudioFile(data.answerBytes);
	}
}
