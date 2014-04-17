package utils;

import javax.sound.sampled.AudioFormat;

import audio.AudioConstants;

/**
 * Contains the constants used in the project
 */
public class FlashcardConstants {

	public static final	AudioFormat standardizedFormat = AudioConstants.TTSREADER;

	public static final String METADATA_HEADER = "NAME\tINTERVAL\tSETS\tTAGS";
	public final static String CARDS_FOLDER = "./Example FileSystem/Application Data/CARDS";

	public static final String[] DEFAULT_TABLE_COLUMNS = {"Name", "Interval", "Sets", "Tags"};
}
