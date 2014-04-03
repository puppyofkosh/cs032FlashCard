package audio;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
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
	AudioInputStream getStream() throws IOException;

	
	/**
	 * Also not Serializable - we may need to have a constants class to store it
	 * on the client and server side.
	 * @return
	 */
	AudioFormat getFormat();
	
	/**
	 *
	 * FIXME: Only for testing purposes! This is unsafe as a caller could modify the array we return
	 * As soon as we implement getStream, we should remove this.
	 * .....
	 * 
	 * Maybe this method does have a future - AudioInputStreams aren't serializable - meaning they can't
	 * be sent/received over socket with Read/WriteObject. Obviously we can stream over socket but we'd have
	 * to open additional ones like so.
	 * http://stackoverflow.com/questions/7796179/how-does-one-establish-multiple-io-streams-between-a-client-and-server
	 * @return
	 */
	byte[] getRawBytes();
}
