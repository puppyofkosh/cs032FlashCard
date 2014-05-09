package search;

import java.util.List;

import flashcard.FlashCardSet;

import backend.Resources;

/**
 * Let us perform a search for a specific set
 * @author puppyofkosh
 *
 */
public interface SetSearch {
	public List<FlashCardSet> searchForSets(Resources r);
}
