package protocol;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import flashcard.SerializableFlashCard;

public interface Request extends Serializable {
	

	
	public enum RequestType {
		CARD_LIST,
		SET_LIST,
		ALL,
		UPLOAD,
		META_DATA
	}
	
	RequestType getType();		
}
