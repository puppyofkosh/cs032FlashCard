package audio;

import java.util.List;

import javax.sound.sampled.AudioInputStream;

/**
 * A representation of an AudioFile that can be used within the program
 * 
 * To be implemented by Peter
 * 
 */
public interface AudioFile {
	/**
	 * Gets the AudioInputStream for this AudioFile.
	 * This might be used for combining AudioFiles, or for playing an AudioFile.
	 * @return The AudioInputStream for this AudioFile
	 */
	AudioInputStream getStream();
	
	/**
	 * FIXME: Only for testing purposes! This is unsafe as a caller could modify the array we return
	 * As soon as we implement getStream, we should remove this.
	 * @return
	 */
	byte[] getRawBytes();
}
