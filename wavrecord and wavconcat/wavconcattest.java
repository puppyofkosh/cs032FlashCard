import java.io.File;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

// taken mostly from:
// http://karthicraghupathi.com/2013/01/12/concatenate-wav-files-in-java/

public class wavconcattest {

	public static void concatentateFiles(List<String> sourceFiles, String destinationFile) throws Exception {
		
		AudioInputStream audioInputStream = null;
		List<AudioInputStream> listStream = new ArrayList<>();
		AudioFormat audioFormat = null;
		Long frameLength = null;
		
		for (String sourceFile : sourceFiles) {
			audioInputStream = AudioSystem.getAudioInputStream(new File(sourceFile));
			
			if (audioFormat == null) {
				audioFormat = audioInputStream.getFormat();
			}
			
			listStream.add(audioInputStream);
			
			if (frameLength == null) {
				frameLength = audioInputStream.getFrameLength();
			}
			else {
				frameLength += audioInputStream.getFrameLength();
			}
		}
		AudioSystem.write(new AudioInputStream(new SequenceInputStream(Collections.enumeration(listStream)), audioFormat, frameLength), Type.WAVE, new File(destinationFile));
	}
	
	public static void main(String[] args) throws Exception {
		List<String> input = new ArrayList<>();
		int interval = 0;
		input.add("testnoise.wav");
		for (int i = 0; i < interval; i++) {
			input.add("blank.wav");
		}
		input.add("testnoise.wav");
		concatentateFiles(input, "newoutput0.wav");
	}
}
