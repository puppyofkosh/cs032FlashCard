package audio;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class AudioTest {

	@Test
	public void testCreation() throws IOException {

		AudioFile ht = new DiscAudioFile(("data/tts-test/hi-there.wav"));
		assertTrue(ht.getRawBytes().length == 30764);
		
		
	}

	/**
	 * Ensure that the concatenator produces a valid audio file
	 * @throws IOException
	 */
	@Test
	public void testConcatenation() throws IOException
	{
		AudioFile ht = new DiscAudioFile(("data/tts-test/hi-there.wav"));
		assertTrue(ht.getRawBytes().length == 30764);
		
		File f = WavFileConcatenator.concatenate(Arrays.asList(ht.getStream(), ht.getStream()), "test");
		assertTrue(f.exists());
	}
}
