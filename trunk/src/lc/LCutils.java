package lc;

import java.util.Locale;
import java.util.ResourceBundle;

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
}
