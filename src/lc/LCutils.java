package lc;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;

import org.apache.commons.io.FilenameUtils;


public class LCutils {
	private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final String STRING_RESOURCE_NAME = "NameInLanguageList";
	
	private static String iCurrentLocaleString;
	private static ResourceBundle iResourceBundle;
	
	private interface SearchResourceListener{
		Object OnFind(Object param);
	}
	
	public static class LanguageResourceItem{
		private String iName;
		private String iLocaleString;
		
		public LanguageResourceItem(String name, String localeString) {
			iName = name;
			iLocaleString = localeString;
		}
		
		public String toString() {
			return iName;
		}
		
		public String localeString() {
			return iLocaleString;
		}
	}
	
	public static String CurrentLocaleString() {
		return iCurrentLocaleString;
	}
	
	public static int GetCurrentLocaleIndexOfList(Collection<LanguageResourceItem> langListModel) {
		int i = 0;
		for (LanguageResourceItem languageResourceItem : langListModel) {
			if (languageResourceItem.localeString().equals(iCurrentLocaleString)) {
				return i;
			}
			i++;
		}
		return 0;
	}
	
	public static void SetLocale(String localeString) {
		String[] localeStringsArr = localeString.split("_");
		
		Locale locale = null;
		
		switch (localeStringsArr.length) {
		case 1:
			locale = new Locale(localeStringsArr[0]);
			break;
			
		case 2:
			locale = new Locale(localeStringsArr[0], localeStringsArr[1]);
			break;
			
		case 3:
			locale = new Locale(localeStringsArr[0], localeStringsArr[1], localeStringsArr[2]);
			break;

		default: // do nothing
			break;
		}
		
		if (locale != null) {
			iResourceBundle = ResourceBundle.getBundle("resources.strings.strings", locale);
			LOGGER.info("SetLocale: " + iResourceBundle.getLocale().toString());
			
			iCurrentLocaleString = localeString;
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

	public static Vector<LanguageResourceItem> supportedUILanguages() throws IOException,
			URISyntaxException {
		final Vector<LanguageResourceItem> langList = new Vector<LanguageResourceItem>();

		CodeSource src = LCutils.class.getProtectionDomain().getCodeSource();
		if (src != null) {

			URL binJarUrl = src.getLocation();
			if (binJarUrl != null) {
				URI binJarUri = binJarUrl.toURI();
				String separator = "(\\\\|/)";
				String stringsPropertiesPath = ".*resources" + separator
						+ "strings" + separator + "strings_.*\\.properties";

				Pattern stringsPropertiesPattern = Pattern
						.compile(stringsPropertiesPath);

				getResources(binJarUri.getPath(), stringsPropertiesPattern,
						new SearchResourceListener() {

							@Override
							public Object OnFind(Object fileName) {
								String baseName = FilenameUtils
										.getBaseName((String) fileName);
								LanguageResourceItem langItem = itemOfStringResource(baseName);
								
								if (langItem != null) {
									String name = langItem.toString();
									String localeString = langItem.localeString();
									
									if (!name.isEmpty() && !localeString.isEmpty()) {
										LOGGER.info("supported language found: " + langItem);
										langList.add(new LanguageResourceItem(name, localeString));
									}
								}
								return null;
							}
						});
			}
		}
		return langList;
	}

	private static LanguageResourceItem itemOfStringResource(String fileBaseName) {
		String localeString = fileBaseName.replaceFirst("strings_", "") ; // "strings_en_EN" -> "en_EN"
		ResourceBundle iResourceBundle = ResourceBundle.getBundle("resources.strings." + fileBaseName);
		
		return new LanguageResourceItem(iResourceBundle.getString(STRING_RESOURCE_NAME), localeString);
	}

	// for all elements of searchPath get a Collection of resources Pattern
	// pattern = Pattern.compile(".*"); - to gets all resources
	//
	// @param pattern - the pattern to match
	// @return the resources in the order they are found
	private static Collection<String> getResources(final String searchPath,
			final Pattern pattern, SearchResourceListener listener)
			throws IOException {
		final ArrayList<String> resourcesList = new ArrayList<String>();
		final File file = new File(searchPath);
		if (file.isDirectory()) {
			resourcesList.addAll(getResourcesFromDirectory(file, pattern,
					listener));
		} else {
			resourcesList.addAll(getResourcesFromJarFile(file, pattern,
					listener));
		}

		return resourcesList;
	}

	private static Collection<String> getResourcesFromJarFile(final File file,
			final Pattern pattern, final SearchResourceListener listener)
			throws IOException {
		final ArrayList<String> resourcesList = new ArrayList<String>();

		ZipFile zf = new ZipFile(file);

		final Enumeration<? extends ZipEntry> e = zf.entries();
		while (e.hasMoreElements()) {
			final ZipEntry ze = e.nextElement();
			final String fileName = ze.getName();
			final boolean accept = pattern.matcher(fileName).matches();

			if (accept) {
				resourcesList.add(fileName);

				if (listener != null) {
					listener.OnFind(fileName);
				}
			}
		}

		zf.close();
		return resourcesList;
	}

	private static Collection<String> getResourcesFromDirectory(
			final File directory, final Pattern pattern,
			final SearchResourceListener listener) throws IOException {
		final ArrayList<String> resourcesList = new ArrayList<String>();
		final File[] fileList = directory.listFiles();

		if (fileList != null) {
			for (final File file : fileList) {
				if (file.isDirectory()) {
					resourcesList.addAll(getResourcesFromDirectory(file, pattern,
							listener));
				} else {
					final String fileName = file.getCanonicalPath();
					final boolean accept = pattern.matcher(fileName).matches();

					if (accept) {
						resourcesList.add(fileName);

						if (listener != null) {
							listener.OnFind(fileName);
						}
					}
				}
			}
		}

		return resourcesList;
	}
}
