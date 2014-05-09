package flashcard;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.UUID;

import utils.NumberAwareStringComparator;

/**
 * "Abstract" flashcard imploementation that provides a bunch of random useful
 * stuff and trivial method implementations
 * 
 * @author puppyofkosh
 * 
 */
public abstract class AbstractFlashCard implements FlashCard, Serializable {

	/**
	 * Compare flashcards by name (DOES NOT use lexigraphic ordering, but rather
	 * a different scheme so numbers "matter." For instance "1st president" will
	 * come before "10th president"
	 * 
	 * @author puppyofkosh
	 * 
	 */
	public static class NameComparator implements Comparator<FlashCard> {
		@Override
		public int compare(FlashCard a, FlashCard b) {
			return NumberAwareStringComparator.INSTANCE.compare(a.getName(),
					b.getName());
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SerializableFlashCard.MetaData data = new SerializableFlashCard.MetaData();

	public AbstractFlashCard(SerializableFlashCard.MetaData d) {
		data = new SerializableFlashCard.MetaData(d);
	}

	@Override
	public int hashCode() {
		return data.getId().hashCode();
	}

	public AbstractFlashCard(FlashCard f) {
		data = new SerializableFlashCard.MetaData(f);
	}

	@Override
	public String getName() {
		return data.name;
	}

	@Override
	public Collection<String> getTags() {
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
	public boolean sameMetaData(FlashCard s) {
		// Make sure both sets have the same tags
		boolean tagEquality = new HashSet<>(getTags()).equals(new HashSet<>(s
				.getTags()));

		return (tagEquality && s.getName().equals(getName()) && s.getInterval() == getInterval());
	}

	@Override
	public String toString() {
		return "Flashcard " + data.name;
	}

	@Override
	public UUID getUniqueId() {
		return data.getId();
	}
}
