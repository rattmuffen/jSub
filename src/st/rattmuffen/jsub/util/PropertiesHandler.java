package st.rattmuffen.jsub.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class for reading and writing in a propertiesfile.
 * @version 0.3.1
 * @author rattmuffen
 */
public class PropertiesHandler {

	public static final String DOWNLOAD_FIRST = "downloadFirst";
	public static final String EXIT_AFTER = "exitAfter";
	public static final String OPEN_AFTER = "openAfter";
	public static final String LANGUAGE = "subLang";
	private static final String COMMENT = "-- jSub Properties file --";
	public static final String RENAME_AFTER = "renameAfter";

	private static String fileName = "properties";

	public static void writeProperty(String name, String value) throws IOException {
		File f = new File(fileName);

		if (f.exists()) {
			Properties prop = new Properties();

			prop.load(new FileInputStream(f));

			prop.setProperty(name, value);
			prop.store(new FileOutputStream(f),COMMENT);
		} else {
			boolean success = createPropertiesFile();

			if (success)
				writeProperty(name, value);
			else
				throw new IOException("Could not create properties file.");
		}
	}

	public static String readProperty(String name) throws FileNotFoundException, IOException {
		File f = new File(fileName);

		if (f.exists()) {
			Properties prop = new Properties();
			prop.load(new FileInputStream(f));

			return prop.getProperty(name);
		} else {
			boolean success = createPropertiesFile();
			throw new IOException("Could not find properties file to read from. Created new: " + success);
		}
	}

	public static boolean createPropertiesFile() throws IOException {
		File f = new File(fileName);

		if (!f.exists()) 
			return f.createNewFile();
		return f.exists();
	}
}
