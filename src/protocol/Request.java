package protocol;

import java.io.Serializable;

public interface Request extends Serializable {
	
	public enum RequestType {
		CARD_LIST,
		SET_LIST,
		ALL,
		UPLOAD
	}
	
	RequestType getType();		
}
