package protocol;

public class UploadCardsResponse implements Response{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean _confirmed;
	
	public UploadCardsResponse(boolean confirmed) {
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
