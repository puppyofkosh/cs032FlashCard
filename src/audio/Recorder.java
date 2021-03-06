package audio;

/**
 * An interface for Objects that can record audio from a microphone.
 */
public interface Recorder {

	/**
	 * Starts recording audio from the microphone.
	 * This will only work once per object.
	 * Once done recording, it will run all the Runnable
	 * objects on the Event Dispatch Thread
	 */
	void startRecord(Runnable... runnables);
	
	/**
	 * Stops recording audio from the microphone,
	 * and returns the audio that has been recorded.
	 * This will only work once per object.
	 * @return the AudioFile that has been recorded
	 */
	AudioFile stopRecord();
}
