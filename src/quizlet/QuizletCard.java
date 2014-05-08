package quizlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import search.Search;

import controller.Controller;
import audio.AudioFile;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * A class to represent a single Card from Quizlet
 */
public class QuizletCard {
	public String term;
	public String definition;
	
	private QuizletCard(JSONObject object) throws JSONException {
		this.term = object.getString("term");
		this.definition = object.getString("definition");
	}
	
	/**
	 * generates tags for a card
	 * @return A list of generated tags
	 */
	public List<String> generatedTags()
	{
		List<String> tags = new ArrayList<>();
		for (String token : definition.split("[ \\n.,\\t]"))
		{
			String fixed = "";
			try {
				fixed = Controller.parseInput(token);
			} catch (IOException e) {
				break;
			}
			if (fixed.length() == 0)
				break;
			
			if (!Search.commonWords.contains(fixed.toLowerCase()) && tags.size() < 2)
			{
				System.out.println("Adding token " + fixed);
				tags.add(fixed);
			}
		}
		
		return tags;
	}
	/**
	 * Converts a JSONObject representing a Fully detailed Set
	 *  from Quizlet into QuizletCards
	 * @param set a JSONObject representing a set from Quizlet
	 * @return an array of QuizletCard
	 * @throws JSONException if the JSONObject cannot be read
	 */
	public static QuizletCard[] getCards(JSONObject set) throws JSONException {
		QuizletCard[] output = new QuizletCard[set.getInt("term_count")];
		JSONArray cards = set.getJSONArray("terms");
		for (int i = 0; i < cards.length(); i++) {
			output[i] = new QuizletCard(cards.getJSONObject(i));
		}
		return output;
	}
	
	/**
	 * Converts this QuizletCard into a FlashCard
	 * @return a FlashCard that contains the information available
	 * from this QuizletCard
	 */
	public FlashCard getFlashCard() {
		return new QuizletFlashCard();
	}
	
	/**
	 * Converts this QuizletCard into a FlashCard with the input interval
	 * @param interval The interval for the created FlashCard
	 * @return a FlashCard that contains the information available
	 * from this QuizletCard and the input interval
	 */
	public FlashCard getFlashCard(int interval) {
		return new QuizletFlashCard(interval);
	}
	
	/**
	 * A FlashCard to represent a QuizletCard
	 */
	private class QuizletFlashCard implements FlashCard {
		
		AudioFile question;
		AudioFile answer;
		int interval;
		
		/**
		 * Creates a QuizletFlashCard from the parent QuizletCard
		 */
		QuizletFlashCard() {
			question = Controller.readTTS(term);
			answer = Controller.readTTS(definition);
			interval = 0;
		}
		
		/**
		 * Creates a QuizletFlashCard from the parent
		 *  QuizletCard and input interval
		 * @param interval the interval of the created FlashCard
		 */
		QuizletFlashCard(int interval) {
			this();
			this.interval = interval;
		}
		
		@Override
		public String getName() {
			
			return term;
		}

		@Override
		public Collection<FlashCardSet> getSets() {
			// TODO Auto-generated method stub
			return new ArrayList<FlashCardSet>();
		}

		@Override
		public Collection<String> getTags() {
			// TODO Auto-generated method stub
			return new ArrayList<String>();
		}

		@Override
		public void addTag(String tag) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeTag(String tag) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getInterval() {
			// TODO Auto-generated method stub
			return interval;
		}

		@Override
		public void setInterval(int interval) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getPath() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AudioFile getQuestionAudio() {
			// TODO Auto-generated method stub
			return question;
		}

		@Override
		public AudioFile getAnswerAudio() {
			// TODO Auto-generated method stub
			return answer;
		}

		@Override
		public boolean sameMetaData(FlashCard f) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public UUID getUniqueId() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
