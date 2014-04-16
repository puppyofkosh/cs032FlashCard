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

public class WavFileConcatenator {

	private static DiscAudioFile intervalAudio = new DiscAudioFile("intervalaudio.wav");
	
	private String destination;
	
	public WavFileConcatenator(String destination) throws IOException {
		this.destination = destination;
		
		if (!intervalAudio.exists()) {
			AudioFormat format = cs32flashcardutil.FlashcardConstants.standardizedFormat;
			int frames = (int) format.getFrameRate();
			int size = (int) (format.getFrameSize() * frames);
			byte[] bytes = new byte[size];
			Arrays.fill(bytes, (byte) 0);
			
			AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(bytes), format, frames), Type.WAVE, intervalAudio);
			intervalAudio.deleteOnExit();
		}
	}
	
	public void changeDestination(String destination) {
		this.destination = destination;
	}
	
	public File getNewFile(String fileName) {
		File output = new File(this.destination + fileName + ".wav");
		int collisionPreventer = 0;
		while (output.exists()) {
			output = new File(this.destination + fileName + collisionPreventer++ + ".wav");
		}
		
		return output;
	}
	
	public void concatenate(List<AudioInputStream> streams, String name) throws IOException  {
		long frameLength = 0;
		AudioFormat format = cs32flashcardutil.FlashcardConstants.standardizedFormat;
		for (AudioInputStream stream : streams) {
			frameLength += stream.getFrameLength();
		}
		AudioInputStream output = new AudioInputStream(new SequenceInputStream(Collections.enumeration(streams)), format, frameLength);
		
		AudioSystem.write(output, Type.WAVE, getNewFile(name));
	}
	
	public void concatenate(FlashCard card) throws IOException {
		List<AudioInputStream> streams = new ArrayList<>();
		streams.add(card.getQuestionAudio().getStream());
		for (int i = 0; i < card.getInterval(); i++) {
			streams.add(intervalAudio.getStream());
		}
		streams.add(card.getAnswerAudio().getStream());
		
		concatenate(streams, card.getName());
	}
}
