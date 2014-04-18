package flashcard;

import java.io.IOException;
import java.util.Collection;

import audio.AudioFile;
/**
 * 
 * @author samkortchmar/ian boros/peter krishner
 *The interface for the FlashCard objects that are the core of this project
 *Probably we will only have one class that implements this interface, but
 *we wanted to make it an interface to create stubs.
 * 
 * Sam
 * 
 * 
 */
public interface FlashCard {

/*FlashCards will be created only after all the information in the constructors
 * below has been collected by our FlashCardFactory class - this way we avoid
 * problems with cards that are in intermediate states. When a FlashCard is created
 * in this way it creates a FlashCard on the filesystem.*/
		/*FlashCard(String name, AudioFile question, AudioFile answer,
		 *  int interval, List<String> tags, String set);*/	
		/*FlashCard(String name, AudioFile question, String questionText,
		 *  AudioFile answer, String answerText, int interval, List<String> tags,  String set);*/
	
/*This constructor builds a FlashCard object form the filePath to the FlashCard on the filesystem.
		/*FlashCard(String filePath)*/
	
	/**
	 * Returns the name of the current FlashCard as a string.
	 * @return
	 */
	String getName();
	/**
	 * This method gets all sets a given FlashCard is a member of. Note this
	 * information is not actually stored in the FlashCard object, but in
	 * the sets.info file we will maintain.
	 * @return - A collection of all the Sets this FlashCard is a member of.
	 */
	Collection<FlashCardSet> getSets();
	
	/**
	 * This method gets all the tags that this FlashCard has attached to it.
	 * This information will be retrieved from the FlashCard Metadata file
	 * in the FlashCard folder.
	 * @return - A collection of the tags associated with this FlashCard.
	 */
	Collection<String> getTags();
	
	/**
	 * This method adds a tag to the current FlashCard by adding the tag string
	 * to the current flashCards Metadata file. It also adds the card-tag pair 
	 * to the current tag index file.
	 * @param tag - the tag to be added.
	 * @throws IOException - if the current FlashCard's Metadata file cannot be
	 * found.
	 */
	void addTag(String tag) throws IOException;
	
	/**
	 * Returns the interval for the current FlashCard. This information will be
	 * in the metaData file but also be loaded into the buffered FlashCards from
	 * getFlashCards in Resources.
	 * @return - the interval between the question and the answer of the card.
	 */
	int getInterval();
	
	/**
	 * Sets the interval of the current FlashCard. This method is for 
	 * retroactively setting the card's interval, after it has been made and
	 * read by the Resources class. This method will affect both the buffered
	 * version and on-disk version of the FlashCard, hence the IOException
	 * 
	 * @param interval - the interval to be set.
	 * @throws IOException - if the filesystem does not contain the specified
	 * FlashCard.
	 */
	void setInterval(int interval) throws IOException;
	
	/**
	 * Returns the path this card is stored in (relative to the project folder)
	 * @return
	 */
	String getPath();
	AudioFile getQuestionAudio();
	
	AudioFile getAnswerAudio();

}
