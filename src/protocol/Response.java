package protocol;

public interface Response {
	
	public enum ResponseType {
		SORTED_CARDS,
		SORTED_SETS
	}
	
	ResponseType getType();
}
