package audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * An AudioFile stored in the JVM's memory, rather than on disc
 * @author Peter
 *
 */
public class MemoryAudioFile implements AudioFile {

	private byte[] data;
	
	public MemoryAudioFile(byte[] data) {
		this.data = data;
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
}
