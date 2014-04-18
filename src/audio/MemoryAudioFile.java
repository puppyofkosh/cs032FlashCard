package audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import utils.FlashcardConstants;

/**
 * An AudioFile stored in the JVM's memory, rather than on disc
 * @author Peter
 *
 */
public class MemoryAudioFile implements AudioFile, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] data;
	
	public MemoryAudioFile(byte[] data) {
		this.data = data;
	}
	
	public MemoryAudioFile(AudioFile a) throws IOException
	{
		this(a.getRawBytes());
	}
	
	@Override
	public AudioInputStream getStream() throws IOException {
		// TODO Auto-generated method stub
		try {
			return AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			throw new IOException("Audio File Format Exception");
		}
	}

	@Override
	public AudioFormat getFormat() {
		// TODO Auto-generated method stub
		return FlashcardConstants.standardizedFormat;
	}

	@Override
	public byte[] getRawBytes() {
		// TODO Auto-generated method stub
		return data;
	}
}
