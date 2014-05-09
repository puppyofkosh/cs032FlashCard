package protocol;

import java.util.ArrayList;
import java.util.List;

import flashcard.FlashCard;
import flashcard.SerializableFlashCard;

/**
 * Response used by server when cards are requested. Just sends a list of serializable flashcards.
 * @author puppyofkosh
 *
 */
public class CardListResponse implements Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<FlashCard> _cards = new ArrayList<>();
	
	public CardListResponse(List<FlashCard> cards) {
		for (FlashCard c : cards)
		{
			_cards.add(new SerializableFlashCard(c));
		}
	}
	
	@Override
	public ResponseType getType() {
		return ResponseType.SORTED_CARDS;
	}
	
	public List<FlashCard> getSortedCards() {
		return _cards;
	}
}