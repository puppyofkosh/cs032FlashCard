package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import utils.Writer;
import audio.AudioFile;
import audio.AudioPlayer;
import audio.ByteArrayAudioPlayer;
import audio.FreeTTSReader;
import audio.TextToSpeechReader;
import audio.WavFileConcatenator;
import backend.FileImporter;
import database.DatabaseFactory;
import flashcard.FlashCard;
import flashcard.SerializableFlashCard;

/**
 * Provide methods that mess with backend stuff for the GUI to call
 * This class should also handle launching tasks in a new thread and all that
 * 
 * FIXME: Pretty much all of these helper methods should launch a new thread to do their work
 * 
 * @author puppyofkosh
 *
 */
public class Controller {

	private static AudioPlayer player;
	private static TextToSpeechReader reader;

	/**
	 * Import a tsv or similar
	 * @param filename
	 */
	public static void importCardsToLibrary(String filename) {
		// FIXME: Not necessary to create a new reader every time
		TextToSpeechReader ttsReader;
		try {
			ttsReader = new FreeTTSReader();

			FileImporter importer = new FileImporter(new File(filename), ttsReader);
			importer.importCards();

			System.out.println("Imported " + importer.getCardList().size() + " cards");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * Export a card to flat wav
	 * @param f
	 * @param destination
	 */
	public static void exportCard(FlashCard f, String destinationFolder) {
		if (!destinationFolder.endsWith("/"))
			guiMessage("WARNING: destinationFolder should end with a slash(/)", true);
		try {
			WavFileConcatenator.concatenate(f);						
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}


	/**
	 * Play a chunk of audio. This method should ensure that only one piece
	 * of audio is playing at a time
	 */
	public static void playAudio(AudioFile file) throws IOException {	
		// FIXME: This is due to a bug in audioplayer (it is not re-usable)
		// We want to store only 1 player, and use that all the time to
		// make sure we dont play 2 things at once
		if (player == null)
			player = new ByteArrayAudioPlayer();

		if (player.isPlaying())
			player.stop();

		player.play(file);
	}

	public static void stopAudio() {
		player.stop();
	}
	
	public static boolean hasPlayer() {
		//FIXME: implement for real.
		return true;
	}
	
	public static boolean hasReader() {
		//FIXME: implement for real.
		return true;
	}

	/**
	 * Create a flashcard from the given data and save it to file
	 * @param data
	 */
	public static FlashCard createCard(SerializableFlashCard.Data data)
	{
		FlashCard card = new SerializableFlashCard(data);
		card = DatabaseFactory.writeCard(card);
		
		return card;
	}

	/***
	 * Turn a string like "tag1, tag2 tag3..." into
	 * [tag1, tag2, tag3]
	 * @param allTags
	 * @return
	 */
	public static List<String> parseTags(String allTags)
	{
		// FIXME: This sucks. Split on spaces/commas/tabs whatever
		if (allTags.equals(""))
			return Arrays.asList();

		return new ArrayList<>(Arrays.asList(allTags.split(", ")));
	}

	public void requestAutocorrections(String text, int boxNo) {
		// TODO Auto-generated method stub
	}

	public static void guiMessage(String text, int duration, boolean error) {
		//FIXME - do for real
		if (error)
			Writer.err(text);
		else
			Writer.out(text);
	}

	public static void guiMessage(String text, boolean error) {
		guiMessage(text, 3, error);
	}

	public static void guiMessage(String text, int duration) {
		guiMessage(text, duration, false);
	}

	public static void guiMessage(String text) {
		guiMessage(text, 3);
	}

	public static String parseCardName(String text) {
		//FIXME implement for real
		return text;
	}

	public static void playFlashCard(FlashCard card) throws IOException {
		playAudio(card.getQuestionAudio());
	}

	public static boolean verifyInput(String input) {
		//FIXME implement for real
		return input.length() > 0;
	}
	
	public static void addTag(FlashCard card, String tag) throws IOException {
		if (card == null)
			return;
		else {
			card.addTag(tag);
			System.out.println("Added tag");
		}
		
	}
	
	public static void removeTag(FlashCard card, String tag) throws IOException {
		if (card == null)
			return;
		else
			card.removeTag(tag);
	}

	public static TextToSpeechReader getReader() {
		try {
			return new FreeTTSReader();
		} catch (Throwable e) {
			guiMessage("Could not load reader", true);
			return null;
		}
	}

	public static void deleteCard(FlashCard card) {
		//FIXME implement for real
		guiMessage("BANG you're dead", false);
		DatabaseFactory.deleteCard(card);
	}

	public static Collection<FlashCardSet> getAllSets() {
		//FIXME implement for real
		return SimpleFactory.getResources().getAllSets();
	}
}
