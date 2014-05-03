package flashcard;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

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

	public static class MetaData implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public String name = "", pathToFile = "";
		public String questionText = "", answerText = "";
		public List<String> tags = new ArrayList<>();
		public int interval = 0;
		
		private UUID uuid = UUID.randomUUID();
		
		public UUID getId()
		{
			return uuid;
		}
		
		public MetaData()
		{
			
		}
		
		public MetaData(MetaData d)
		{
			name = d.name;
			pathToFile = d.pathToFile;
			questionText = d.questionText;
			answerText = d.answerText;
			
			tags = new ArrayList<>(d.tags);
			
			interval = d.interval;
			uuid = d.uuid;
		}

		public void regenerateId()
		{
			uuid = UUID.randomUUID();
		}
		
		public MetaData(FlashCard f)
		{
			name = f.getName();
			// FIXME: Sets!
			try {
				pathToFile = f.getPath();
				tags = new ArrayList<>(f.getTags());
				
				interval = f.getInterval();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			uuid = f.getUniqueId();
		}
	}
	
	public static class AudioData implements Serializable
	{
		public AudioFile question, answer;


		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Override so that we convert question and answer to memory audio files
		 * FIXME: This is a pretty crappy way of doing serialization IMO. Maybe we should make
		 * AudioFiles serializable?
		 * @param stream
		 * @throws IOException
		 */
		private void writeObject(java.io.ObjectOutputStream stream)
	            throws IOException {
			
			MemoryAudioFile q = new MemoryAudioFile(question.getRawBytes());
			MemoryAudioFile a = new MemoryAudioFile(answer.getRawBytes());
			
			stream.writeObject(q);
			stream.writeObject(a);
	    }

		/**
		 * Override so that we can convert question and answer to MemoryAudioFile
		 * @param stream
		 * @throws IOException
		 * @throws ClassNotFoundException
		 */
	    private void readObject(java.io.ObjectInputStream stream)
	            throws IOException, ClassNotFoundException {			
			question = (AudioFile)stream.readObject();
			answer = (AudioFile)stream.readObject();
	    }
		
	}
	
	public static class Data extends MetaData implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private AudioData audioData = new AudioData();
	
		public Data()
		{
			super();
		}
		
		public Data(Data d)
		{
			super(d);
			setQuestion(d.getQuestion());
			setAnswer(d.getAnswer());
		}
		
		public Data(FlashCard f)
		{
			super(f);
			// FIXME: Sets!
			setQuestion(f.getQuestionAudio());
			setAnswer(f.getAnswerAudio());
		}

		public AudioFile getQuestion() {
			return audioData.question;
		}

		public void setQuestion(AudioFile question) {
			audioData.question = question;
		}

		public AudioFile getAnswer() {
			return audioData.answer;
		}

		public void setAnswer(AudioFile answer) {
			audioData.answer = answer;
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
		return data.getQuestion();
	}

	@Override
	public AudioFile getAnswerAudio() {
		return data.getAnswer();
	}
	
	@Override
	public String toString()
	{
		return "Flashcard at " + data.pathToFile;
	}
	
	@Override
	public boolean sameMetaData(FlashCard s)
	{	
		// Make sure both sets have the same tags
		boolean tagEquality = new HashSet<>(getTags()).equals(new HashSet<>(s.getTags()));

		return (tagEquality && s.getName().equals(getName()) && s.getInterval() == getInterval());
	}
	
	@Override
	public int hashCode()
	{
		return getPath().hashCode();
	}

	@Override
	public UUID getUniqueId() {
		// TODO Auto-generated method stub
		return data.getId();
	}
}
