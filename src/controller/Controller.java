package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import backend.FileImporter;

import audio.AudioFile;
import audio.ByteArrayAudioPlayer;
import audio.FreeTTSReader;
import audio.TextToSpeechReader;
import audio.WavFileConcatenator;
import flashcard.FlashCard;
import flashcard.LocallyStoredFlashCard;
import flashcard.SimpleFactory;

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
	
	
	
	
	/**
	 * Import a tsv or similar
	 * @param filename
	 */
	public static void importCardsToLibrary(String filename)
	{
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
	public static void exportCard(FlashCard f, String destinationFolder)
	{
		if (!destinationFolder.endsWith("/"))
			System.out.println("WARNING: destinationFolder should end with a slash(/)");

		try {
			WavFileConcatenator concat = new WavFileConcatenator(destinationFolder + f.getName());
			concat.concatenate(f);						
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	/**
	 * Play a chunk of audio. This method should ensure that only one piece
	 * of audio is playing at a time
	 */
	public static void playAudio(AudioFile file)
	{	
		// FIXME: This is due to a bug in audioplayer (it is not re-usable)
		// We want to store only 1 player, and use that all the time to
		// make sure we dont play 2 things at once
		ByteArrayAudioPlayer player = new ByteArrayAudioPlayer();
		
		if (player.isPlaying())
			player.stop();

		try {
			player.play(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a flashcard from the given data and save it to file
	 * @param data
	 */
	public static FlashCard createCard(LocallyStoredFlashCard.Data data)
	{
		LocallyStoredFlashCard card = new LocallyStoredFlashCard(data);
		SimpleFactory.writeCard(card);
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
}