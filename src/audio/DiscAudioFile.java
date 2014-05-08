package audio;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat.Type;

/**
 * An AudioFile that refers to a file on disc
 */
public class DiscAudioFile extends File implements AudioFile {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a DiscAudioFile the same way as a regular File
	 * @param arg0 the name of the DiscAudioFile
	 */
	public DiscAudioFile(String arg0) {
		super(arg0);
	}

	@Override
	public AudioInputStream getStream() throws IOException {
		
		try {
			return AudioSystem.getAudioInputStream(this);
		}
		catch (UnsupportedAudioFileException e) {
			throw new IOException("Audio File Format Exception");
		}
	}

	@Override
	public byte[] getRawBytes() {
		
		ByteArrayOutputStream bytStream = new ByteArrayOutputStream();
		try {
			AudioSystem.write(getStream(), Type.WAVE, bytStream);
		} catch (IOException e) {
			return null;
		}
		return bytStream.toByteArray();
	}
}
