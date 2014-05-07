package backend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import database.DatabaseFactory;
import edu.brown.cs032.autocorrect.backend.AutoCorrectEngine;
import edu.brown.cs032.autocorrect.backend.ranker.DefaultRanker;
import edu.brown.cs032.autocorrect.backend.ranker.SuggestionRanker;
import edu.brown.cs032.autocorrect.backend.structures.CompactTrie;
import edu.brown.cs032.autocorrect.backend.suggestion.CaseIgnorantPrefixGenerator;
import edu.brown.cs032.autocorrect.backend.suggestion.ExactSuggestionGenerator;
import edu.brown.cs032.autocorrect.backend.suggestion.LEDSuggestionGenerator;
import edu.brown.cs032.autocorrect.backend.suggestion.WhiteSpaceSuggestionGenerator;

public class AutoCorrector {
	
	private AutoCorrectEngine engine;
		
	public AutoCorrectEngine getEngine() 
	{
		// Try to see if the available terms have changed. If so, reinitialize
		if (terms.size() != DatabaseFactory.getResources().getAllTags().size())
		{
			init();
		}
		
		return engine;
	}
	
	// use this to compare if our current terms are up to date with what's available
	private List<String> terms;
	
	public AutoCorrector()
	{
		init();
	}
	
	public void init()
	{
		terms = new ArrayList<String>();
		
		terms.addAll(DatabaseFactory.getResources().getAllCardNames());
		terms.addAll(DatabaseFactory.getResources().getAllTags());
		terms.addAll(DatabaseFactory.getResources().getAllSetNames());
		System.out.println(terms.size() + " terms for AC");
		engine = initEngine(terms);
	}
	
	/**
	 * Return an autocorrect engine to use.
	 * 
	 * @return
	 */
	private AutoCorrectEngine initEngine(List<String> suggestions) {
		// Data structures we'll be populating with the CLI args
		CompactTrie dictionary = new CompactTrie();
		// List of words cleaned, in order they were originally for ranker
		ArrayList<String> wordlist = new ArrayList<String>();

		// Initialize the ranker based on whether or not --smart is enabled
		SuggestionRanker ranker;
		ranker = new DefaultRanker();

		// Add all of the provided suggestions to our data structures
		for (String w : suggestions) {
			dictionary.insert(w);
			wordlist.add(w);
		}

		// Store all words once in a list. The prefix generator uses this as
		// having the words in an array seem to allow for faster iteration vs
		// a set or hashset or something
		HashSet<String> wordset = new HashSet<String>(wordlist);
		ArrayList<String> nonDuplicateWordlist = new ArrayList<String>(wordset);

		AutoCorrectEngine engine = new AutoCorrectEngine(ranker);

		// By default we have the "exact" generator, which just produces results
		// for "exact" matches
		engine.addGenerator(new ExactSuggestionGenerator(dictionary));

		// Add other generators

		// Make case insensitive LED generator
		engine.addGenerator(new LEDSuggestionGenerator(nonDuplicateWordlist, 2, true));
		engine.addGenerator(new WhiteSpaceSuggestionGenerator(dictionary));
		engine.addGenerator(new CaseIgnorantPrefixGenerator(dictionary));
		return engine;
	}
}
