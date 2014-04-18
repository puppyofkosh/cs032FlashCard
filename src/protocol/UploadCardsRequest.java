package protocol;

import java.util.List;

import flashcard.FlashCard;

public class UploadCardsRequest implements Request {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<FlashCard> _cards;
	
	public UploadCardsRequest(List<FlashCard> card) {
		_cards = card;
	}

	@Override
	public RequestType getType() {
		return RequestType.UPLOAD;
	}
	
	public List<FlashCard> getCardsForUpload() {
		return _cards;
	}
}
