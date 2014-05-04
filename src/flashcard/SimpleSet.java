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

	private String name = "";
	private Set<FlashCard> cards = new HashSet<>();
	
	private Set<String> globalTags = new HashSet<>();
	
	private String author = "";
	private int interval = 0;
	
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
		return author;
	}

	@Override
	public int getInterval() {
		// TODO Auto-generated method stub
		return interval;
	}
	
	@Override
	public void setTags(List<String> tags) throws IOException {
		this.globalTags = new HashSet<>(tags);
	}

	@Override
	public void addAuthor() {
		
	}

	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	@Override
	public boolean sameMetaData(FlashCardSet s)
	{	
		// Make sure both sets have the same tags
		boolean tagEquality = new HashSet<>(getTags()).equals(new HashSet<>(s.getTags()));

		return (tagEquality && s.getAuthor().equals(getAuthor()) && s.getName().equals(getName()) && s.getInterval() == getInterval());
	}

	@Override
	public void removeCard(FlashCard f) throws IOException {
		cards.remove(f);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
