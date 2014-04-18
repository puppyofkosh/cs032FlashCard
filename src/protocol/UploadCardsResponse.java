package protocol;

public class UploadCardsResponse implements Response{
	
	boolean _confirmed;
	
	UploadCardsResponse(boolean confirmed) {
		_confirmed = confirmed;
	}

	@Override
	public ResponseType getType() {
		return ResponseType.UPLOAD;
	}
	
	public boolean confirmed() {
		return _confirmed;
	}

}
