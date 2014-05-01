package quizlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuizletCard {
	public String term;
	public String definition;
	
	private QuizletCard(JSONObject object) throws JSONException {
		this.term = object.getString("term");
		this.definition = object.getString("definition");
	}
	
	public static QuizletCard[] getCards(JSONObject set) throws JSONException {
		QuizletCard[] output = new QuizletCard[set.getInt("term_count")];
		JSONArray cards = set.getJSONArray("terms");
		for (int i = 0; i < cards.length(); i++) {
			output[i] = new QuizletCard(cards.getJSONObject(i));
		}
		return output;
	}
}
