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

	private static final String X_POS = "xPos";
	private static final String Y_POS = "yPos";
	private static final String EDIT_WIDTH = "editWidth";
	private static final String EDIT_HEIGHT = "editHeight";
	private static final String LESSON_WIDTH = "lessonWidth";
	private static final String LESSON_HEIGHT = "lessonHeight";
	private static final String DIALOG_X_POS = "dialogXpos";
	private static final String DIALOG_Y_POS = "dialogYpos";
	private static final String DIALOG_WIDTH = "dialogWidth";
	private static final String DIALOG_HEIGHT = "dialogHeight";

	private static String iCurrentLocaleString;
	private static ResourceBundle iResourceBundle;
	
	private interface SearchResourceListener{
		Object onFind(Object param);
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
		public int lessonWidth;
		public int lessonHeight;

		public int dialogXpos;
		public int dialogYpos;
		public int dialogWidth;
		public int dialogHeight;
	}

	public static String currentLocaleString() {
		return iCurrentLocaleString;
	}
	
	public static int getCurrentLocaleIndexOfList(Collection<LanguageResourceItem> langListModel) {
		int i = 0;
		for (LanguageResourceItem languageResourceItem : langListModel) {
			if (languageResourceItem.localeString().equals(iCurrentLocaleString)) {
				return i;
			}
			i++;
		}
		return 0;
	}
	
	public static void setLocale(String localeString) {
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
			LOGGER.info("setLocale: " + iResourceBundle.getLocale().toString());
			
			iCurrentLocaleString = localeString;
		}
	}

	// current locale string
	public static String string(String key) {
		return iResourceBundle.getString(key);
	}
	
	public static ImageIcon image(String imgName) {
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
							public Object onFind(Object fileName) {
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
					listener.onFind(fileName);
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
							listener.onFind(fileName);
						}
					}
				}
			}
		}

		return resourcesList;
	}

	public static void saveSettings(Settings settings) {
		Properties props = new Properties();
		props.setProperty(X_POS, Integer.toString(settings.xPos));
		props.setProperty(Y_POS, Integer.toString(settings.yPos));
		props.setProperty(EDIT_WIDTH, Integer.toString(settings.editWidth));
		props.setProperty(EDIT_HEIGHT, Integer.toString(settings.editHeight));
		props.setProperty(LESSON_WIDTH, Integer.toString(settings.lessonWidth));
		props.setProperty(LESSON_HEIGHT, Integer.toString(settings.lessonHeight));
		props.setProperty(DIALOG_X_POS, Integer.toString(settings.dialogXpos));
		props.setProperty(DIALOG_Y_POS, Integer.toString(settings.dialogYpos));
		props.setProperty(DIALOG_WIDTH, Integer.toString(settings.dialogWidth));
		props.setProperty(DIALOG_HEIGHT, Integer.toString(settings.dialogHeight));

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
			return new Settings();
		} catch (IOException e) {
			LOGGER.info("fail to load settings: " + e.getMessage());
			e.printStackTrace();
			return new Settings();
		}

		Settings settings = new Settings();
		try {
			settings.xPos = Integer.parseInt(props.getProperty(X_POS, ""));
			settings.yPos = Integer.parseInt(props.getProperty(Y_POS, ""));
			settings.editWidth = Integer.parseInt(props.getProperty(EDIT_WIDTH, ""));
			settings.editHeight = Integer.parseInt(props.getProperty(EDIT_HEIGHT, ""));
			settings.lessonWidth = Integer.parseInt(props.getProperty(LESSON_WIDTH, ""));
			settings.lessonHeight = Integer.parseInt(props.getProperty(LESSON_HEIGHT, ""));
			settings.dialogXpos = Integer.parseInt(props.getProperty(DIALOG_X_POS, ""));
			settings.dialogYpos = Integer.parseInt(props.getProperty(DIALOG_Y_POS, ""));
			settings.dialogWidth = Integer.parseInt(props.getProperty(DIALOG_WIDTH, ""));
			settings.dialogHeight = Integer.parseInt(props.getProperty(DIALOG_HEIGHT, ""));
		} catch (NumberFormatException e) {
			LOGGER.info("fail to parse settings: " + e.getMessage());
			e.printStackTrace();
			return new Settings();
		}

		return settings;
	}
}
