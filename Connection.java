import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;

/**
 * @author Gireeshma Reddy
 * @version 1.0
 * @since 2016-07-13 The Connection class takes the topic entered by the user
 *        through the findResult method in the form of a string and passes it to
 *        the Wikipedia URL after changing the format. The resulting JSON file
 *        is used to extract the actual content which is then returned.
 *
 */

public class Connection {
	/**
	 * @param inputString
	 *            - The string entered by the user.
	 * @return The string which is the final result shown to the user.
	 */
	public String findResult(String inputString) {
		String finalString = "";
		String stringFromJSON = "";

		inputString = changeToWikiFormat(inputString);
		stringFromJSON = readFromTheURL(inputString);
		finalString = extractContent(stringFromJSON);

		if (finalString.equals("")) {
			finalString = ("Did not find any relevant article on Wikipedia. Please enter the complete name.");
		}

		return finalString;

	}

	/**
	 * @param inputString
	 *            - The string entered by the user.
	 * @return - The string modified to the wikipedia title format.
	 * 
	 *         This method converts the input string entered by the user to a
	 *         Wikipedia accepted title.
	 */
	public String changeToWikiFormat(String inputString) {

		inputString = WordUtils.capitalizeFully(inputString);// Obtained from
																// the library
																// mentioned.
		inputString = inputString.replaceAll(" ", "_");
		return inputString;

	}

	/**
	 * @param inputString
	 *            - The input string in the modified format.
	 * @return - The output from the URL in JSON text.
	 * 
	 *         This method takes the title and stores the content obtained in a
	 *         JSON file.
	 */
	public String readFromTheURL(String inputString) {
		String stringFromJSON = "";
		InputStream input = null;
		OutputStream output = null;

		try {
			// The input stream is obtained from the URL.
			input = new URL(
					"https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles="
							+ inputString).openStream();
			File fileJSON = new File("output.json");
			output = new FileOutputStream("output.json");

			byte[] buffer = new byte[1024];
			for (int length = 0; (length = input.read(buffer)) > 0;) {
				// The output is written to a JSON file.
				output.write(buffer, 0, length);
			}

			FileReader reader = new FileReader("output.json");
			BufferedReader br = new BufferedReader(reader);
			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					// The lines are appended to the string buffer which is used
					// to store the output
					sb.append(line);
					line = br.readLine();
				}
				// The string buffer is converted to a string.
				stringFromJSON = sb.toString();
			} finally {
				br.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return stringFromJSON;

	}

	/**
	 * @param stringFromJSON
	 *            - The string obtained from the JSON file.
	 * @return - The extracted content without any tags.
	 * 
	 *         This method uses regular expressions to remove the unwanted
	 *         content and tags from the JSON string.
	 */
	public String extractContent(String stringFromJSON) {
		String finalString = "";

		Pattern pattern = Pattern.compile("(?<=\"extract\":\").*");
		Matcher matcher = pattern.matcher(stringFromJSON);
		boolean found = false;

		while (matcher.find()) {
			String stringAfterExtract = matcher.group().toString();
			finalString = stringAfterExtract.split("\"}")[0];
			found = true;
		}

		if (!found) {
			finalString = ("Did not find any relevant article on Wikipedia.");
		}

		return finalString;
	}

}