package flashcard;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

public abstract class AbstractFlashCard implements FlashCard, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SerializableFlashCard.MetaData data = new SerializableFlashCard.MetaData();
	
	public AbstractFlashCard(SerializableFlashCard.MetaData d)
	{
		data = new SerializableFlashCard.MetaData(d);
	}
	
	public AbstractFlashCard(FlashCard f)
	{
		data = new SerializableFlashCard.MetaData(f);
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return data.name;
	}

	@Override
	public Collection<String> getTags() {
		// TODO Auto-generated method stub
		return data.tags;
	}

	@Override
	public void addTag(String tag) throws IOException {
		data.tags.add(tag);
	}

	@Override
	public void removeTag(String tag) throws IOException {
		data.tags.remove(tag);
	}

	@Override
	public int getInterval() {
		// TODO Auto-generated method stub
		return data.interval;
	}

	@Override
	public void setInterval(int interval) throws IOException {
		data.interval = interval;
	}

	@Override
	public String getPath() throws IOException {
		return data.pathToFile;
	}

	@Override
	public boolean sameMetaData(FlashCard s)
	{	
		// Make sure both sets have the same tags
		boolean tagEquality = new HashSet<>(getTags()).equals(new HashSet<>(s.getTags()));

		return (tagEquality && s.getName().equals(getName()) && s.getInterval() == getInterval());
	}
	
	@Override
	public String toString()
	{
		return "Flashcard " + data.name;
	}

}