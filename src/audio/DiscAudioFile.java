package audio;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * An AudioFile that refers to a file on disc
 * @author Peter
 *
 */
public class DiscAudioFile extends File implements AudioFile {

	private static final long serialVersionUID = 1L;

	public DiscAudioFile(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	@Override
	public AudioInputStream getStream() throws IOException {
		// TODO Auto-generated method stub
		try {
		return AudioSystem.getAudioInputStream(this);
		}
		catch (UnsupportedAudioFileException e) {
			throw new IOException("Audio File Format Exception");
		}
	}
}
