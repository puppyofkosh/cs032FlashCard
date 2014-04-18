package audio;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import flashcard.FlashCard;

//FIXME: There's a bug in here. When we run something like:
// WavFileConcatenator("myfile")
// The file produced after calling concatenate is "myfilemyfile.wav"

public class WavFileConcatenator {

	private static DiscAudioFile intervalAudio = new DiscAudioFile("intervalaudio.wav");
	private static String destination = "output";
	
	public WavFileConcatenator(String string) throws IOException {
		this();
	}
	
	public WavFileConcatenator() throws IOException {
		//this.destination = destination;
		
		if (!intervalAudio.exists()) {
			AudioFormat format = utils.FlashcardConstants.standardizedFormat;
			int frames = (int) format.getFrameRate();
			// FIXME: Weird int cast
			int size = (format.getFrameSize() * frames);
			byte[] bytes = new byte[size];
			Arrays.fill(bytes, (byte) 0);
			
			AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(bytes), format, frames), Type.WAVE, intervalAudio);
			intervalAudio.deleteOnExit();
		}
	}
	
	public static void changeDestination(String destination) {

		WavFileConcatenator.destination = destination;
	}
	
	public File getNewFile(String fileName) {
		File output = new File(destination + "/"+ fileName + ".wav");
		int collisionPreventer = 0;
		while (output.exists()) {
			output = new File(destination + "/" + fileName + collisionPreventer++ + ".wav");
		}
		
		return output;
	}
	
	public File concatenate(List<AudioInputStream> streams, String name) throws IOException  {
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
	
	public File concatenate(FlashCard card) throws IOException {
		List<AudioInputStream> streams = new ArrayList<>();
		streams.add(card.getQuestionAudio().getStream());
		for (int i = 0; i < card.getInterval(); i++) {
			streams.add(intervalAudio.getStream());
		}
		streams.add(card.getAnswerAudio().getStream());
		
		return concatenate(streams, card.getName());
	}
}
