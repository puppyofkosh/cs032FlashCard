package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// FIXME: Make delimiter a parameter
public class TSVLineParser {
	private List<String> header;

	public TSVLineParser(String[] header) {
		this.header = new ArrayList<>(Arrays.asList(header));
	}

	/**
	 * Given a line from a TSV file, read it into a map containing entries that
	 * go [header string -> value read from file] Ex: name id bob 1
	 * 
	 * Reading the line "bob 1" would give: [name -> bob, id -> 1]
	 * 
	 * @param line
	 * @return
	 */
	public Map<String, String> read(String line) {
		String[] chunks = line.split("\\t");
		Map<String, String> map = new HashMap<>();

		// We allow for fewer args in the chunks because the last n arguments
		// may just be empty
		if (chunks.length > header.size()) {
			// Bad line--we just return null
			return null;
		}

		for (int i = 0; i < header.size(); i++) {
			if (i < chunks.length) {
				map.put(header.get(i), chunks[i]);
			}
		}

		return map;
	}
}
