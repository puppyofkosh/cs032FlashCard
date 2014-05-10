package protocol;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.junit.Test;

import audio.DiscAudioFile;
import audio.MemoryAudioFile;
import flashcard.SerializableFlashCard;

/**
 * Our protocol relies on the classes SerializableFlashCard (and its inner
 * classes) to function, so we test the custom serialization protocls we use for
 * those here
 * 
 * @author puppyofkosh
 * 
 */
public class ProtocolTest {

	/**
	 * Helper methods (FROM STACKOVERFLOW) used to just serialize and
	 * deserialize objects
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	private static <T extends Serializable> byte[] pickle(T obj)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.close();
		return baos.toByteArray();
	}

	private static <T extends Serializable> T unpickle(byte[] b, Class<T> cl)
			throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object o = ois.readObject();
		return cl.cast(o);
	}

	/**
	 * We use a custom/overloaded serialzation method for one of our classes so
	 * we test that here
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testCustomSerialization() throws IOException,
			ClassNotFoundException {

		SerializableFlashCard.AudioData data = new SerializableFlashCard.AudioData();
		data.question = new MemoryAudioFile(new DiscAudioFile(
				"data/flashcard-test/hi-there.wav"));
		data.answer = new MemoryAudioFile(new DiscAudioFile(
				"data/flashcard-test/acronym.wav"));

		byte[] bytes = pickle(data);

		SerializableFlashCard.AudioData unserialized = unpickle(bytes,
				SerializableFlashCard.AudioData.class);

		// Make sure what went in the pipe came out right
		assertTrue(unserialized.answer.getRawBytes().length == data.answer
				.getRawBytes().length);
		// Make sure what came out is a memory audio file and doesn't rely on
		// outside resources
		assertTrue(!(unserialized.answer instanceof DiscAudioFile));
		assertTrue((unserialized.answer instanceof MemoryAudioFile));

	}

	@Test
	public void testMetaData() throws IOException, ClassNotFoundException {
		SerializableFlashCard.MetaData metadata = new SerializableFlashCard.MetaData();
		metadata.name = "Hello there, I am a cat";

		byte[] bytes = pickle(metadata);

		SerializableFlashCard.MetaData unserialized = unpickle(bytes,
				SerializableFlashCard.MetaData.class);

		assertTrue(unserialized.name.equals(metadata.name));
	}

}
