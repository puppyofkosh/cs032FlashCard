package quizlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class for making requests to the Quizlet API
 */
public class QuizletRequest {
	
	/**
	 * Search the Quizlet database using the input search
	 * @param search the search term to use
	 * @return the JSONArray containing the sets that Quizlet returned
	 * @throws JSONException if The JSON returned by Quizlet
	 * is not properly formatted
	 * @throws IOException if there is an error in I/O
	 */
	public static JSONArray search(String search) throws JSONException, IOException {
		String fixedSearch = search.replace(" ", "%20");
		URL url;
		url = new URL("https://api.quizlet.com/2.0/search/sets?client_id=h69PJudW8j&autocomplete=1&q=" + fixedSearch);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
				return new JSONObject(reader.readLine()).getJSONArray("sets");
		}
	}
	
	/**
	 * gets the set from Quizlet with the input id
	 * @param id the id of the set to get
	 * @return The JSONObject returned by Quizlet
	 */
	public static JSONObject getSet(String id) {
		try {
			URL url = new URL("https://api.quizlet.com/2.0/sets/" + id + "?client_id=h69PJudW8j");
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
				return new JSONObject(reader.readLine());
			} catch (JSONException | IOException e1) {
				return null;
			}
		}
		catch (MalformedURLException e2)
		{
			return null;
		}
		
	}
}
