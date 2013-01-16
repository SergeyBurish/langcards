package lc;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

public class LCutils {
	
	// en_EN - default locale
	//private static Locale iLocale = new Locale("en_EN");
	private static ResourceBundle iResourceBundle = ResourceBundle.getBundle("resources.strings.strings", new Locale("en_EN"));
	
	public static void SetLocale(String localeString) {
		Locale locale = new Locale(localeString);
		
		if (locale != null) {
			//ResourceBundle.clearCache(null);
			iResourceBundle = ResourceBundle.getBundle("resources.strings.strings", locale);
			//iResourceBundle
		}
	}

	// current locale string
	public static String String(String key) {
		return iResourceBundle.getString(key);
	}
	
	public static ImageIcon Image(String imgName) {
		java.net.URL imgURL = LCmain.class.getResource("/resources/images/" + imgName);
		if (imgURL != null) {
			return new ImageIcon(imgURL, "");
		}
		
		return null;
	}
}
