package audio;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import utils.FlashcardConstants;

/**
 * Recorder that writes audio to a file as it records, then
 * converts it into a MemoryAudioFile once recording is over
 * @author Peter
 *
 */
public class BufferRecorder implements Recorder {
	
	TargetDataLine line;
	DiscAudioFile recordedAudio;
	AudioFormat format;
	
	@Override
	public void startRecord() {
		recordedAudio = new DiscAudioFile("temp.wav");
		format = FlashcardConstants.standardizedFormat;
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); 
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new CaptureThread().start();
	}

	@Override
	public AudioFile stopRecord() {
		line.stop();
		line.close();
		AudioFile newFile = new MemoryAudioFile(recordedAudio.getRawBytes());
		recordedAudio.delete();
		return newFile;
	}
	
	private class CaptureThread extends Thread {
		public void run() {
			AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
			
			try {
				line.open(format);
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			line.start();
			try {
				AudioSystem.write(new AudioInputStream(line), fileType, recordedAudio);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
