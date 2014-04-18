package utils;

import javax.sound.sampled.AudioFormat;

import audio.AudioConstants;

/**
 * Contains the constants used in the project
 */
public class FlashcardConstants {

	public static final	AudioFormat standardizedFormat = AudioConstants.TTSREADER;

	public static final String METADATA_HEADER = "NAME\tINTERVAL\tSETS\tTAGS";
	public final static String CARDS_FOLDER = "./files/";


	public static final String AMAZON_HOSTNAME = "54.208.81.226";
public static final String DEFAULT_HOSTNAME = "localhost";
	public static final int DEFAULT_PORT = 9888;
	
	public static final String[] DEFAULT_TABLE_COLUMNS = {"Name", "Interval", "Sets", "Tags"};
}
