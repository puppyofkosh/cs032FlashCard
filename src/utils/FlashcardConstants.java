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

	public static final String CS_HOSTNAME = "cslab5c";
	//TODO - get amazon ip address again
	public static final String AMAZON_HOSTNAME = "";
	public static final String DEFAULT_HOSTNAME = "localhost";
	public static final int DEFAULT_PORT = 9888;
	
	public static final String[] DEFAULT_TABLE_COLUMNS = {"Name", "Interval", "Sets", "Tags"};
	
	public static final String QUESTION_FILE = "q.wav";
	public static final String ANSWER_FILE = "a.wav";
	
	public static final String DB_DIR = "./database/";
	public static final String DB_FILE = "carddb";
}
