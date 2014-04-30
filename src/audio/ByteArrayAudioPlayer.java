package audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import flashcard.FlashCard;

/**
 * FIXME: In the ideal world, this class would be re-usable
 * @author puppyofkosh
 *
 */
public class ByteArrayAudioPlayer implements AudioPlayer {

	private boolean playing;
	private SourceDataLine line;
	private Thread playThread;
	
	@Override
	public void play(AudioFile a) throws IOException {
		if (a == null)
			throw new IOException("Received null AudioFile");
		else {
			stop();
			playThread = new PlayThread(a);
			playThread.start();
		}
	}
	
	@Override
	public void play(FlashCard card) throws IOException {
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
		if (playThread != null) {
			playThread.interrupt();
			playThread = null;
			line.stop();
			line.close();
		}
	}
	
	private void blockingPlay(AudioFile file) {
		try {
			AudioFormat format = AudioConstants.TTSREADER;
			
			byte[] bytes = file.getRawBytes();

			AudioInputStream stream = new AudioInputStream(new ByteArrayInputStream(bytes), format, bytes.length / format.getFrameSize());
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();
			byte[] buffer = new byte[format.getFrameSize() * (int)format.getFrameRate()];
			int bytesRead = stream.read(buffer);
			while (bytesRead != -1) {
				line.write(buffer, 0, bytesRead);
				bytesRead = stream.read(buffer);
			}
		} catch (Throwable e) {
			System.out.println(e.getMessage());
		}
		finally {
			line.drain();
			line.stop();
			line.close();
		}
	}
	
	private class PlayCardThread extends Thread {
		FlashCard card;
		
		PlayCardThread(FlashCard card) {
			this.card = card;
		}
		
		@Override
		public void run() {
			blockingPlay(card.getQuestionAudio());
			try {
				Thread.sleep(card.getInterval() * 1000);
			} catch (InterruptedException e) {
				return;
			}
			blockingPlay(card.getAnswerAudio());
		}
	}

	private class PlayThread extends Thread {

		AudioFile file;

		PlayThread(AudioFile file) {
			this.file = file;

		}

		@Override
		public void run() {
			blockingPlay(file);
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException
	{

		FreeTTSReader reader = new FreeTTSReader();
		ByteArrayAudioPlayer player = new ByteArrayAudioPlayer();

		AudioFile file = reader.read("Testing, testing 1 2 3, homie");
		player.play(file);
		Thread.sleep(6000);

		file = reader.read("Whats up, homie");
		player.play(file);
		Thread.sleep(5000);

		file = reader
				.read("This clip does not contain the word homie");
		player.play(file);
		Thread.sleep(5000);

		file = new DiscAudioFile("data/tts-test/strange-characters.wav");
		player.play(file);
		Thread.sleep(11000);
	}
}

