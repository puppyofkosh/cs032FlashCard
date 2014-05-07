package search;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import controller.Controller;

import backend.AutoCorrector;
import backend.Resources;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

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
		

		
		List<String> candidates = Search.corrector.getEngine().makeSuggestionsOnPhrase(_input);
		
		for (int i = 0; i < 20 && i < candidates.size(); ++i)
		{
			cards.addAll(db.getFlashCardsByName(candidates.get(i)));
		
			try {
				FlashCardSet set = db.getSetByName(candidates.get(i));
				if (set != null)
					cards.addAll(set.getAll());
			} catch (IOException e) {
			}
		}
		// Eliminate duplicates
		return new ArrayList<>(new HashSet<>(cards));
	}

}
