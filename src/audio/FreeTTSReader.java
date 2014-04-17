package audio;

import java.io.IOException;
import java.nio.file.Paths;

import utils.FlashcardConstants;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

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
	
	/**
	 * Setup our TTS reader
	 * @throws IOException - A lot can go wrong here-missing audio files, mainly.
	 */
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
		
		// FIXME: Figure this shit out-what size do we want?
		audioPlayer.setAudioFormat(FlashcardConstants.standardizedFormat);
        
		
		voice.setAudioPlayer(audioPlayer);
	}

	
	/**
	 * Turn text into speech
	 * @param text - Text to "translate"
	 */
	@Override
	public AudioFile read(String text) {
		if (audioPlayer == null)
		{
			audioPlayer = new SingleFileAudioPlayer(TEMP_FILE_NAME, javax.sound.sampled.AudioFileFormat.Type.WAVE);
		}
		
		voice.speak(text);
		audioPlayer.close();
		audioPlayer = null;
		
		
		return new DiskAudioFile(Paths.get(TEMP_FILE_NAME + ".wav"));
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
			e.printStackTrace();
		}
	}

}
