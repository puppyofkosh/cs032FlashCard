package audio;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * AudioPlayer that plays a single AudioFile at a time. 
 * @author Peter
 *
 */
public class BasicAudioPlayer implements AudioPlayer {


	private static boolean playing = false;
	private static Clip clip;
	
	@Override
	public void play(AudioFile a) throws IOException {
		// TODO Auto-generated method stub
		stop();
		try {
			new PlayThread(a).start();
			
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			throw new IOException("Line Unavailable Exception");
		}
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return playing;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if (clip != null) {	
			synchronized (clip) {
				if (clip.isOpen())
					clip.close();
			}
		}
			
		playing = false;
	}
	
	/**
	 * Thread to play an audio clip, and close it automatically when it is done.
	 * 
	 * 
	 * 			
			
			FIXME: Seems to be broken on linux. This may help:
			
			Mixer.Info pulse = null;
			for (Mixer.Info m : AudioSystem.getMixerInfo())
			{
				if (m.getName().equals("PulseAudio Mixer"))
					pulse = m;
			}
			System.out.println(pulse);
			if (pulse == null)
				clip = AudioSystem.getClip();
			else
				clip = AudioSystem.getClip(pulse);
	 * @author Peter
	 *
	 */
	private static class PlayThread extends Thread {
		
		Clip threadClip;
		
		PlayThread(AudioFile file) throws LineUnavailableException, IOException {
			clip = AudioSystem.getClip();
			clip.open(file.getStream());
			threadClip = clip;
		}
		
		@Override
		public void run() {
			
			if (clip != null) {
				clip.start();
				playing = true;
				try {
					Thread.sleep(100 + clip.getMicrosecondLength() / 1000);
				} catch (InterruptedException e) {}
				synchronized (threadClip) {
					if (threadClip.isOpen()) {
						playing = false;
						threadClip.close();
					}
				}
				
			}
		}
	}
	
}
