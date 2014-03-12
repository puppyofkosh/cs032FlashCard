package flashcard;

import java.util.List;

public interface FlashCardSet {
	// getters for global tags
	
	//?
	List<FlashCard> getAll();
	
	void addTag(String tag);
	void removeTag(String tag);
	
	List<String> getTags();
}
