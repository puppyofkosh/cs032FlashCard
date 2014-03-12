package flashcard;
/*
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import backend.Resources;

public class FlashCardTest {
	Resources r = new ResourcesClass();
	FlashCardSet fSet = new FlashCardSetClass(r.getSetByName("AMERICAN HISTORY"));
	FlashCardSet badFSet = new FlashCardSetClass(r.getSetByName("AMERICAN HISTORY"));
	String examplePath = "./Example FileSystem/Application Data/;
			FlashCard lincoln = new FlashCardClass(examplePath + "CARDS/ABRAHAM LINCOLN'S BIRTHDAY/");
	FlashCard obama = new FlashCardClass(examplePath + "CARDS/BARACK OBAMA'S INAUGURATION/");
	FlashCard badCard = new FlashCardClass(examplePath + "CARDS/WRONG CARD");

	@Test
	public void getSetsTest() {
		Collection<FlashCardSet> lincolnSets = lincoln.getSets();
		assertTrue(lincolnSets.contains(fSet));
		assertFalse(lincolnSets.contains(r.getSetByName("EMPTY SET")));
		for(FlashCardSet set : lincolnSets) {
			assertTrue(set.getTags().contains("PRESIDENTS"));
		}
	}

	@Test
	public void getTagsTest() {
		Set<String> lTags = (Set<String>) lincoln.getTags();
		Set<String> lTagsOnFile = new HashSet<>();
		lTagsOnFile.add("TALL PEOPLE");
		lTagsOnFile.add("PRESIDENTS");
		lTagsOnFile.add("THE CIVIL WAR");
		lTagsOnFile.add("HAT WEARERS");
		assertTrue(lTags.equals(lTagsOnFile));
	}

	@Test
	public void addTagsTest() {
		try {
			lincoln.addTag("TEST TAG");
			assertTrue(lincoln.getTags().contains("TEST TAG"));
		} catch (IOException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void addTagsFailTest() {
		boolean worked = true;
		try {
			badCard.addTag("TEST TAG");
			worked = true;
		}
		catch (IOException e) {
			worked = false;
		} 
		assertFalse(worked);
	}
	
	@Test
	public void getIntervalTest() {
		assertTrue(lincoln.getInterval() == 1);
		//Note the following is a global interval from the set AMERICAN HISTORY
		assertTrue(obama.getInterval() == 2); 
	}
	
	@Test
	public void setIntervalTest() {
		assertTrue(lincoln.getInterval() == 1);
		lincoln.setInterval(4);
		assertTrue(lincoln.getInterval() == 4);
	}
	
	@Test
	public void setIntervalTest2() {
		assertTrue(lincoln.getInterval() == 1);
		//Set interval only takes numbers > 0.
		lincoln.setInterval(-4);
		assertTrue(lincoln.getInterval() == 1);
		lincoln.setInterval(0);
		assertTrue(lincoln.getInterval() == 1);
	}
	
	@Test
	public void setIntervalFailTest() {
		boolean worked = true;
		try {
			badCard.setInterval(4);
			worked = true;
		}
		catch (IOException e) {
			worked = false;
		}
		assertFalse(worked);
	}
}*/
