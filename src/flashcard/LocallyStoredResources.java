package flashcard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.Resources;

/**
 * @author puppyofkosh
 *
 */
public class LocallyStoredResources implements Resources {

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
	private String filename = "";
	
	public LocallyStoredResources(String file)
	{
		filename = file;
		load();
	}
	
	/**
	 * Used so we can initialize an empty resources object and then save it
	 */
	private LocallyStoredResources()
	{
		
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
		{
			cardsWithName = new ArrayList<>();
		}	
		cardsWithName.add(f.getPath());
		data.nameMap.put(f.getName(), cardsWithName);

		data.flashCardFiles.add(f.getPath());
	}
	
	@Override
	public List<String> getFlashCardsByTag(String tag) {
		List<String> results = data.tagMap.get(tag);
		if (results == null)
			return Arrays.asList();
		return results;
	}

	@Override
	public List<FlashCard> getFlashCardsByName(String name) {
		List<String> paths = data.nameMap.get(name);
		if (paths == null)
			return Arrays.asList();
		
		List<FlashCard> cards = new ArrayList<>();
		for (String path : paths)
		{
			cards.add(SimpleFactory.readCard(path));
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
			l.add(SimpleFactory.readCard(s));
		return l;
	}
	
	
	/**
	 * Cute util function that lets us wipe a resources file to an empty state
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.out.println("OPTIONS:\n show -> show all resources \n clear -> clear all resources");
		}
		
		if (args[0].equals("clear"))
		{
			LocallyStoredResources resource = new LocallyStoredResources();
			resource.filename = "resources.ian";
			resource.save();
		}
		else if (args[0].equals("show"))
		{
			LocallyStoredResources resource = new LocallyStoredResources("resources.ian");
			System.out.println(resource.getAllCards());
		}
	}
}
