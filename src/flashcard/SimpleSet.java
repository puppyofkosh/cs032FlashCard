package flashcard;

import java.io.IOException;
import java.util.Collection;

/***
 * Not completely implemented-just contains a name
 * @author puppyofkosh
 *
 */
public class SimpleSet implements FlashCardSet{

	private String name;
	public SimpleSet(String name)
	{
		this.name = name;
	}
	
	@Override
	public Collection<FlashCard> getAll() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTag(String tag) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTag(String tag) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<String> getTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
