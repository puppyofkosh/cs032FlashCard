package audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import settings.Settings;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * A class for concatenating together AudioFiles
 */
public class WavFileConcatenator {

	// the AudioFile that holds the blank interval audio for concatenation
	private static DiscAudioFile intervalAudio = setupIntervalAudio();
	
	/**
	 * Sets up and returns an AudioFile containing 1 second of silent audio 
	 * @return An AudioFile containing 1 second of silent audio
	 */
	private static DiscAudioFile setupIntervalAudio() {
		intervalAudio = new DiscAudioFile("intervalaudio.wav");
		AudioFormat format = AudioConstants.STANDARD_FORMAT;
		int frames = (int) format.getFrameRate();
		int size = (format.getFrameSize() * frames);
		byte[] bytes = new byte[size];
		Arrays.fill(bytes, (byte) 0);
		
		try {
		AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(bytes), format, frames), Type.WAVE, intervalAudio);
		} catch (IOException e) {
			System.err.println("Could not create interval audio");
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				intervalAudio.delete();
			}
		});
		
		return intervalAudio;
	}
	
	/**
	 * returns a uniquely named wav file starting with the input filename
	 * @param fileName The base for the file name
	 * @return A uniquely named wav file, in the destination folder,
	 * starting with the input file name, and followed by numbers if
	 * necessary to avoid an overlap
	 */
	private static File getUniqueWavFile(String fileName) {
		// ensures the output destination is a valid path
		new File(Settings.getOutputDestination()).mkdirs();
		
		File output = new File(Settings.getOutputDestination() + "/"+ fileName + ".wav");
		int collisionPreventer = 0;
		//makes sure file name is unique by possibly adding a number to the end
		while (output.exists()) {
			output = new File(Settings.getOutputDestination() + "/" + fileName + collisionPreventer++ + ".wav");
		}
		
		return output;
	}
	
	/**
	 * Concatenates the AudioInputStreams into a single wav file
	 * that is in the destination folder, with a name beginning
	 * with the input name
	 * @param streams The streams to concatenate
	 * @param name The name of the generated audio file (although
	 * numbers may be added to the end to avoid overlaps)
	 * @return The generated File
	 * @throws IOException If an error in I/O occurs
	 */
	public static File concatenate(List<AudioInputStream> streams, String name) throws IOException  {
		long frameLength = 0;
		AudioFormat format = AudioConstants.STANDARD_FORMAT;
		
		for (AudioInputStream stream : streams) {
			frameLength += stream.getFrameLength();
		}
		
		AudioInputStream output = new AudioInputStream(new SequenceInputStream(Collections.enumeration(streams)), format, frameLength);
		
		File file = getUniqueWavFile(name);
		AudioSystem.write(output, Type.WAVE, file);
		return file;
	}
	
	/**
	 * Generates the wav file corresponding to a FlashCard
	 * @param card the card to create a wav file from
	 * @return The File that was created
	 * @throws IOException If an error in I/O occurs
	 */
	public static File concatenate(FlashCard card) throws IOException {
		List<AudioInputStream> streams = new ArrayList<>();
		streams.add(card.getQuestionAudio().getStream());
		
		// inserts the desired interval by looping the 1 second silent audio
		for (int i = 0; i < card.getInterval(); i++) {
			streams.add(intervalAudio.getStream());
		}
		streams.add(card.getAnswerAudio().getStream());
		
		return concatenate(streams, card.getName());
	}
	
	/**
	 * creates a wav file from all of the FlashCards in a collection
	 * @param cards The cards to generate wav files from
	 * @throws IOException if an error in I/O occurs.
	 */
	public static void concatenate(Collection<FlashCard> cards) throws IOException {
		for (FlashCard card : cards) {
			concatenate(card);
		}
	}
	
	/**
	 * creates a wav file from all the FlashCards in a FlashCardSet
	 * @param set The set to generate wav files from
	 * @throws IOException if an error in I/O occurs.
	 */
	public static void concatenate(FlashCardSet set) throws IOException {
		concatenate(set.getAll());
	}
}
