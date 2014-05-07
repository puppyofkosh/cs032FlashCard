package protocol;

import search.Search;

public class MetaDataCardRequest implements Request{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Search _params;
	
	public MetaDataCardRequest(Search params) {
		_params = params;
	}

	@Override
	public RequestType getType() {
		return RequestType.PARAMATERIZED_META_DATA;
	}
	
	
	public Search getSearchParameters() {
		return _params;
	}
}
