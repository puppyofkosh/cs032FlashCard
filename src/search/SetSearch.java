package search;

import java.util.List;

import flashcard.FlashCardSet;

import backend.Resources;

public interface SetSearch {
	public List<FlashCardSet> searchForSets(Resources r);
}
