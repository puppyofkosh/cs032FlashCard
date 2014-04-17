package protocol;

public class CardListRequest implements Request {

	@Override
	public RequestType getType() {
		return RequestType.CARD_LIST;
	}
	
	public SearchParameters getSearchParamaters() {
		
	}
	
	

}
