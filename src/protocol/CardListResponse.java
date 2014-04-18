package protocol;

import java.util.List;

import flashcard.FlashCard;

public class CardListResponse implements Response {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<FlashCard> _cards;
	
	public CardListResponse(List<FlashCard> cards) {
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