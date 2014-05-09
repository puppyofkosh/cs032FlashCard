package protocol;

import java.io.Serializable;

/**
 * A response is a thing sent from a server to the client (after a Request is
 * sent to the server)
 * 
 * @author puppyofkosh
 * 
 */
public interface Response extends Serializable {

	public enum ResponseType {
		CONNECTION_SUCCESSFUL, SORTED_CARDS, SORTED_SETS, UPLOAD, META_DATA
	}

	ResponseType getType();
}
