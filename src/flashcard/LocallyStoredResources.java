package flashcard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
	
	@Override
	public List<FlashCard> getFlashCardsWithTag(String tag)
	{
		return null;
	}
		
	public void load()
	{
		loadHumanReadable();
		
		/*try {
			FileInputStream fin = new FileInputStream(filename);
			ObjectInputStream objectIn = new ObjectInputStream(fin);
			
			Object o = objectIn.readObject();
			
			objectIn.close();
			
			ResourceData data = (ResourceData)o;
			
			// FIXME: To use serialized version uncomment this
			//this.data = data;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public void saveHumanReadable()
	{
		// Write the tag hash map
		try(FileWriter rewriter = new FileWriter(new File("index/tag-index.dat"), false)) {
			for (Map.Entry<String, List<String>> e : data.tagMap.entrySet())
			{
				rewriter.write(e.getKey() + "\t" + e.getValue() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Write the name map
		try(FileWriter rewriter = new FileWriter(new File("index/name-index.dat"), false)) {
			for (Map.Entry<String, List<String>> e : data.nameMap.entrySet())
			{
				rewriter.write(e.getKey() + "\t" + e.getValue() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Write the list of available cards
		try(FileWriter rewriter = new FileWriter(new File("index/names.dat"), false)) {
			for (String path : data.flashCardFiles)
			{
				rewriter.write(path + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void loadHumanReadable()
	{
		String filePath = "index/tag-index.dat";
			
		// Read in the tags
		try {
			FileReader infoReader = new FileReader(filePath);
			BufferedReader bufferedReader = new BufferedReader(infoReader);
			String line;
			while((line = bufferedReader.readLine()) !=null) { //Loops while bufferedReader can find a next line
					//FIXME predefine regex
					String[] info = line.split("\t");
					String tag = info[0];
					
					String pathsString = info[1];
					pathsString = pathsString.replaceAll("[\\[\\]]", "");
					
					String[] paths = pathsString.split(", ");
					data.tagMap.put(tag, new ArrayList<>(Arrays.asList(paths)));
			}
			bufferedReader.close();
		} catch(FileNotFoundException ex) {
			System.out.println(new File(".").getAbsolutePath());
			System.out.println("ERROR: File cannot be found at location " + filePath);
			ex.printStackTrace();
		} catch(IOException ex) {
			System.out.println("Error reading file '" + filePath + "'");
		}
		
		
		filePath = "index/name-index.dat";
		// Read in the names -> file path list
		try {
			FileReader infoReader = new FileReader(filePath);
			BufferedReader bufferedReader = new BufferedReader(infoReader);
			String line;
			while((line = bufferedReader.readLine()) !=null) { //Loops while bufferedReader can find a next line
					//FIXME predefine regex
					String[] info = line.split("\t");
					String name = info[0];
					
					String namesString = info[1];
					namesString = namesString.replaceAll("[\\[\\]]", "");
					
					String[] names = namesString.split(", ");
					data.nameMap.put(name, new ArrayList<>(Arrays.asList(names)));
			}
			bufferedReader.close();
		} catch(FileNotFoundException ex) {
			System.out.println(new File(".").getAbsolutePath());
			System.out.println("ERROR: File cannot be found at location " + filePath);
			ex.printStackTrace();
		} catch(IOException ex) {
			System.out.println("Error reading file '" + filePath + "'");
		}
		
		
		filePath = "index/names.dat";
		// Read in the list of names
		try {
			FileReader infoReader = new FileReader(filePath);
			BufferedReader bufferedReader = new BufferedReader(infoReader);
			String line;
			while((line = bufferedReader.readLine()) !=null) { //Loops while bufferedReader can find a next line
				data.flashCardFiles.add(line);
			}
			bufferedReader.close();
		} catch(FileNotFoundException ex) {
			System.out.println(new File(".").getAbsolutePath());
			System.out.println("ERROR: File cannot be found at location " + filePath);
			ex.printStackTrace();
		} catch(IOException ex) {
			System.out.println("Error reading file '" + filePath + "'");
		}
	}
	
	public void save()
	{	
		saveHumanReadable();
		
		/*ObjectOutputStream objectOut;
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
		}*/
	}
		
	public void addFlashCard(FlashCard f)
	{
		String cardPath = "";
		try {
			cardPath = f.getPath();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't add card");
			return;
		}
		
		for (String tag : f.getTags())
		{
			if (tag.equals(""))
				continue;
			
			List<String> taggedCards = data.tagMap.get(tag);
			if (taggedCards == null)
				taggedCards = new ArrayList<>();
			taggedCards.add(cardPath);
			
			// Not necessary unless taggedCards was null, just being super verbose
			data.tagMap.put(tag, taggedCards);
		}
		
		List<String> cardsWithName = data.nameMap.get(f.getName());
		if (cardsWithName == null)
		{
			cardsWithName = new ArrayList<>();
		}	
		cardsWithName.add(cardPath);
		data.nameMap.put(f.getName(), cardsWithName);

		data.flashCardFiles.add(cardPath);
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
