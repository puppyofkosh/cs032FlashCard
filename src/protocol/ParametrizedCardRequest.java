package protocol;

import search.SearchParameters;

public class ParametrizedCardRequest implements Request {

	private SearchParameters _params;
	
	public ParametrizedCardRequest(SearchParameters params) {
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
