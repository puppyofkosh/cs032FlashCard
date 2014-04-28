package audio;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Test;

public class TextToSpeechReaderTest {

	TextToSpeechReader reader;
	public TextToSpeechReaderTest() throws IOException
	{
		reader = new FreeTTSReader();
	}
	
	@Test
	public void testSetup() throws IOException {
		TextToSpeechReader r = new FreeTTSReader();
	}
	
	@Test
	public void testSmallExample() throws IOException
	{
		AudioFile a = reader.read("hi there");
		
		// We've stored an identical file-check to see they're the same
		AudioFile expected = new DiscAudioFile(("data/tts-test/hi-there.wav"));
		
		assertTrue(Arrays.equals(expected.getRawBytes(), a.getRawBytes()));
	}
	
	/**
	 * "CIT" should come out as CEE EYE TEE
	 * @throws IOException 
	 */
	@Test
	public void testAcronym() throws IOException
	{
		String text = "I wish the C.I.T. were open longer hours. Then I'd be able to spend less time outside.";
		
		AudioFile a = reader.read(text);
		// We've stored an identical file-check to see they're the same
		AudioFile expected = new DiscAudioFile(("data/tts-test/acronym.wav"));
		
		assertTrue(Arrays.equals(expected.getRawBytes(), a.getRawBytes()));
	}

}
