package protocol;

import search.SearchParameters;

public class CardListRequest implements Request {

	private SearchParameters _params;
	
	CardListRequest(SearchParameters params) {
		_params = params;
	}

	@Override
	public RequestType getType() {
		return RequestType.CARD_LIST;
	}
	
	
	public SearchParameters getSearchParameters() {
		return _params;
	}
}
