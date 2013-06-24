package util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class Utils {

	public static boolean arrayContains(String[] array,String string) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equalsIgnoreCase(string)) 
				return true;
		}
		
		return false;
	}
	
	public static boolean checkExtension(File f, String[] exts) {
		System.out.println(FileUtils.getFileExtension(f));
		
		if (Arrays.asList(exts) != null) {
			return Arrays.asList(exts).contains(FileUtils.getFileExtension(f));
		} 
		
		return false;
	}
	
	public static String getHTMLCompliantString(String string) {
		return string.replaceAll(" ", "+");
	}
	
	public static boolean openURL(URL url) {
		URI uri = null;
		Desktop desktop = null;
	     
		try {
			uri = new URI(url.toString());
			desktop = Desktop.getDesktop();
			
			desktop.browse(uri);
			return true;
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
