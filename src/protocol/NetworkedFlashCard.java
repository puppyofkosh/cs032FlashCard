package protocol;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import audio.AudioFile;

import client.Client;

import flashcard.AbstractFlashCard;
import flashcard.FlashCardSet;
import flashcard.SerializableFlashCard;

/**
 * Stores the meta data about a flashcard locally, but also store a connection to a server so we can download the audio stuff
 * @author puppyofkosh
 *
 */
public class NetworkedFlashCard extends AbstractFlashCard implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	// Some identifier the server uses to identify this card
	private String serverIdentifer;

	public NetworkedFlashCard(SerializableFlashCard.MetaData data, String id)
	{
		super(data);
		this.serverIdentifer = id;
	}
	
	@Override
	public Collection<FlashCardSet> getSets() {
		// TODO Auto-generated method stub
		return Arrays.asList();
	}

	@Override
	public AudioFile getQuestionAudio() {
		throw new UnsupportedOperationException("Audio file does not exist locally");
	}

	@Override
	public AudioFile getAnswerAudio() {
		throw new UnsupportedOperationException("Audio file does not exist locally");
	}
	
	
}
