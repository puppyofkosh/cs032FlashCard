package protocol;

/**
 * Application layer response that just says "Okay I'm here"
 * @author puppyofkosh
 *
 */
public class ConnectionSuccessfulResponse implements Response {

	private static final long serialVersionUID = 1L;

	@Override
	public ResponseType getType() {
		// TODO Auto-generated method stub
		return ResponseType.CONNECTION_SUCCESSFUL;
	}

}
