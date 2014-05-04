package audio;

import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.SwingWorker;

import controller.Controller;
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
	public void startRecord(Runnable...runnables) {
		recordedAudio = new DiscAudioFile("temp.wav");
		format = FlashcardConstants.standardizedFormat;
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); 
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new CaptureThread(runnables).start();
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
		
		TimeoutThread thread;
		
		CaptureThread(Runnable...runnables) {
			thread = new TimeoutThread(runnables);
			try {
				line.open(format);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				Controller.guiMessage("Unable to record", true);
			}
			
			line.start();
			thread.execute();
		}
		public void run() {
			try {
				AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
				AudioSystem.write(new AudioInputStream(line), fileType, recordedAudio);
				thread.cancel(true);
			} catch (IOException e) {
				e.printStackTrace();
				Controller.guiMessage("Unable to record", true);
			}
		}
	}
	private class TimeoutThread extends SwingWorker<Boolean, Void> {
		int duration = 60;
		Runnable[] runnables;
		TimeoutThread(Runnable...runnables) {
	
			this.runnables = runnables;
		}
		
		@Override
		public Boolean doInBackground() {
			try {
				Thread.sleep(1000 * duration);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				return false;
			}
			return true;
		}
		
		@Override
		public void done() {
			try {
				for (Runnable task: runnables) {
					task.run();
				}
				stopRecord();
				
			} catch (Exception e) {
				for (Runnable task: runnables) {
					task.run();
				}
			}
		}
	}

}
