package flashcard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import backend.SimpleResources;

import audio.AudioFile;
import audio.BasicAudioPlayer;
import audio.DiscAudioFile;
import audio.MemoryAudioFile;

/**
 * Fixme: uses serialization
 * @author puppyofkosh
 *
 */
public class SimpleFactory implements FlashCardFactory{	
	/**
	 * What it writes to disk. Just a hack using java serialization
	 * @author puppyofkosh
	 *
	 */
	public static class FlashCardData implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * FIXME: These public fields are unsafe
		 */
		public byte[] questionBytes;
		public byte[] answerBytes;
		public int interval;
		public List<String> tags;
		public List<String> setNames;
		public String name;
		
		public FlashCardData(byte[] question, byte[] answer, int interval, List<String> tags, List<String> setNames, String name)
		{
			this.questionBytes = question;
			this.answerBytes = answer;
			this.tags = tags;
			this.interval = interval;
			this.setNames = setNames;
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return "Flashcard Data: question: " + questionBytes.length + " answer: " + answerBytes.length + " tags: " + tags;
		}
	}
	
	private SimpleResources resources = new SimpleResources();
	
	public SimpleFactory()
	{
		resources.setFactory(this);
		resources.load();
	}
	
	public SimpleResources getResources()
	{
		return resources;
	}
	
	@Override
	public FlashCard create(String name, AudioFile question, AudioFile answer,
			int interval, List<String> tags, String set) {
		
		FlashCardData data = new FlashCardData(question.getRawBytes(), answer.getRawBytes(), interval, tags, Arrays.asList(set), name);
		String filename = "files/" + set + "-" + name + ".ian";
		saveData(filename, data);

		FlashCard card = new SimpleFlashCard(filename, this);
		resources.addFlashCard(card);
		resources.save();
		return card;
	}
	
	
	public FlashCardData getData(String filePath)
	{
		
		try {
			FileInputStream fin = new FileInputStream(filePath);
			ObjectInputStream objectIn = new ObjectInputStream(fin);
			
			Object o = objectIn.readObject();
			
			objectIn.close();
			
			FlashCardData data = (FlashCardData)o;
			return data;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public FlashCard create(String filePath) {
		return new SimpleFlashCard(filePath, this);
	}
	

	public void saveData(String filename, FlashCardData data)
	{
		ObjectOutputStream objectOut;
		try {
			FileOutputStream fileOut = new FileOutputStream(filename);

			objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(data);
			objectOut.close();

		} catch (IOException e) {
			System.out.println("Couldn't write to file");
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		SimpleFactory f = new SimpleFactory();
		
		//SimpleResources r = new SimpleResources();
		//r.save();
		//r.load();
		//System.out.println(r.getFlashCardsByName("a"));
		
		
		// FIXME: Watch out for the saving and loading problems!
		System.out.println(f.getResources().getFlashCardsByTag("tag 4").get(0));
		
		
		
		//f.create("test", new DiscAudioFile("tmp.wav"), new DiscAudioFile("tmp.wav"), 3, Arrays.asList("tag 1"), "set");
		//FlashCard card = f.create("set-test.ian");
		//System.out.println(card.getInterval());
		//System.out.println(card.getTags());
		/*f.create("a", new DiscAudioFile("tmp.wav"), new DiscAudioFile("tmp.wav"), 3, Arrays.asList("tag 1", "tag 2"), "set1");
		f.create("b", new DiscAudioFile("tmp.wav"), new DiscAudioFile("tmp.wav"), 3, Arrays.asList("tag 2"), "set2");
		f.create("c", new DiscAudioFile("tmp.wav"), new DiscAudioFile("tmp.wav"), 3, Arrays.asList("tag 3"), "set3");
		f.create("d", new DiscAudioFile("tmp.wav"), new DiscAudioFile("tmp.wav"), 3, Arrays.asList("tag 1", "tag 4"), "set2");
		f.create("e", new DiscAudioFile("tmp.wav"), new DiscAudioFile("tmp.wav"), 3, Arrays.asList("tag 1"), "set1");
		f.create("f", new DiscAudioFile("tmp.wav"), new DiscAudioFile("tmp.wav"), 3, Arrays.asList("tag 1"), "set1");
		f.create("g", new DiscAudioFile("tmp.wav"), new DiscAudioFile("tmp.wav"), 3, Arrays.asList("tag 1"), "set2");
		*/
		// FIXME: Wtf? This crashes on ubuntu 12.10
		//AudioFile start = new DiscAudioFile("hi-there.wav");
		//AudioFile end = new MemoryAudioFile(start.getRawBytes());
		//BasicAudioPlayer p = new BasicAudioPlayer();
		//p.play(start);
		//p.play(end);
	}
	
}
