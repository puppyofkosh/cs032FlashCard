package protocol;

/**
 * Just confirms that the sent cards were uploaded (from a UploadCardsRequest)
 * TODO: Maybe combine all of the confirmation responses into one class?
 * @author puppyofkosh
 *
 */
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
