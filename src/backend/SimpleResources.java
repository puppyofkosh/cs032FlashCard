package backend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flashcard.FlashCard;
import flashcard.FlashCardFactory;
import flashcard.FlashCardSet;
import flashcard.SimpleFactory.FlashCardData;


/***
 * Resources implementation that uses serialized index files to keep track of things
 * FIXME: This totally sucks
 * @author puppyofkosh
 *
 */
public class SimpleResources implements Resources{

	/**
	 * Data structures that let us quickly search for cards based on _stuff_
	 * @author puppyofkosh
	 *
	 */
	public static class ResourceData implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		// [tag -> list of flash card file names with that tag]
		public Map<String, List<String>> tagMap = new HashMap<>();
		
		
		// [name -> list of file names with that name]
		public Map<String, List<String>> nameMap = new HashMap<>();
		
		// "index" of all flashcard file names that exist
		public List<String> flashCardFiles = new ArrayList<>();
	}
	
	private ResourceData data = new ResourceData();
	private FlashCardFactory factory = null;
	
	public void setFactory(FlashCardFactory fac)
	{
		factory = fac;
	}
	
	String filename = "resources.ian";
	
	public SimpleResources(String file)
	{
		filename = file;
	}
	
	public SimpleResources()
	{
		
	}
	
	public void addFlashCard(FlashCard f)
	{
		for (String tag : f.getTags())
		{
			List<String> taggedCards = data.tagMap.get(tag);
			if (taggedCards == null)
				taggedCards = new ArrayList<>();
			taggedCards.add(f.getPath());
			
			// Not necessary unless taggedCards was null, just being super verbose
			data.tagMap.put(tag, taggedCards);
		}
		
		List<String> cardsWithName = data.tagMap.get(f.getName());
		if (cardsWithName == null)
			cardsWithName = new ArrayList<>();
			
		cardsWithName.add(f.getPath());
		data.nameMap.put(f.getName(), cardsWithName);
		
		data.flashCardFiles.add(f.getPath());
	}
	
	public void load()
	{
		try {
			FileInputStream fin = new FileInputStream(filename);
			ObjectInputStream objectIn = new ObjectInputStream(fin);
			
			Object o = objectIn.readObject();
			
			objectIn.close();
			
			ResourceData data = (ResourceData)o;
			this.data = data;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void save()
	{
		ObjectOutputStream objectOut;
		try {
			FileOutputStream fileOut = new FileOutputStream(filename);

			objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(data);
			objectOut.close();

		} catch (IOException e) {
			System.out.println("Couldn't write to file");
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Not sure why interface specified returning strings
	 */
	@Override
	public List<String> getFlashCardsByTag(String tag) {
		List<String> results = data.tagMap.get(tag);
		if (results == null)
			return Arrays.asList();
		return results;
	}

	/**
	 * FIXME: Should do a legit search by name, not by filename
	 */
	@Override
	public List<FlashCard> getFlashCardsByName(String name) {

		List<String> paths = data.nameMap.get(name);
		if (paths == null)
			return Arrays.asList();
		
		List<FlashCard> cards = new ArrayList<>();
		for (String path : paths)
		{
			cards.add(factory.create(path));
		}
		return cards;
	}

	@Override
	public FlashCardSet getSetByName(String setName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FlashCardSet> getAllSets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FlashCard> getAllCards() {
		List<FlashCard> l = new ArrayList<>();
		for (String s : data.flashCardFiles)
			l.add(factory.create(s));
		return l;
	}

}
