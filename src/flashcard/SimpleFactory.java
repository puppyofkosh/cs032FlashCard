package flashcard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import flashcard.LocallyStoredResources.ResourceData;

import utils.FlashcardConstants;
import utils.Writer;
import audio.AudioFile;
import audio.ByteArrayAudioPlayer;
import audio.DiscAudioFile;
import audio.WavFileConcatenator;

/**
 *
 * @author puppyofkosh
 *
 */
//FIXME uses serialization
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
	
	private static LocallyStoredResources resources = new LocallyStoredResources("resources.ian");
	
	public static LocallyStoredResources getResources()
	{
		return resources;
	}
	
	public SimpleFactory()
	{

	}
	
	
	private static String composeMetadata(FlashCard card) {
		return card.getName() + "\t" +
	           card.getInterval() + "\t" + 
			   card.getSets() + "\t" +
	           card.getTags() + "\t";
	}
	
	public static void writeMetadata(FlashCard card) {
		try(FileWriter rewriter = new FileWriter(new File(card.getPath() + ".INFO.txt"), false)) {
			String new_metadata = FlashcardConstants.METADATA_HEADER
								  + "\n" + composeMetadata(card);	
			rewriter.write(new_metadata);
		} catch (IOException e) {
			Writer.err("Could not change metadata on card", card.getName(), 
					"located at", card.getPath());
			e.printStackTrace();
		}
	}
	
	public static void writeCard(FlashCard card) {

		// Create the directory for the card if it doesn't exist yet
		File dir = new File(card.getPath());
		if (!dir.exists())
			dir.mkdir();
		
		// WRite all files for the card
		writeMetadata(card);
		try {
			// FIXME: This breaks stuff!
			Writer.writeAudioFile(card.getPath(), card.getQuestionAudio().getStream(), true);
			Writer.writeAudioFile(card.getPath(), card.getAnswerAudio().getStream(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		resources.addFlashCard(card);
		resources.save();
	}
	
	public static FlashCard readCard(String filePath)
	{
		LocallyStoredFlashCard.Data data = new LocallyStoredFlashCard.Data();
		data.pathToFile = filePath;
		try {
			FileReader infoReader = new FileReader(filePath + ".INFO.txt");
			BufferedReader bufferedReader = new BufferedReader(infoReader);
			String line;
			String name = "";
			int lineNum = 0;
			while((line = bufferedReader.readLine()) !=null) { //Loops while bufferedReader can find a next line
				if (lineNum != 0) {
					//FIXME predefine regex
					String[] info = line.split("\t");
					data.name = info[0];
					try {
						data.interval = Integer.parseInt(info[1]);
					} catch (NumberFormatException nfe) {
						nfe.printStackTrace();
					}
					// FIXME: we need a notion of sets
					//sets = info[2];
					// replace [ and ] with nothing
					info[3] = info[3].replaceAll("[\\[\\]]", "");
					data.tags = new ArrayList<>(Arrays.asList(info[3].split(", ")));
				}
				lineNum++;
			}
			bufferedReader.close();
		} catch(FileNotFoundException ex) {
			System.out.println(new File(".").getAbsolutePath());
			System.out.println("ERROR: File cannot be found at location " + filePath);
			ex.printStackTrace();
		} catch(IOException ex) {
			System.out.println("Error reading file '" + filePath + "'");
		}

		data.question = new DiscAudioFile(filePath + "q.wav");
		data.answer = new DiscAudioFile(filePath + "a.wav");
		
		return new LocallyStoredFlashCard(data);
	}
	

	public static void main(String[] args) throws IOException, InterruptedException
	{	
		//System.out.println(staticResources.getAllCards());
		
		/// EXAMPLE USE OF READ CARD AND WRITE CARD
		// RUN WITH java -cp "derived/cs032FlashCard.jar:lib/*" flashcard.SimpleFactory "$@"
		// NOTE NOTE NOTE
		// test-card directory must exist before this is called
		// The problem with this is is in Writer.writeAudioFile
		
		// 1) Create an audio flashcard with the following data and write it to file
		// Must make sure directory for the card name exists beforehand!
		
		LocallyStoredFlashCard.Data data = new LocallyStoredFlashCard.Data();
		data.answer = new DiscAudioFile("acronym.wav");
		data.question = new DiscAudioFile("acronym.wav");
		data.pathToFile = "files/test-card/";
		data.name = "test-card";

		LocallyStoredFlashCard card = new LocallyStoredFlashCard(data);

		
		// If you want, write the card to disk
		//SimpleFactory.writeCard(card);
		
		
		
		
		// 2) Read the card in from file
		FlashCard readCard = SimpleFactory.readCard("files/test-card/");
		readCard.setInterval(8);
		System.out.println(readCard.getAnswerAudio().getRawBytes().length);
		System.out.println(readCard.getName());
		
		WavFileConcatenator wCat = new WavFileConcatenator("test-final");
		wCat.concatenate(readCard);
		
		// Just for S & g's play the audio file that's stored by the card we read in
		ByteArrayAudioPlayer testPlayer = new ByteArrayAudioPlayer();
		testPlayer.play(readCard.getAnswerAudio());
		/////////////////////////////////////////////////////////
		
		
		
	}


	@Override
	public FlashCard create(String name, AudioFile question, AudioFile answer,
			int interval, List<String> tags, String set) {
		LocallyStoredFlashCard.Data data = new LocallyStoredFlashCard.Data();
		data.name = name;
		data.question = question;
		data.answer = answer;
		data.interval = interval;
		data.tags = tags;
		data.sets = set;
		
		FlashCard card = new LocallyStoredFlashCard(data);
		writeCard(card);
		return card;
		
	}


	@Override
	public FlashCard create(String filePath) {
		return readCard(filePath);
	}
}

