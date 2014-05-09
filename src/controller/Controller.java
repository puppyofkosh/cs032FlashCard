package controller;

import flashcard.FlashCard;
import flashcard.FlashCardSet;
import flashcard.SerializableFlashCard;
import flashcard.SimpleSet;
import gui.GuiConstants;
import gui.GuiConstants.TabType;
import gui.MainFrame;
import gui.SetBrowser;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import audio.AudioFile;
import audio.AudioPlayer;
import audio.BufferRecorder;
import audio.ByteArrayAudioPlayer;
import audio.FreeTTSReader;
import audio.Recorder;
import audio.TextToSpeechReader;
import backend.FileImportWorker;
import backend.FileImporter;
import database.DatabaseFactory;

/**
 * Provide methods that mess with backend stuff for the GUI to call This class
 * should also handle launching tasks in a new thread and all that
 * 
 * FIXME: Pretty much all of these helper methods should launch a new thread to
 * do their work
 * 
 * @author puppyofkosh
 * 
 */
public class Controller {

	private static AudioPlayer player = new ByteArrayAudioPlayer();
	private static TextToSpeechReader reader;
	private static Recorder recorder;
	private static MainFrame gui;
	public static SetBrowser setBrowser = new SetBrowser();

	// the flashcard being played right now
	private static FlashCard currentlyPlayingFlashCard = null;
	private static TabType currentTab = TabType.FLASHBOARD;


	///////////////////////////////////////////////////////////////////////////////	
	///// AUDIO RECORDING AND PLAYING METHODS /////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////	


	/**
	 * Import a tsv or similar
	 * 
	 * @param filename
	 */
	public static void importCardsFromFile(final String filename) {

		TextToSpeechReader ttsReader;
		try {
			ttsReader = new FreeTTSReader();

			FileImporter importer = new FileImporter(new File(filename),
					ttsReader);

			FileImportWorker w = new FileImportWorker(importer, new Runnable() 
			{
				@Override
				public void run()
				{
					Controller.updateGUI(Controller.getCurrentTab());
				}
			});
			w.execute();
		} catch (IOException e) {
			guiMessage("Invalid file", true);
		}
	}

	/**
	 * Plays audio, called from the gui.
	 * @param file - the audio to be played.
	 * @param runnables - usually button whose displayed icon will change
	 * after it is run.
	 * @throws IOException - if the audio cannot be found or played.
	 */
	public static void playAudioThenRun(AudioFile file, Runnable... runnables)
			throws IOException {
		player.playThenRun(file, runnables);
	}

	public static void playFlashcardThenRun(FlashCard card,
			Runnable... runnables) throws IOException {
		currentlyPlayingFlashCard = card;
		player.playThenRun(card, runnables);
	}

	public static FlashCard getCurrentlyPlayingFlashCard() {
		return currentlyPlayingFlashCard;
	}

	/**
	 * Hard stop on all audio playing.
	 */
	public static void stopAudio() {
		currentlyPlayingFlashCard = null;
		player.stop();
	}

	public static boolean hasPlayer() {
		return player != null;
	}

	public static boolean hasReader() {
		return getReader() != null;
	}


	private static TextToSpeechReader getReader() {
		if (reader != null)
			return reader;
		else {
			try {
				reader = new FreeTTSReader();
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


	public static void startRecord(Runnable... runnables) {
		recorder = new BufferRecorder();
		recorder.startRecord(runnables);
	}

	public static AudioFile finishRecording() {
		return recorder.stopRecord();
	}


	///////////////////////////////////////////////////////////////////////////////	
	///// CARD MODIFICATION METHODS ///////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////	

	/**
	 * Create a flashcard from the given data and save it to file
	 * 
	 * @param data
	 */
	public static FlashCard createCard(SerializableFlashCard.Data data) {
		FlashCard card = new SerializableFlashCard(data);

		// Don't do this (for now). When we add the card to the set, it will
		// write it to disk for us.
		// I can change that if that seems like a bad way of doing things
		// card = DatabaseFactory.writeCard(card);

		return card;
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

	/**
	 * replace a card with a different card in the DB
	 * 
	 * @param oldCard
	 * @param newCard
	 */
	public static void replaceCard(FlashCard oldCard, FlashCard newCard) {

		Collection<FlashCardSet> sets = oldCard.getSets();
		Controller.deleteCard(oldCard);
		try {
			for (FlashCardSet s : sets) {
				s.addCard(newCard);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteCard(FlashCard card) {
		// The DB handles removing the card from its sets
		if (currentlyPlayingFlashCard != null
				&& card.equals(currentlyPlayingFlashCard))
			stopAudio();

		DatabaseFactory.deleteCard(card);
		updateGUI(getCurrentTab());
	}

	public static void editCard(FlashCard card) {
		gui.editCard(card);

	}


	///////////////////////////////////////////////////////////////////////////////	
	///// SET MODIFICATION METHODS ////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////	


	/**
	 * If a set with the same name & metadata already exists, return that,
	 * otherwise create a new set
	 * 
	 * Use this for when we're okay with not getting a "new" set, and getting an already existing set
	 * 
	 * @param name
	 * @param author
	 * @param tags
	 * @param interval
	 * @return
	 */
	public static FlashCardSet createSet(String name, String author,
			List<String> tags, int interval) {
		FlashCardSet existingSet = DatabaseFactory.getResources().getSetByName(
				name);
		FlashCardSet set = new SimpleSet(name);
		for (String tag : tags) {
			try {
				set.addTag(tag);
			} catch (IOException e) {
				Controller.guiMessage("Could not write tag: " + tag, true);
			}
		}
		set.setAuthor(author);
		set.setInterval(interval);

		if (existingSet != null && existingSet.sameMetaData(set)) {
			return existingSet;
		}
		set = DatabaseFactory.writeSet(set);

		updateGUI(getCurrentTab());
		return set;
	}

	/**
	 * Create a new set, write it to disk, and return it. The name may not match
	 * up with what is given.
	 * 
	 * Use this when we want to guarantee a NEW set is created on disk.
	 * 
	 * @param name
	 * @param author
	 * @param tags
	 * @param interval
	 * @return
	 */
	public static FlashCardSet createNewSet(String name, String author,
			List<String> tags, int interval) {
		FlashCardSet set = new SimpleSet(name);
		for (String tag : tags) {
			try {
				set.addTag(tag);
			} catch (IOException e) {
				Controller.guiMessage("Could not write tag: " + tag, true);
			}
		}
		set.setAuthor(author);
		set.setInterval(interval);

		set = DatabaseFactory.writeSet(set);

		updateGUI(getCurrentTab());
		return set;
	}


	public static void editSet(FlashCardSet set) {
		gui.editSet(set);
	}

	/**
	 * Remove given set from both GUI and database/disk
	 * @param set
	 */
	public static void deleteSet(FlashCardSet set) {
		DatabaseFactory.deleteSet(set);
		updateGUI(getCurrentTab());
	}

	public static void setDefaultInterval(int interval) {
		gui.setDefaultInterval(interval);
	}

	public static Collection<FlashCardSet> getAllSets() {
		return DatabaseFactory.getResources().getAllSets();
	}




	///////////////////////////////////////////////////////////////////////////////	
	///// GENERAL GUI METHODS /////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////	
	
	public static void launchGUI() {
		gui = new MainFrame();
	}

	public static TabType getCurrentTab() {
		return currentTab;
	}

	public static void switchTabs(TabType tab) {
		currentTab = tab;
		gui.showTab(tab);
	}

	public static void updateAll() {
		updateGUI(TabType.FLASHBOARD, TabType.EXPORT, TabType.IMPORT,
				TabType.CARD, TabType.SET, TabType.SETTINGS);
	}

	public static void updateGUI(TabType... types) {
		setBrowser.updateSourceList();
		for (TabType type : types) {
			gui.update(type);
		}
	}

	public static SetBrowser requestSetBrowser() {
		return setBrowser;
	}

	public void requestAutocorrections(String text, int boxNo) {
		// TODO - autocorrect not yet implemented.
	}

	/**
	 * All messages are passed from various gui panels to this method.
	 * @param text - the message to be displayed.
	 * @param duration
	 * @param error - whether or not it is an error.
	 */
	public static void guiMessage(String text, int duration, boolean error) {
		//		Uncomment this if you need to do testing.
		//		if (error)
		//			Writer.err(text);
		//		else
		//			Writer.out(text);

		if (gui == null)
			return;

		JLabel label = new JLabel(text);
		final int time = duration;
		final JDialog dialog = new JDialog(gui, (error ? "Error" : "Message"),
				false);
		dialog.add(label);
		Rectangle bounds = gui.getBounds();
		label.setFont(label.getFont().deriveFont(
				(float) (bounds.width / (text.length() + 2))));
		label.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		label.setBackground(GuiConstants.CARD_BACKGROUND);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setOpaque(true);
		dialog.setBounds(bounds.x + (bounds.width / 4), bounds.y
				+ (bounds.height / 4), bounds.width / 2, bounds.height / 2);
		dialog.setVisible(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(time * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				dialog.dispose();
			}
		}).start();
	}


	/**
	 * @see #guiMessage(String, int, boolean)
	 */
	public static void guiMessage(String text, boolean error) {
		guiMessage(text, 3, error);
	}

	/**
	 * @see #guiMessage(String, int, boolean)
	 */
	public static void guiMessage(String text, int duration) {
		guiMessage(text, duration, false);
	}

	/**
	 * @see #guiMessage(String, int, boolean)
	 */
	public static void guiMessage(String text) {
		guiMessage(text, 3);
	}
}
