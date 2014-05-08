package audio;

import java.io.IOException;

import flashcard.FlashCard;

/**
 * A class for playing audio
 */
public interface AudioPlayer {

	/**
	 * Plays an AudioFile, stopping the playback 
	 * of any other AudioFiles currently playing.
	 * @param a The AudioFile to play
	 * @throws IOException if there is a problem reading the audio file
	 */
	void play(AudioFile a) throws IOException;

	/**
	 * Plays a FlashCard how it will sound once exported, 
	 * stopping the playback of any other AudioFiles currently playing.
	 * @param card the FlashCard to play
	 * @throws IOException if there is a problem reading the FlashCard
	 */
	void play(FlashCard card) throws IOException;

	/**
	 * Checks if the player is currently playing audio
	 * @return true if there is audio playing, false otherwise
	 */
	boolean isPlaying();
	
	/**
	 * Stops the playback of any AudioFiles currently playing.
	 */
	void stop();

	/**
	 * Plays a FlashCard how it will sound once exported, 
	 * stopping the playback of any other AudioFiles currently playing.
	 * Once it is done playing, all the runnables 
	 * will be run on the Event Dispatch Thread
	 * @param card The FlashCard to play
	 * @param runnables Runnable objects to run after playback
	 * @throws IOException if there is a problem reading the FlashCard
	 */
	void playThenRun(FlashCard card, Runnable... runnables) throws IOException;

	/**
	* Plays an AudioFile, stopping the playback 
	 * of any other AudioFiles currently playing.
	 * Once it is done playing, all the runnables 
	 * will be run on the Event Dispatch Thread
	 * @param file The AudioFile to play
	 * @param runnables Runnable objects to run after playback
	 * @throws IOException if there is a problem reading the FlashCard
	 */
	void playThenRun(AudioFile file, Runnable... runnables) throws IOException;
}
