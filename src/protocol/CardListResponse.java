package protocol;

import java.util.List;

import flashcard.FlashCard;

public class CardListResponse implements Response {

	private List<FlashCard> _cards;
	
	CardListResponse(List<FlashCard> cards) {
		_cards = cards;
	}
	
	@Override
	public ResponseType getType() {
		return ResponseType.SORTED_CARDS;
	}
	
	public List<FlashCard> getSortedCards() {
		return _cards;
	}

}
