package flashcard;

import java.io.IOException;
import java.util.List;

public interface FlashCard {
	
/*FlashCard(String name, AudioFile question, AudioFile answer,
 *  int interval, List<String> tags, String set);*/	
/*FlashCard(String name, AudioFile question, String questionText,
 *  AudioFile answer, String answerText, int interval, List<String> tags,  String set);*/
	
	// Getters for sets, tags, interval
	
	List<FlashCardSet> getSets();
	void addSet(FlashCardSet s) throws IOException;
	
	List<String> getTags();
	void addTag(String tag) throws IOException;
	
	int getInterval();
	void setInterval(int interval) throws IOException;

}
