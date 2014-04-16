package flashcard;

import java.util.List;

import audio.AudioFile;

public interface FlashCardFactory {
	/**
	 * Create flashcard from given info
	 * @param name
	 * @param question
	 * @param answer
	 * @param interval
	 * @param tags
	 * @param set - name of the set it's in (FIXME: Set names may not be unique)
	 * @return
	 */
	public FlashCard create(String name, AudioFile question, AudioFile answer,
			int interval, List<String> tags, String set);
	
	/**
	 * create a flashcard object from an already existing file/directory
	 * @param filePath
	 * @return
	 */
	public FlashCard create(String filePath);
}
