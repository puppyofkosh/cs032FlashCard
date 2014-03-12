package flashcard;
/* Tests commented out until we have stubs. 

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import backend.Resources;
public class FlashCardSetTest {
	Resources r = new ResourcesClass();
	FlashCardSet fSet = new FlashCardSetClass(r.getSetByName("AMERICAN HISTORY"));
	FlashCardSet badFSet = new FlashCardSetClass(r.getSetByName("AMERICAN HISTORY"));
	String examplePath = "./Example FileSystem/Application Data/;
			FlashCard lincoln = new FlashCardClass(examplePath + "CARDS/ABRAHAM LINCOLN'S BIRTHDAY/");
	FlashCard obama = new FlashCardClass(examplePath + "CARDS/BARACK OBAMA'S INAUGURATION/");


	@Test
	public void GetAllFlashCardsTest() {
		try {
			Collection<FlashCard> cardsInSet = fSet.getAll();
			for(FlashCard f : cardsInSet) {
				assertTrue(f.equals(obama) || f.equals(lincoln));
			}
		}
		catch (IOException e) {
			assertTrue(false);
		}
	}

	@Test
	public void GetAllFlashCardsFailTest() {
		boolean worked;
		try {
			Collection<FlashCard> cardsInSet = badFSet.getAll();
		} 
		catch (IOException e) {
			worked = false;
		}
		assertFalse(worked);
	}


	@Test
	public void addTagTest() {
		try {
		fSet.addTag("TEST TAG");
		Collection<FlashCard> cardsInSet = fSet.getAll();
		for (FlashCard f : cardsInSet) {
			assertTrue(f.getTags().contains("TEST TAG"));
		}
		assertTrue(fSet.getTags().contains("TEST TAG"));
		}
		catch (IOException e) {
			assertTrue(false);
		}
	}

	@Test
	public void addTagFailTest() {
		boolean worked = true;
		try {
			badFSet.addTag("TEST TAG");
			worked = true;
		}
		catch (IOException e) {
			worked = false;
		}
		assertFalse(worked);
	}
	
	@Test
	public void removeTagTest() {
		try {
		fSet.removeTag("PRESIDENTS");
		Collection<FlashCard> cardsInSet = fSet.getAll();
		for (FlashCard f : cardsInSet) {
			assertFalse(f.getTags().contains("PRESIDENTS"));
		}
		assertFalse(fSet.getTags().contains("PRESIDENTS"));
		}
		catch (IOException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void removeTagFailTest() {
		boolean worked = true;
		try {
		badFSet.removeTag("PRESIDENTS");
		worked = true;
		}
		catch (IOException e) {
			worked = false;
		}
		assertFalse(worked);
	}
}*/
