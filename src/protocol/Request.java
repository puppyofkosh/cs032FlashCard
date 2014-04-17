package protocol;

public interface Request {
	
	public enum RequestType {
		CARD_LIST,
		SET_LIST
	}
	
	RequestType getType();		

}
