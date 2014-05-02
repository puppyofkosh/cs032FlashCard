package protocol;

import java.io.IOException;
import java.util.Collection;

import audio.AudioFile;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * Stores the meta data about a flashcard locally, but also store a connection to a server so we can download the audio stuff
 * @author puppyofkosh
 *
 */
public class NetworkedFlashCard implements FlashCard{
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<FlashCardSet> getSets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTag(String tag) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTag(String tag) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setInterval(int interval) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPath() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AudioFile getQuestionAudio() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AudioFile getAnswerAudio() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean sameMetaData(FlashCard f) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
