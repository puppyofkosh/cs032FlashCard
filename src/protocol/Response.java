package protocol;

import java.io.Serializable;

public interface Response extends Serializable {
	
	public enum ResponseType {
		SORTED_CARDS,
		SORTED_SETS,
		UPLOAD,
		META_DATA
	}
	
	ResponseType getType();
}
