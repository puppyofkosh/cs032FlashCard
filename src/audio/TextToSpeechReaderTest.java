package audio;

import static org.junit.Assert.*;

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
	public void testSmallExample()
	{
		AudioFile a = reader.read("hi there");
		
		// We've stored an identical file-check to see they're the same
		AudioFile expected = new DiskAudioFile(Paths.get("data/tts-test/hi-there.wav"));
		
		assertTrue(Arrays.equals(expected.getRawBytes(), a.getRawBytes()));
	}
	
	/**
	 * "CIT" should come out as CEE EYE TEE
	 */
	@Test
	public void testAcronym()
	{
		String text = "I wish the C.I.T. were open longer hours. Then I'd be able to spend less time outside.";
		
		AudioFile a = reader.read(text);
		// We've stored an identical file-check to see they're the same
		AudioFile expected = new DiskAudioFile(Paths.get("data/tts-test/acronym.wav"));
		
		assertTrue(Arrays.equals(expected.getRawBytes(), a.getRawBytes()));
	}
	
	@Test
	public void testWeirdCharacters()
	{
		String text = "For 3/4 or 75% of his time, Dr. Walker practices for $90 a visit on Dr. Dr., next to King Philip X of St. Lameer St. in Nashua NH.";
		AudioFile a = reader.read(text);
		// We've stored an identical file-check to see they're the same
		AudioFile expected = new DiskAudioFile(Paths.get("data/tts-test/strange-characters.wav"));
		
		System.out.println(expected.getRawBytes().length);
		System.out.println(a.getRawBytes().length);
		
		assertTrue(Arrays.equals(expected.getRawBytes(), a.getRawBytes()));
	}
}
