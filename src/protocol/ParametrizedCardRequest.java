package protocol;

import search.Search;

public class ParametrizedCardRequest implements Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Search _params;
	
	public ParametrizedCardRequest(Search params) {
		_params = params;
	}

	@Override
	public RequestType getType() {
		return RequestType.CARD_LIST;
	}
	
	
	public Search getSearchParameters() {
		return _params;
	}
}
