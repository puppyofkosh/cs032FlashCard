package backend;

import java.util.ArrayList;
import java.util.List;

import flashcard.FlashCard;
import flashcard.FlashCardSet;
import flashcard.FlashCardStub;

public class ResourcesStub {
	
	private static List<FlashCard> selectedCards;

	public List<String> getFlashCardsByTag(String tag) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<FlashCard> getFlashCardsByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public FlashCardSet getSetByName(String setName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<FlashCardSet> getAllSets() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void setSelectedCards(List<FlashCard> selectedCards) {
		ResourcesStub.selectedCards = selectedCards;
	}
	
	public static List<FlashCard> getAllCards() {
		int num = 31;
		String EXAMPLE_PATH = "Example FileSystem/Application Data/CARDS/ABRAHAM LINCOLN'S BIRTHDAY/";
		List<FlashCard> cards = new ArrayList<>(num);
		for(int i = 0; i < num; i++) {
			FlashCard card = new FlashCardStub(EXAMPLE_PATH);
			cards.add(card);
		}
		return cards;
	}
	

}
