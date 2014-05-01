package audio;

import java.io.ByteArrayOutputStream;
//import java.io.File;
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
 * Recorder that generates MemoryAudioFiles
 * @author Peter
 *
 */
public class MemoryRecorder implements Recorder {

	ByteArrayOutputStream out;
	TargetDataLine line;
	AudioFormat format;

	@Override
	public void startRecord() {
		//recordedAudio = new DiscAudioFile("testRecordGUI.wav");
		format =  FlashcardConstants.standardizedFormat;
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
		// TODO Auto-generated method stub
		line.stop();
		line.close();
		return new MemoryAudioFile(out.toByteArray());
	}
	
	private class CaptureThread extends Thread {
		public void run() {
			AudioFileFormat.Type fileType = AudioFileFormat.Type.AU;
			{
			try {
				line.open(format);
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			line.start();
			out = new ByteArrayOutputStream();
			
			try {
				AudioSystem.write(new AudioInputStream(line), fileType, out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	}
}
