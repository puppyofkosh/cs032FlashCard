package protocol;

import search.Search;

/**
 * Request meta data about a specific card (used before cards are imported,
 * while the user is selecting which cards to import)
 * 
 * @author puppyofkosh
 * 
 */
public class MetaDataCardRequest implements Request {
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
