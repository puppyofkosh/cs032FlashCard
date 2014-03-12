package audio;

/**
 * A class for playing audio
 */
public interface AudioPlayer {
	
	/**
	 * Plays an AudioFile, stopping the playback of any other AudioFiles currently playing.
	 * @param a The AudioFile to play
	 */
	void play(AudioFile a);
	
	/**
	 * Stops the playback of any AudioFiles currently playing.
	 */
	void stop();
}
