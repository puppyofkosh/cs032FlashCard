package audio;

import java.io.IOException;

/**
 * A class for playing audio
 * 
 * Peter
 * 
 */
public interface AudioPlayer {
	
	/**
	 * Plays an AudioFile, stopping the playback of any other AudioFiles currently playing.
	 * @param a The AudioFile to play
	 */
	void play(AudioFile a) throws IOException;
	/**
	 * Stops the playback of any AudioFiles currently playing.
	 */
	
	boolean isPlaying();
	void stop();
}
