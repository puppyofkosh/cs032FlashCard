package search;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.FileUtil;
import backend.AutoCorrector;
import backend.Resources;
import flashcard.FlashCard;

/**
 * Interface that lets us perform a search for some cards
 * 
 * @author puppyofkosh
 * 
 */
public interface Search {

	// FIXME: This is a pretty shitty way of doing auto correct imo
	public static AutoCorrector corrector = new AutoCorrector();

	// Generate most common words from res/common-words.csv
	public static final Set<String> commonWords = Collections
			.unmodifiableSet(new HashSet<>(FileUtil
					.allLines("res/common-words.csv")));

	public List<FlashCard> search(Resources db);
}
