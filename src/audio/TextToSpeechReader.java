package audio;
/**
 * An interface for objects that can convert Strings
 * into AudioFiles using text to speech
 * 
 * Implementations will be written by Ian
 */
public interface TextToSpeechReader {
	
	/**
	 * Creates an AudioFile from the input text
	 * using text to speech
	 * @param text The text to read
	 * @return An AudioFile of the text being read.
	 */
	AudioFile read(String text);

}
