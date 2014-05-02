package search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import backend.Resources;
import flashcard.FlashCard;

public class SearchParameters implements Serializable, Search {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _input = "";
	
	public SearchParameters(String input) {
		_input = input;
	}

	/**
	 * @return the _input
	 */
	public String get_input() {
		return _input;
	}


	@Override
	public List<FlashCard> search(Resources db) {
		List<FlashCard> cards = new ArrayList<>();

		String[] searchTokens = _input.split("[, ]");
		
		for (String token : searchTokens)
		{
			cards.addAll(db.getFlashCardsWithTag(token));
			cards.addAll(db.getFlashCardsByName(token));
		}
		
		cards.addAll(db.getFlashCardsByName(_input));
		
		// Eliminate duplicates
		return new ArrayList<>(new HashSet<>(cards));
	}

}
