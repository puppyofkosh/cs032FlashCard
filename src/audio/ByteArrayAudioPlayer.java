package audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import flashcard.FlashCard;

/**
 * FIXME: In the ideal world, this class would be re-usable
 * 
 * @author puppyofkosh
 * 
 */
public class ByteArrayAudioPlayer implements AudioPlayer {
	private PlayThread playThread;
		
	@Override
	public void play(AudioFile a) throws IOException {
		if (a == null)
			throw new IOException("Received null AudioFile");
		else {
			stop();
			playThread = new PlayAudioFileThread(a);
			playThread.start();
		}
	}

	@Override
	public void play(FlashCard card) throws IOException {
		System.out.println("Starting");
		if (card == null)
			throw new IOException("Recieved null FlashCard");

		else {
			stop();
			playThread = new PlayCardThread(card);
			playThread.start();
		}
	}

	@Override
	public boolean isPlaying() {
		return playThread != null;
	}

	@Override
	public void stop() {
		System.out.println("Stopping");
		if (playThread != null) {
			playThread.stopPlaying();
			playThread = null;
		}
	}

	
	public static abstract class PlayThread extends Thread
	{
		protected volatile boolean isPlaying = true;
		
		public void stopPlaying()
		{
			isPlaying = false;
		}
		
		protected void blockingPlay(AudioFile file) throws InterruptedException {
			SourceDataLine line = null;
			try {
				AudioFormat format = AudioConstants.TTSREADER;

				byte[] bytes = file.getRawBytes();
				AudioInputStream stream = new AudioInputStream(
						new ByteArrayInputStream(bytes), format, bytes.length
								/ format.getFrameSize());
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(format);
				line.start();
				int bufferSize = format.getFrameSize() * (int) format.getFrameRate() / 4;
				byte[] buffer = new byte[bufferSize];
				int bytesRead = stream.read(buffer);
				while (bytesRead != -1) {
					
					if (!isPlaying) {
						System.out.println("blocking interrupted");
						line.stop();
						line.close();
						throw new InterruptedException("");
					}
					
					line.write(buffer, 0, bytesRead);
					bytesRead = stream.read(buffer);
				}

				line.drain();
				line.stop();
				line.close();

			} catch (IOException e) {
				System.out.println(e.getMessage());
				return;
			} catch (LineUnavailableException e) {
				System.out.println(e.getMessage());
				return;
			}
		}
	}
	
	
	
	private class PlayCardThread extends PlayThread {
		FlashCard card;
		
		PlayCardThread(FlashCard card) {
			this.card = card;
		}
		
		@Override
		public void run() {
			try {
				blockingPlay(card.getQuestionAudio());
				Thread.sleep(card.getInterval() * 1000);				
				blockingPlay(card.getAnswerAudio());

			} catch (InterruptedException e) {
				System.out.println("Interrupted");
				return;
			}
		}
	}

	private class PlayAudioFileThread extends PlayThread {

		AudioFile file;	
		
		PlayAudioFileThread(AudioFile file) {
			this.file = file;

		}

		@Override
		public void run() {
			try {
				blockingPlay(file);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public static void main(String[] args) throws InterruptedException,
			IOException {

		FreeTTSReader reader = new FreeTTSReader();
		ByteArrayAudioPlayer player = new ByteArrayAudioPlayer();

		AudioFile file = reader.read("Testing, testing 1 2 3, homie");
		player.play(file);
		Thread.sleep(6000);

		file = reader.read("Whats up, homie");
		player.play(file);
		Thread.sleep(5000);

		file = reader.read("This clip does not contain the word homie");
		player.play(file);
		Thread.sleep(5000);

		file = new DiscAudioFile("data/tts-test/strange-characters.wav");
		player.play(file);
		Thread.sleep(11000);
	}
}
