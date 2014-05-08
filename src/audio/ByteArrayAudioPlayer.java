package audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.SwingWorker;

import flashcard.FlashCard;
import flashcard.SerializableFlashCard;

/**
 * A class for playing audio, based on byte arrays
 */
public class ByteArrayAudioPlayer implements AudioPlayer {
	private PlayThread playThread;
		
	@Override
	public void play(AudioFile a) throws IOException {
		playThenRun(a);
	}

	@Override
	public void play(FlashCard card) throws IOException {
		playThenRun(card);
	}

	@Override
	public boolean isPlaying() {
		return playThread != null;
	}

	@Override
	public void stop() {
		//System.out.println("Stopping");
		if (playThread != null) {
			playThread.stopPlaying();
			playThread = null;
		}
	}
	
	@Override
	public void playThenRun(AudioFile file, Runnable...runnables) throws IOException {
		if (file == null)
			throw new IOException("Received null AudioFile");
		else {
			stop();
			playThread = new PlayAudioFileThread(file, runnables);
			playThread.execute();
		}
	}

	@Override
	public void playThenRun(FlashCard card, Runnable...runnables) throws IOException {
		if (card == null)
			throw new IOException("Recieved null FlashCard");
			// to make sure that this is acknowledged, 
			// it isn't a runtime exception

		else {
			stop();
			playThread = new PlayCardThread(card, runnables);
			playThread.execute();
		}
	}
	
	/**
	 * Thread that can play audio and run Runnables afterwards
	 * on the Event Dispatch Thread
	 */
	public static abstract class PlayThread 
	extends SwingWorker<Void, Void> {
		protected volatile boolean isPlaying = true;
		protected Runnable[] runnables;
		
		/**
		 * sets up Thread to run all the Runnable arguments on the
		 * Event Dispatch Thread after done playing
		 * @param runnables
		 */
		public PlayThread(Runnable...runnables) {
			this.runnables = runnables;
			
		}
		
		public void stopPlaying() {
			isPlaying = false;
		}
		
		@Override
		public void done() {
			for(Runnable task: runnables) {
				task.run();
			}
		}
		
		/**
		 * plays the AudioFile, blocking until the audio is over
		 * @param file the AudioFile to play
		 * @throws InterruptedException if interrupted
		 */
		protected void blockingPlay(AudioFile file) throws InterruptedException {
			SourceDataLine line = null;
			try {
				AudioFormat format = AudioConstants.STANDARD_FORMAT;

				byte[] bytes = file.getRawBytes();
				
				AudioInputStream stream = new AudioInputStream(
						new ByteArrayInputStream(bytes), format,
						bytes.length / format.getFrameSize()); // length in frames
				
				// setup line to play audio
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(format);
				line.start();
				// setup buffer to hold the audio
				int bufferSize = format.getFrameSize() * (int) format.getFrameRate() / 4;
				byte[] buffer = new byte[bufferSize];
				int bytesRead = stream.read(buffer);
				while (bytesRead != -1) {
					
					if (!isPlaying) {
						line.stop();
						line.close();
						throw new InterruptedException("");
					}
					//play audio and buffer in next bytes
					line.write(buffer, 0, bytesRead);
					bytesRead = stream.read(buffer);
				}

				line.drain();
				line.stop();
				line.close();

			} catch (Exception e) {
				return;
			}
		}
	}	
	
	/**
	 * A PlayThread to play a FlashCard
	 */
	private class PlayCardThread extends PlayThread {
		FlashCard card;
		
		PlayCardThread(FlashCard card, Runnable...runnables) {
			super(runnables);
			// Keep the card locally in case it is removed from DB
			this.card = new SerializableFlashCard(card);
		}
		
		@Override
		public Void doInBackground() {
			try {
				blockingPlay(card.getQuestionAudio());
				Thread.sleep(card.getInterval() * 1000);				
				blockingPlay(card.getAnswerAudio());

			} catch (InterruptedException e) {}
			//just want to stop playing if interrupted
			return null;
		}
	}

	/**
	 * A Play Thread to play an AudioFile
	 */
	private class PlayAudioFileThread extends PlayThread {

		AudioFile file;	
		
		PlayAudioFileThread(AudioFile file, Runnable...runnables) {
			super(runnables);
			this.file = file;
		}

		@Override
		public Void doInBackground() {
			try {
				blockingPlay(file);
			} catch (InterruptedException e) {}
			return null;
		}
	}
}
