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

public class QuizletCard {
	public String term;
	public String definition;
	
	private QuizletCard(JSONObject object) throws JSONException {
		this.term = object.getString("term");
		this.definition = object.getString("definition");
	}
	
	public List<String> generatedTags()
	{
		List<String> tags = new ArrayList<>();
		for (String token : definition.split(" "))
		{
			if (!Search.commonWords.contains(token.toLowerCase()) && tags.size() < 2)
			{
				System.out.println("Adding token " + token);
				tags.add(token);
			}
		}
		
		return tags;
	}
	
	public static QuizletCard[] getCards(JSONObject set) throws JSONException {
		QuizletCard[] output = new QuizletCard[set.getInt("term_count")];
		JSONArray cards = set.getJSONArray("terms");
		for (int i = 0; i < cards.length(); i++) {
			output[i] = new QuizletCard(cards.getJSONObject(i));
		}
		return output;
	}
	
	public FlashCard getFlashCard() {
		return new QuizletFlashCard();
	}
	
	public FlashCard getFlashCard(int interval) {
		return new QuizletFlashCard(interval);
	}
	
	private class QuizletFlashCard implements FlashCard {
		
		AudioFile question;
		AudioFile answer;
		int interval;
		
		QuizletFlashCard() {
			question = Controller.readTTS(term);
			answer = Controller.readTTS(definition);
			interval = 0;
		}
		
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
