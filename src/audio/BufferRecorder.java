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

import settings.Settings;
import controller.Controller;

/**
 * Recorder that writes audio to a file as it records, then
 * converts it into a MemoryAudioFile once recording is over
 *
 */
public class BufferRecorder implements Recorder {
	
	TargetDataLine line;
	DiscAudioFile recordedAudio;
	AudioFormat format;
	
	@Override
	public void startRecord(Runnable...runnables) {
		recordedAudio = new DiscAudioFile("temp.wav");
		format = AudioConstants.STANDARD_FORMAT;
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); 
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
		} catch (LineUnavailableException e) {
			Controller.guiMessage("unable to record", true);
		}
		new CaptureThread(runnables).start();
	}

	@Override
	public AudioFile stopRecord() {
		line.stop();
		line.close();
		AudioFile newFile = null;
		try { 
			newFile = new MemoryAudioFile(recordedAudio.getRawBytes());
		} catch (Throwable e) {}
		if (recordedAudio.exists())
			recordedAudio.delete();
		return newFile;
	}
	
	/**
	 * A Thread to capture audio from the microphone
	 */
	private class CaptureThread extends Thread {
		
		TimeoutThread thread;
		
		CaptureThread(Runnable...runnables) {
			thread = new TimeoutThread(runnables);
			try {
				line.open(format);
			} catch (LineUnavailableException e) {
				Controller.guiMessage("Unable to record", true);
			}
			
			line.start();
			thread.execute();
		}
		
		@Override
		public void run() {
			try {
				AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
				AudioSystem.write(new AudioInputStream(line), fileType, recordedAudio);
				// writes audio to recordedAudio, blocks until line is closed
				
				thread.cancel(true);
				// once recording is done, there is no need
				//to worry about exceeding the timeout
			} catch (IOException e) {
			
				Controller.guiMessage("Unable to record", true);
			}
		}
	}
	
	/**
	 * Thread to stop recording if it goes too long
	 */
	private class TimeoutThread extends SwingWorker<Boolean, Void> {
		int duration;
		Runnable[] runnables;
		TimeoutThread(Runnable...runnables) {
	
			this.runnables = runnables;
			duration = Settings.getTimeout();
		}
		
		@Override
		public Boolean doInBackground() {
			try {
				Thread.sleep(1000 * duration);
			} catch (InterruptedException e) {
				return false;
			}
			return true;
		}
		
		@Override
		public void done() {
			for (Runnable task: runnables) {
				task.run();
			}
			stopRecord();
		}
	}
}
