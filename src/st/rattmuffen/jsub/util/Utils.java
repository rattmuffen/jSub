package st.rattmuffen.jsub.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class Utils {
	
	public static final String[] acceptedLangs = {
		"eng - en - English",
		"alb - sq - Albanian",
		"ara - ar - Arabic",
		"arm - hy - Armenian",
		"baq - eu - Basque",
		"ben - bn - Bengali",
		"bos - bs - Bosnian",
		"bre - br - Breton",
		"bul - bg - Bulgarian",
		"bur - my - Burmese",
		"cat - ca - Catalan",
		"chi - zh - Chinese",
		"cze - cs - Czech",
		"dan - da - Danish",
		"dut - nl - Dutch",
		"epo - eo - Esperanto",
		"est - et - Estonian",
		"fin - fi - Finnish",
		"fre - fr - French",
		"geo - ka - Georgian",
		"ger - de - German",
		"glg - gl - Galician",
		"ell - el - Greek",
		"heb - he - Hebrew",
		"hin - hi - Hindi",
		"hrv - hr - Croatian",
		"hun - hu - Hungarian",
		"ice - is - Icelandic",
		"ind - id - Indonesian",
		"ita - it - Italian",
		"jpn - ja - Japanese",
		"kaz - kk - Kazakh",
		"khm - km - Khmer",
		"kor - ko - Korean",
		"lav - lv - Latvian",
		"lit - lt - Lithuanian",
		"ltz - lb - Luxembourgish",
		"mac - mk - Macedonian",
		"mal - ml - Malayalam",
		"may - ms - Malay",
		"mon - mn - Mongolian",
		"nor - no - Norwegian",
		"oci - oc - Occitan",
		"per - fa - Persian",
		"pol - pl - Polish",
		"por - pt - Portuguese",
		"rus - ru - Russian",
		"scc - sr - Serbian",
		"sin - si - Sinhalese",
		"slo - sk - Slovak",
		"slv - sl - Slovenian",
		"spa - es - Spanish",
		"swa - sw - Swahili",
		"swe - sv - Swedish",
		"tam - ta - Tamil",
		"tel - te - Telugu",
		"tgl - tl - Tagalog",
		"tha - th - Thai",
		"tur - tr - Turkish",
		"ukr - uk - Ukrainian",
		"urd - ur - Urdu",
		"vie - vi - Vietnamese",
		"rum - ro - Romanian",
		"pob - pb - Brazilian"
	};

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

	public static String getLangCode(String selectedItem) {
		return selectedItem.split(" - ")[0];
	}
}
