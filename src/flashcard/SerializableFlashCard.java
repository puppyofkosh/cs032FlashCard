package flashcard;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import utils.Writer;
import audio.AudioFile;
import audio.MemoryAudioFile;


/**
 * Somewhat legit implementation of a flashcard that stores its data in memory
 * and on file, only writing/reading to file when updates are made
 * @author puppyofkosh
 *
 */
public class SerializableFlashCard implements FlashCard, Serializable{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class Data implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String name = "", pathToFile = "";
		public String questionText = "", answerText = "";
		public List<String> tags = new ArrayList<>();
		public AudioFile question, answer;
		public int interval = 0;
	
		public Data()
		{
			
		}
		
		public Data(Data d)
		{
			name = d.name;
			pathToFile = d.pathToFile;
			questionText = d.questionText;
			answerText = d.answerText;
			
			tags = new ArrayList<>(d.tags);
			question = d.question;
			answer = d.answer;
			
			interval = d.interval;
		}
		
		public Data(FlashCard f)
		{
			name = f.getName();
			// FIXME: Sets!
			try {
				pathToFile = f.getPath();
				tags = new ArrayList<>(f.getTags());
				
				question = f.getQuestionAudio();
				answer = f.getAnswerAudio();
				interval = f.getInterval();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		/**
		 * Override so that we convert question and answer to memory audio files
		 * FIXME: This is a pretty crappy way of doing serialization IMO. Maybe we should make
		 * AudioFiles serializable?
		 * @param stream
		 * @throws IOException
		 */
		private void writeObject(java.io.ObjectOutputStream stream)
	            throws IOException {
			stream.writeObject(name);
			stream.writeObject(pathToFile);
			stream.writeObject(questionText);
			stream.writeObject(answerText);
			stream.writeObject(tags);
			
			MemoryAudioFile q = new MemoryAudioFile(question.getRawBytes());
			MemoryAudioFile a = new MemoryAudioFile(answer.getRawBytes());
			
			stream.writeObject(q);
			stream.writeObject(a);
			stream.writeInt(interval);
	    }

		/**
		 * Override so that we can convert question and answer to MemoryAudioFile
		 * @param stream
		 * @throws IOException
		 * @throws ClassNotFoundException
		 */
	    private void readObject(java.io.ObjectInputStream stream)
	            throws IOException, ClassNotFoundException {
	    	name = (String)stream.readObject();
			pathToFile = (String)stream.readObject();
			questionText = (String)stream.readObject();
			answerText = (String)stream.readObject();
			tags = new ArrayList<>();
			
			Object o = stream.readObject();
			if ((o instanceof List<?>))
			{
				List<?> vals = (List<?>)o;
				for (Object v : vals)
				{
					if (v instanceof String)
					{
						tags.add((String)v);
					}
				}
			}
			
			question = (AudioFile)stream.readObject();
			answer = (AudioFile)stream.readObject();
			interval = stream.readInt();
	    }
	}
	
	/**
	 * Given some information about a flashcard, determine where it should be stored
	 * @param d
	 * @return
	 */
	public static String makeFlashCardPath(Data d)
	{
		// FIXME: Choose a better convention, if possible, based on sets
		return "files/" + d.name + "/";
	}
	
	private Data data;
	
	public SerializableFlashCard(Data data)
	{
		this.data = new Data(data);
	}
	
	public SerializableFlashCard(FlashCard f)
	{
		this.data = new Data(f);
	}
	
	
	@Override
	public String getName() {
		return data.name;
	}

	// FIXME: Implement
	@Override
	public Collection<FlashCardSet> getSets() {
		return Arrays.asList();
	}

	@Override
	public Collection<String> getTags() {
		return data.tags;
	}

	@Override
	public void addTag(String tag) throws IOException {
		data.tags.add(tag);
	}
	
	public void removeTag(String tag) throws IOException {
		data.tags.remove(tag);
	}

	@Override
	public int getInterval() {
		return data.interval;
	}

	@Override
	public void setInterval(int interval) throws IOException {
		data.interval = interval;
		Writer.out("Changing interval");
	}

	@Override
	public String getPath() {
		return data.pathToFile;
	}

	@Override
	public AudioFile getQuestionAudio() {
		return data.question;
	}

	@Override
	public AudioFile getAnswerAudio() {
		return data.answer;
	}
	
	@Override
	public String toString()
	{
		return "Flashcard at " + data.pathToFile;
	}
}
