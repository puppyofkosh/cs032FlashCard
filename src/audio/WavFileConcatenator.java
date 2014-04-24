package audio;



import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
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

import flashcard.FlashCard;
import flashcard.FlashCardSet;

//FIXME: There's a bug in here. When we run something like:
// WavFileConcatenator("myfile")
// The file produced after calling concatenate is "myfilemyfile.wav"

public class WavFileConcatenator {

	private static DiscAudioFile intervalAudio = setupIntervalAudio();
	private static String destination = getOutputLocation();
	
	private static String getOutputLocation() {
		File locationFile = new File("output_location.set");
		if (locationFile.exists()) {
			try (BufferedReader read = new BufferedReader(new FileReader(locationFile))) {
				return read.readLine(); 
			} catch (Throwable e) {}
		}
		
		File defaultOutput = new File("output");
		
		if (!defaultOutput.exists())
			defaultOutput.mkdir();
		
		return "output";
	}
	
	public static void changeDestination(String destination) {

		WavFileConcatenator.destination = destination;
		File locationFile = new File("output_location.set");
		locationFile.delete();
		
		try (PrintWriter writer = new PrintWriter(new FileWriter(locationFile))) {
			writer.println(destination);
		} catch (Throwable e) {}
	}
	
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
	
	private static File getNewFile(String fileName) {
		File output = new File(destination + "/"+ fileName + ".wav");
		int collisionPreventer = 0;
		while (output.exists()) {
			output = new File(destination + "/" + fileName + collisionPreventer++ + ".wav");
		}
		
		return output;
	}
	
	public static File concatenate(List<AudioInputStream> streams, String name) throws IOException  {
		long frameLength = 0;
		AudioFormat format = utils.FlashcardConstants.standardizedFormat;
		for (AudioInputStream stream : streams) {
			frameLength += stream.getFrameLength();
		}
		AudioInputStream output = new AudioInputStream(new SequenceInputStream(Collections.enumeration(streams)), format, frameLength);
		File file = getNewFile(name);
		AudioSystem.write(output, Type.WAVE, file);
		return file;
	}
	
	public static File concatenate(FlashCard card) throws IOException {
		List<AudioInputStream> streams = new ArrayList<>();
		streams.add(card.getQuestionAudio().getStream());
		for (int i = 0; i < card.getInterval(); i++) {
			streams.add(intervalAudio.getStream());
		}
		streams.add(card.getAnswerAudio().getStream());
		
		return concatenate(streams, card.getName());
	}
	
	public static void concatenate(Collection<FlashCard> cards) throws IOException {
		for (FlashCard card : cards) {
			concatenate(card);
		}
	}
	
	public static void concatenate(FlashCardSet set) throws IOException {
		concatenate(set.getAll());
	}
}
