package protocol;

public class AllCardsRequest implements Request {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public RequestType getType() {
		return RequestType.ALL;
	}
}