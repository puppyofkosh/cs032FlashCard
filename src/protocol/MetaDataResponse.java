package protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Return a bunch of NetworkedFlashCards to the client with this class.
 * NetworkedFlashCards only contain the metadata about each card. To get the
 * audio, the client will have to explicitly "download" the card
 * 
 * @author puppyofkosh
 * 
 */
public class MetaDataResponse implements Response {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<NetworkedFlashCard> _cards = new ArrayList<>();

	public MetaDataResponse(List<NetworkedFlashCard> cards) {
		for (NetworkedFlashCard c : cards) {
			_cards.add(c);
		}
	}

	public List<NetworkedFlashCard> getSortedCards() {
		return Collections.unmodifiableList(_cards);
	}

	@Override
	public ResponseType getType() {
		// TODO Auto-generated method stub
		return ResponseType.META_DATA;
	}

	/**
	 * Request all meta data from the server
	 * @author puppyofkosh
	 *
	 */
	public static class MetaDataRequest implements Request
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public RequestType getType() {
			// TODO Auto-generated method stub
			return RequestType.META_DATA;
		}
		
	}

}
