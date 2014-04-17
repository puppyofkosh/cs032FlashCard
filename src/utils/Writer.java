package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.google.common.base.Joiner;

public class Writer {
	
	private static boolean DEBUG_MODE_ON = false;


	/**	
	 * Utilities for printing.
	 * @param strs
	 */
	public static void out(Object...strs) {
		System.out.println(composeString(strs));
	}

	public static void err(Object...strs) {
		System.err.println(composeString(strs));
	}
	
	public static void writeAudioFile(String path, AudioInputStream stream, boolean isQuestion) throws IOException {
		String filename = path + "/" + (isQuestion ? "q" : "a") + ".wav";
		
		// If the file already exists, delete it and overwrite
		try
		{
			Files.delete(Paths.get(filename));
		}
		catch(IOException ignore)
		{
			// ignore
		}
		
		File audioFile = new File(path + "/" + (isQuestion ? "q" : "a") + ".wav");
		AudioSystem.write(stream, AudioFileFormat.Type.WAVE, audioFile);
	}
	
	public static void debug(Object ... strs) {
		if (DEBUG_MODE_ON)
			out(strs);
	}
	
	public static String condenseCollection(Collection<? extends Object> collection) {
		if (collection.size() == 0)
			return "";
		Joiner joiner = Joiner.on(", ").skipNulls();
		return joiner.join(collection);
	}


	private static String composeString(Object...strs) {
		String s = "" + strs[0];
		for (int i = 1; i < strs.length; i++) {
			s += (strs[i] +" ");
		}
		return s;
	}
}
