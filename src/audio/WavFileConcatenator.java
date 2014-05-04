package audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
 * @author Peter
 */
public class WavFileConcatenator {

	// the AudioFile that holds the blank interval audio for concatenation
	private static DiscAudioFile intervalAudio = setupIntervalAudio();
	
	// the path to the destination folder where generated wav files will be placed
	private static String destination = setupOutputLocation();
	
	/**
	 * attempts to set the output location to the user's chosen location
	 * from previous runs of the program. If this cannot be done for whatever reason,
	 * it defaults to 'output'
	 * @return The user's chosen output location from previous runs of the program,
	 * or 'output' if that cannot be found
	 */
	private static String setupOutputLocation() {
		//File locationFile = new File("output_location.set");
		//if (locationFile.exists()) {
		//	try (BufferedReader read = new BufferedReader(new FileReader(locationFile))) {
		//		return read.readLine(); 
		//	} catch (Throwable e) {}
		//}
		new File("output").mkdir();
		return "output";
	}
	
	/**
	 * Changes the output destination for created wav files
	 * @param destination The path of the new output folder
	 */
	public static void changeDestination(String destination) {

		WavFileConcatenator.destination = destination;
		new File(destination).mkdirs();
		
		File locationFile = new File("output_location.set");
		locationFile.delete();
		
		try (PrintWriter writer = new PrintWriter(new FileWriter(locationFile))) {
			writer.println(destination);
		} catch (Throwable e) {}
	}
	
	/**
	 * Sets up and returns an AudioFile containing 1 second of silent audio 
	 * @return An AudioFile containing 1 second of silent audio
	 */
	private static DiscAudioFile setupIntervalAudio() {
		intervalAudio = new DiscAudioFile("intervalaudio.wav");
		AudioFormat format = utils.FlashcardConstants.standardizedFormat;
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
		new File(Settings.getOutputDestination()).mkdirs();
		File output = new File(Settings.getOutputDestination() + "/"+ fileName + ".wav");
		int collisionPreventer = 0;
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
		AudioFormat format = utils.FlashcardConstants.standardizedFormat;
		
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
	
	/**
	 * Gets the output destination of wav files
	 * @return the output destination of wav files
	 */
	public static String getDestination() {
		return destination;
	}
}
