package flashcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/***
 * Not completely implemented-just contains a name
 * @author puppyofkosh
 *
 */
public class SimpleSet implements FlashCardSet{

	private String name;
	private Set<FlashCard> cards;
	
	private List<String> globalTags = new ArrayList<>();
	
	
	public SimpleSet(String name)
	{
		this.name = name;
		cards = new HashSet<>();
	}
	
	@Override
	public Collection<FlashCard> getAll() throws IOException {
		return cards;
	}

	@Override
	public void addTag(String tag) throws IOException {
		globalTags.add(tag);
	}

	@Override
	public void removeTag(String tag) throws IOException {
		globalTags.remove(tag);
	}

	@Override
	public Collection<String> getTags() {
		return Collections.unmodifiableCollection(globalTags);
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void addCard(FlashCard f) throws IOException {
		cards.add(f);

	}
	public String getAuthor() {
		// TODO Auto-generated method stub
		return "SAM!";
	}

	@Override
	public int getInterval() {
		// TODO Auto-generated method stub
		return 8;
	}
}
