package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class AudioConstants {	
	
public static AudioFormat TTSREADER = new AudioFormat(Encoding.PCM_SIGNED, 16000.0f, 16, 1, 2, 16000.0f, false);
public static AudioFormat MICROPHONE_TEST = new AudioFormat(Encoding.PCM_UNSIGNED, 8000.0f, 8, 1, 1, 8000f, false);
}
