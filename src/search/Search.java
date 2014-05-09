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

	// FIXME: This is a pretty bad way of doing auto correct IMO
	public static AutoCorrector corrector = new AutoCorrector();

	// Generate most common words from res/common-words.csv
	// used for automatic quizlet tagging
	public static final Set<String> commonWords = Collections
			.unmodifiableSet(new HashSet<>(FileUtil
					.allLines("res/common-words.csv")));

	public List<FlashCard> search(Resources db);
}
