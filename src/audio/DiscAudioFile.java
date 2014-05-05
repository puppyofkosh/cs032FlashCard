package audio;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat.Type;

import controller.Controller;
import utils.FlashcardConstants;

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
	@Override
	public AudioFormat getFormat() {
		// TODO Auto-generated method stub
		return FlashcardConstants.standardizedFormat;
	}

	@Override
	public byte[] getRawBytes() {
		// TODO Auto-generated method stub
		ByteArrayOutputStream bytStream = new ByteArrayOutputStream();
		try {
			AudioSystem.write(getStream(), Type.WAVE, bytStream);
		} catch (IOException e) {
			//XXX :/
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//Controller.guiMessage("audio data could not be read", true);
			return null;
		}
		return bytStream.toByteArray();
	}
}
