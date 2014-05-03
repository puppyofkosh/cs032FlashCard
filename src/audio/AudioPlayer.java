package audio;

import java.io.IOException;

import flashcard.FlashCard;

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

	void play(FlashCard card) throws IOException;

	/**
	 * Stops the playback of any AudioFiles currently playing.
	 */

	boolean isPlaying();
	void stop();

	void playThenRun(FlashCard card, Runnable... runnables) throws IOException;

	void playThenRun(AudioFile file, Runnable... runnables) throws IOException;
}
