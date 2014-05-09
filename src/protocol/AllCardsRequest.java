package protocol;

/**
 * Request for all the cards the server has. We mostly allow this because our
 * server is so small and we have so few cards. In a production environment this
 * should be disabled
 * 
 * @author puppyofkosh
 * 
 */
public class AllCardsRequest implements Request {

	private static final long serialVersionUID = 1L;

	@Override
	public RequestType getType() {
		return RequestType.ALL;
	}
}