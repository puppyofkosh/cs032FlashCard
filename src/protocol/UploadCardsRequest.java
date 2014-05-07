package protocol;

import java.util.ArrayList;
import java.util.List;

import flashcard.FlashCard;
import flashcard.SerializableFlashCard;

public class UploadCardsRequest implements Request {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<FlashCard> _cards = new ArrayList<>();
	
	public UploadCardsRequest(List<FlashCard> card) {
		for (FlashCard c : card)
		{
			_cards.add(new SerializableFlashCard(c));
		}
	}

	@Override
	public RequestType getType() {
		return RequestType.UPLOAD;
	}
	
	public List<FlashCard> getCardsForUpload() {
		return _cards;
	}
}
