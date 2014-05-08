package quizlet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A representation of a set from Quizlet
 */
public class QuizletSet {
	public long id;
	public String name;
	public int terms;
	public String author;
	
	/**
	 * Creates a new QuizletSet from a JSONObject
	 * representing a set from Quizlet
	 * @param set
	 * @throws JSONException
	 */
	public QuizletSet(JSONObject set) throws JSONException {
		id = set.getLong("id");
		name = set.getString("title");
		terms = set.getInt("term_count");
		author = set.getString("created_by");
	}
	
}
