package audio;

import javax.sound.sampled.AudioInputStream;

/**
 * A representation of an AudioFile that can be used within the program
 */
public interface AudioFile {
	/**
	 * Gets the AudioInputStream for this AudioFile.
	 * This might be used for combining AudioFiles, or for playing an AudioFile.
	 * @return The AudioInputStream for this AudioFile
	 */
	AudioInputStream getStream();
}