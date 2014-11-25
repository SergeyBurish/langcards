package lc;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;

import org.apache.commons.io.FilenameUtils;


public class LCutils {
	private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final String STRING_RESOURCE_NAME = "NameInLanguageList";
	private static final String SETTINGS_FILE_NAME = "langCardsSettings";

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

	public static class Settings {
		public int xPos;
		public int yPos;
		public int editWidth;
		public int editHeight;
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

	public static void saveSettings(Settings settings) {
		Properties props = new Properties();
		props.setProperty("xPos", Integer.toString(settings.xPos));
		props.setProperty("yPos", Integer.toString(settings.yPos));
		props.setProperty("editWidth", Integer.toString(settings.editWidth));
		props.setProperty("editHeight", Integer.toString(settings.editHeight));

		try {
			FileOutputStream output = new FileOutputStream(SETTINGS_FILE_NAME);
			props.store(output, "Language Cards settings");
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LOGGER.warning("fail to create/save " + SETTINGS_FILE_NAME + ": " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.warning("fail to create/save " + SETTINGS_FILE_NAME + ": " + e.getMessage());
		}
	}

	public static Settings loadSettings() {

		Properties props = new Properties();
		try {
			FileInputStream input = new FileInputStream(SETTINGS_FILE_NAME);
			props.load(input);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LOGGER.info("fail to load settings: " + e.getMessage());
			return null;
		} catch (IOException e) {
			LOGGER.info("fail to load settings: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		int xPos = 0;
		int yPos = 0;
		int editWidth = 0;
		int editHeight = 0;
		try {
			xPos = Integer.parseInt(props.getProperty("xPos", ""));
			yPos = Integer.parseInt(props.getProperty("yPos", ""));
			editWidth = Integer.parseInt(props.getProperty("editWidth", ""));
			editHeight = Integer.parseInt(props.getProperty("editHeight", ""));
		} catch (NumberFormatException e) {
			LOGGER.info("fail to parse settings: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		Settings settings = new Settings();
		settings.xPos = xPos;
		settings.yPos = yPos;
		settings.editWidth = editWidth;
		settings.editHeight = editHeight;
		return settings;
	}
}
