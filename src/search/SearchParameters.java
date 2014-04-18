package search;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SearchParameters implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int _numberOfCards = 5;
	private List<String> _tags = new LinkedList<>();
	private String _input = "";
	private int _duration = 4;
	
	//TODO expand this class to make it usable
	
	SearchParameters(String input, List<String> tags, int numberOfCards, int duration) {
		this(input, tags, numberOfCards);
		_duration = duration;
	}
	
	public SearchParameters(String input, int numberOfCards) {
		this(input);
		_numberOfCards = numberOfCards;
	}
	
	SearchParameters(String input, List<String> tags, int numberOfCards) {
		this(input, tags);
		_numberOfCards = numberOfCards;
	}
	
	SearchParameters(String input, List<String> tags) {
		this(input);
		_tags = tags;
	}
	
	public SearchParameters(String input) {
		_input = input;
	}

	/**
	 * @return the _numberOfCards
	 */
	public int get_numberOfCards() {
		return _numberOfCards;
	}

	/**
	 * @return the _tags
	 */
	public List<String> get_tags() {
		return _tags;
	}

	/**
	 * @return the _input
	 */
	public String get_input() {
		return _input;
	}

	/**
	 * @return the _duration
	 */
	public int get_duration() {
		return _duration;
	}

}
