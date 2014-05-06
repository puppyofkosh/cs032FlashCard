package quizlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuizletRequest {

	public static void main(String[] args) throws UnsupportedEncodingException, IOException, JSONException {
		BufferedReader reader = null;
		try {
			URL url = new URL("https://api.quizlet.com/2.0/search/sets?client_id=h69PJudW8j&q=french&q=animals");
			reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			String line = reader.readLine();
			System.out.println(line);
			JSONObject o = new JSONObject(line);
			JSONArray sets = o.getJSONArray("sets");
			//for(int i )
		}
			finally {
				if (reader != null)
					try {reader.close();}
				catch (IOException e) {}
			
		}
	}
	
	public static JSONArray search(String search) throws JSONException, IOException {
		String fixedSearch = search.replace(" ", "%20");
		URL url;
		url = new URL("https://api.quizlet.com/2.0/search/sets?client_id=h69PJudW8j&autocomplete=1&q=" + fixedSearch);
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
				return new JSONObject(reader.readLine()).getJSONArray("sets");
		}
	}
	
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
