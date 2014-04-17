package audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class ByteArrayAudioPlayer implements AudioPlayer {

	private boolean playing;
	private SourceDataLine line;
	
	@Override
	public void play(AudioFile a) throws IOException {
		stop();
		playing = true;
		new PlayThread(a).start();
	}
	
	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return playing;
	}

	@Override
	public void stop() {
		if (playing) {
			playing = false;
			line.stop();
			line.close();
		}
	}
	
	private class PlayThread extends Thread {
	
		AudioFile file;
		
		PlayThread(AudioFile file) {
			this.file = file;
		
		}
		
		@Override
		public void run() {
			try {
				AudioFormat format = AudioConstants.TTSREADER;
				byte[] data = file.getRawBytes();
				
				AudioInputStream stream = new AudioInputStream(new ByteArrayInputStream(data), format, data.length / format.getFrameSize());
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(format);
				line.start();
				byte[] buffer = new byte[format.getFrameSize() * (int)format.getFrameRate()];
				int bytesRead = stream.read(buffer);
				while (playing && bytesRead != -1) {
					line.write(buffer, 0, bytesRead);
					bytesRead = stream.read(buffer);
				}
		        line.drain();
				line.stop();
				line.close();
				playing = false;
			} catch (Throwable e) {
				System.out.println(e.getMessage());
				playing = false;
				line.drain();
				line.stop();
				line.close();
			}
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

