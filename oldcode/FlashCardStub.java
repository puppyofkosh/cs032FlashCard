package flashcard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import utils.Writer;
import audio.AudioFile;
import audio.AudioFileStub;

/**
 * Deprecated, should not be used except for testing.
 * @author samkortchmar
 *
 */
public class FlashCardStub implements FlashCard, Serializable {

	private static final long serialVersionUID = -5514013016523354268L;
	String name, sets, pathToFile;
	String questionText, answerText = "";
	List<String> tags;
	AudioFile question, answer;
	int interval;

	FlashCardStub(String name, AudioFile question, AudioFile answer,
			int interval, List<String> tags, String set) {
		this.name = name;
		this.question = question;
		this.answer = answer;
		this.interval = interval;
		this.tags = tags;
		this.sets = set;
	}

	FlashCardStub(String name, AudioFile question, String questionText,
			AudioFile answer, String answerText, int interval, List<String> tags,  String set) {
		this(name, question, answer, interval, tags, set);
		this.questionText = questionText;
		this.answerText = answerText;
	}

	public FlashCardStub(String filePath) {

		//Read the metadata, really ugly and not at all how we'll actually do it.
		try {
			FileReader infoReader = new FileReader(filePath + ".INFO.txt");
			BufferedReader bufferedReader = new BufferedReader(infoReader);
			String line;
			int lineNum = 0;
			while((line = bufferedReader.readLine()) !=null) { //Loops while bufferedReader can find a next line
				if (lineNum != 0) {
					//FIXME predefine regex
					String[] info = line.split("\t");
					name = info[0];
					try {
						interval = Integer.parseInt(info[1]);
					} catch (NumberFormatException nfe) {
						nfe.printStackTrace();
					}
					sets = info[2];
					tags = Arrays.asList(info[3].split(","));
				}
				lineNum++;
			}
			bufferedReader.close();
			pathToFile = filePath;
		} catch(FileNotFoundException ex) {
			System.out.println(new File(".").getAbsolutePath());
			System.out.println("ERROR: File cannot be found at location " + filePath);
			ex.printStackTrace();
		} catch(IOException ex) {
			System.out.println("Error reading file '" + filePath + "'");
		}

		//Read the audio files.
		try {
			this.question = new AudioFileStub(AudioSystem.getAudioInputStream(new File(filePath + "q.wav")));
			this.answer = new AudioFileStub(AudioSystem.getAudioInputStream(new File(filePath + "a.wav")));
		} catch (UnsupportedAudioFileException | IOException e) {
			System.out.println("Could not read files");
			e.printStackTrace();
		}	
		Writer.debug("Flashcard Made!");
	}

	public AudioFile getQuestionAudio() {
		return question;
	}

	public AudioFile getAnswerAudio() {
		return answer;
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<FlashCardSet> getSets() {
		String[] setArray = sets.split(",");
		List<FlashCardSet> setList = new LinkedList<>();
		for(String s : setArray) {
			setList.add(new SimpleSet(s.trim()));
		}
		return setList;
	}

	@Override
	public Collection<String> getTags() {
		return tags;
	}

	@Override
	public void addTag(String tag) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getInterval() {
		// TODO Auto-generated method stub
		return interval;
	}

	@Override
	public void setInterval(int interval) throws IOException {
		// TODO Auto-generated method stub

	}
	
	@Override
	public String toString() {
		return String.format("Flashcard %s is in set %s with tags %s and interval %s", name, sets, tags, interval);
	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeTag(String tag) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean sameMetaData(FlashCard f) {
		// TODO Auto-generated method stub
		return false;
	}




}
