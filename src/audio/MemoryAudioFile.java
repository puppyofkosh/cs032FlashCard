package audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * An AudioFile stored in the JVM's memory, rather than on disc
 */
public class MemoryAudioFile implements AudioFile, Serializable {

	private static final long serialVersionUID = 1L;
	private byte[] data;
	
	/**
	 * Turns a byte array holding raw audio data into a MemoryAudioFile
	 * @param data the byte array holding raw audio data
	 */
	public MemoryAudioFile(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Converts an AudioFile into a MemoryAudioFile
	 * @param a the AudioFile to convert
	 * @throws IOException if the AudioFile cannot be read
	 */
	public MemoryAudioFile(AudioFile a) throws IOException {
		this(a.getRawBytes());
	}
	
	@Override
	public AudioInputStream getStream() throws IOException {
		
		try {
			return AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
		} catch (UnsupportedAudioFileException e) {
			throw new IOException("Audio File Format Exception");
		}
	}

	@Override
	public byte[] getRawBytes() {
		return data;
	}
}
