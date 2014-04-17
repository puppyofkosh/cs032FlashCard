package audio;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import utils.FlashcardConstants;

/**
 * An AudioFile that is temporarily stored on disc, and is deleted once the program closes
 * @author Peter
 *
 */
public class TempAudioFile implements AudioFile {

	private File file;
	private static int id = 0;

	public TempAudioFile() {
		file = new File("temp" + ++id + ".wav");
		if (file.exists())
			file = new File("temp" + ++id + ".wav");
		file.deleteOnExit();
		// TODO Auto-generated constructor stub
	}

	@Override
	public AudioInputStream getStream() throws IOException {
		// TODO Auto-generated method stub
		try {
		return AudioSystem.getAudioInputStream(file);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return bytStream.toByteArray();
	}
}
