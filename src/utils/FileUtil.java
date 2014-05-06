package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtil {
	public static List<String> allLines(String filename) {
		Scanner s;
		ArrayList<String> list = new ArrayList<>();
		try {
			s = new Scanner(new File(filename));
			while (s.hasNext()) {
				list.add(s.next());
			}
			s.close();
		}
		catch (FileNotFoundException e) {
			Writer.err("Couldn't load file " + filename);
		}
		return list;
	}
}
