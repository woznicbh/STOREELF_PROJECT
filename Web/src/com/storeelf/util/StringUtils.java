package com.storeelf.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 
 * Class Name: com.storeelf.tools.utils.StringUtils<BR/>
 * Purpose: Basic String Utilities<BR/>
 * Creation Date: Sep 7, 2011 9:58:20 AM<BR/>
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

	public static void writeToFile(File file, List<String> toWrite)
			throws IOException {

		try {
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);

			for (String s : toWrite) {
				out.write(s);
			}
			out.close();
		} catch (Exception e) {
			System.out.println("Failed to write to file");
		}
	}

	public static String listToCommaSepStringWithSingleQuotes(List<Object> list) {
		String str = "";
		int count = 0;
		for (Object s : list) {
			if (StringUtils.isVoid(s.toString())) {
				continue;
			}
			if (count == 0) {
				str = "'" + s.toString() + "'";
			} else {
				str = str + "," + "'" + s.toString() + "'";
			}
			count++;
		}
		return str;
	}

	public static String listToCommaSepString(List<Object> list) {
		String str = "";
		int count = 0;
		for (Object s : list) {
			if (StringUtils.isVoid(s.toString())) {
				continue;
			}
			if (count == 0) {
				str = s.toString();
			} else {
				str = str + "," + s.toString();
			}
			count++;
		}
		return str;
	}

	public static String listToCommaSepStringWithDoubleQuotes(List<String> list) {
		String str = "";
		int count = 0;
		for (String s : list) {
			if (StringUtils.isVoid(s)) {
				continue;
			}
			if (count == 0) {
				str = "\"" + s + "\"";
			} else {
				str = str + "," + "\"" + s + "\"";
			}
			count++;
		}
		return str;
	}

	/**
	 * 
	 * Purpose: Method to check if the String has any value or not
	 * 
	 * @param str
	 *            : Input String
	 * @return: True/False
	 */
	public static boolean isVoid(String str) {
		boolean check = false;
		if (str == null || str.equals("")) {
			check = true;
		}
		return check;
	}

	/**
	 * 
	 * Purpose: Converts an input stream to a String
	 * 
	 * @param is
	 *            : InputStream Object
	 * @return: String after reading the InputStream
	 * @throws IOException
	 */
	public static String convertStreamToString(InputStream is)
			throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	/**
	 * 
	 * Purpose: Splits the string based on a regex pattern
	 * 
	 * @param src
	 *            : Source String
	 * @param patternstr
	 *            : Regex patten
	 * @return: String Array
	 */
	public static String[] split(String src, String patternstr) {
		Pattern pattern = Pattern.compile(patternstr);
		String[] array = pattern.split(src);
		return array;
	}
	
	/**
	 * Purpose: Method to read a file.
	 * @param file
	 * @return String
	 */
	public static String readFile(String file) {
		FileReader fr = null;
		BufferedReader br;
		String version;

		try {
			fr = new FileReader(file);
			if (fr.ready()) {
				br = new BufferedReader(fr);
				version = br.readLine();
				fr.close();
				return version;
			}
		} catch (IOException e) {

			return "Not Available";
		}

		finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					// return "Not Available";
				}
			}
		}
		return "Not Available";
	}
	
	/**
	 * Generates new Unique ID.
	 * 
	 * @return the unique ID string        
	 */
	public static String generateUuid() {
		String uuidStr = UUID.randomUUID().toString();
		return uuidStr;
		}
}