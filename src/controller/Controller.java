package controller;

import flashcard.FlashCard;
import flashcard.FlashCardSet;
import flashcard.SerializableFlashCard;
import flashcard.SimpleSet;
import gui.MainFrame;

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
import audio.DiscRecorder;
import audio.FreeTTSReader;
import audio.Recorder;
import audio.TextToSpeechReader;
import audio.WavFileConcatenator;
import backend.FileImporter;
import database.DatabaseFactory;

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

	private static AudioPlayer player = new ByteArrayAudioPlayer();
	private static TextToSpeechReader reader;
	private static Recorder recorder;
	private static MainFrame gui;

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
		return getReader() != null;
	}

	/**
	 * Create a flashcard from the given data and save it to file
	 * @param data
	 */
	public static FlashCard createCard(SerializableFlashCard.Data data) {
		FlashCard card = new SerializableFlashCard(data);

		// Don't do this (for now). When we add the card to the set, it will write it to disk for us.
		// I can change that if that seems like a bad way of doing things
		// card = DatabaseFactory.writeCard(card);
		
		
		gui.updateAll();
		return card;
	}

	/***
	 * Turn a string like "tag1, tag2 tag3..." into
	 * [tag1, tag2, tag3]
	 * @param allTags
	 * @return
	 */
	public static List<String> parseTags(String allTags) {
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
		player.play(card);
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
		}

	}

	public static void removeTag(FlashCard card, String tag) throws IOException {
		if (card == null)
			return;
		else
			card.removeTag(tag);
	}

	private static TextToSpeechReader getReader() {
		if (reader != null)
			return reader;
		else {
			try {
				reader =  new FreeTTSReader();
				return reader;
			} catch (Throwable e) {
				guiMessage("Could not load reader", true);
				return null;
			}
		}
	}

	public static AudioFile readTTS(String text) {
		return reader.read(text);
	}

	public static void startRecord() {
		recorder = new DiscRecorder();
		recorder.startRecord();
	}

	public static AudioFile finishRecording() {
		return recorder.stopRecord();
	}

	public static void deleteCard(FlashCard card) {
		DatabaseFactory.deleteCard(card);
		gui.updateAll();
	}

	public static Collection<FlashCardSet> getAllSets() {
		return DatabaseFactory.getResources().getAllSets();
	}
	
	public static FlashCardSet createSet(String name, String author, List<String> tags, int interval) {
		//TODO Should check if the set exists already.
		FlashCardSet set = new SimpleSet(name);
		for(String tag : tags) {
			try {
				set.addTag(tag);
			} catch (IOException e) {
				Controller.guiMessage("Could not write tag: " + tag, true);
			}
		}
		set.setAuthor(author);
		set.setInterval(interval);
		set = DatabaseFactory.writeSet(set);
		return set;
	}

	public static void launchGUI() {
		gui = new MainFrame();
	}
}
