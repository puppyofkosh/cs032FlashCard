package audio;

import java.io.IOException;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import com.sun.speech.freetts.audio.AudioPlayer;

/**
 * Produce text to speech using the freeTTS library
 * 
 * Ian
 * 
 * We probably only need to keep one of these around for our entire program. That or I'll make this static
 * 
 * @author puppyofkosh
 *
 */
public class FreeTTSReader implements TextToSpeechReader {

	
	private VoiceManager manager;
	private Voice voice;
	
	// We'll want to override this to provide a byte stream or something...for now just have it write a file
	private AudioPlayer audioPlayer;
	
	private String DEFAULT_VOICE = "kevin16";
	private String TEMP_FILE_NAME = "tmp";
	
	// initialization will probably need the locations of some files and stuff (not important to design)
	public FreeTTSReader() throws IOException
	{
		manager = VoiceManager.getInstance();
		voice = manager.getVoice(DEFAULT_VOICE);
		if (voice == null)
		{
			throw new IOException("Could not load default voice!");
		}
		
		voice.allocate();
		audioPlayer = new SingleFileAudioPlayer(TEMP_FILE_NAME, javax.sound.sampled.AudioFileFormat.Type.WAVE);
        voice.setAudioPlayer(audioPlayer);
	}

	
	
	@Override
	public AudioFile read(String text) {
		voice.speak(text);
		audioPlayer.close();
		audioPlayer = new SingleFileAudioPlayer(TEMP_FILE_NAME, javax.sound.sampled.AudioFileFormat.Type.WAVE);
		
		return null;
	}
	
	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.out.println("USAGE: bin/tts sometextinquotes\n THe output will show up in tmp.wav");
			return;
		}
		
		try {
			FreeTTSReader reader = new FreeTTSReader();
			reader.read(args[0]);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
