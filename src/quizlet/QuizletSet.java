package quizlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuizletSet {
	public long id;
	public String name;
	public int terms;
	public String author;
	
	public QuizletSet(JSONObject set) throws JSONException {
		id = set.getLong("id");
		name = set.getString("title");
		terms = set.getInt("term_count");
		author = set.getString("created_by");
	}
	
}
