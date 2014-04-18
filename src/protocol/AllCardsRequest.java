package protocol;

public class AllCardsRequest implements Request {

	@Override
	public RequestType getType() {
		return RequestType.ALL;
	}
}