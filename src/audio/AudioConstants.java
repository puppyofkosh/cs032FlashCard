package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class AudioConstants {	
	
public static AudioFormat STANDARD_FORMAT = new AudioFormat(Encoding.PCM_SIGNED, 16000.0f, 16, 1, 2, 16000.0f, false);
}
