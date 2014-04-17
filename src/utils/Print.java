package utils;

public class Print {
	
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
	
	public static void debug(Object ... strs) {
		if (DEBUG_MODE_ON)
			out(strs);
	}


	private static String composeString(Object...strs) {
		String s = "" + strs[0];
		for (int i = 1; i < strs.length; i++) {
			s += (strs[i] +" ");
		}
		return s;
	}
	


}
