package protocol;

import java.io.Serializable;

/**
 * A request is a "thing" sent from a client to the server. The server casts the
 * request to a specific type based on the getType method, and acts accordingly.
 * 
 * @author puppyofkosh
 * 
 */
public interface Request extends Serializable {

	public enum RequestType {
		CARD_LIST, SET_LIST, ALL, UPLOAD, ALL_META_DATA, PARAMATERIZED_META_DATA
	}

	RequestType getType();
}
