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
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;

import org.apache.commons.io.FilenameUtils;


public class LCutils {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private interface SearchResourceListener{
		Object OnFind(Object param);
	}

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

	public static Collection<String> supportedUILanguages() throws IOException,
			URISyntaxException {
		CodeSource src = LCutils.class.getProtectionDomain().getCodeSource();
		if (src != null) {

			URL binJarUrl = src.getLocation();
			if (binJarUrl != null) {
				URI binJarUri = binJarUrl.toURI();
				String separator = "(\\\\|/)";
				String stringsPropertiesPath = ".*resources" + separator
						+ "strings" + separator + "strings_.._..\\.properties";
				Pattern stringsPropertiesPattern = Pattern
						.compile(stringsPropertiesPath);

				final ArrayList<String> langList = new ArrayList<String>();

				getResources(binJarUri.getPath(), stringsPropertiesPattern,
						new SearchResourceListener() {

							@Override
							public Object OnFind(Object fileName) {
								String baseName = FilenameUtils
										.getBaseName((String) fileName);
								String langName = langNameOfStringResource(baseName);

								if (langName != null && !langName.isEmpty()) {
									LOGGER.info("supported language found: " + langName);
									langList.add(langName);
								}

								return null;
							}
						});

				return langList;
			}
		}
		return null;
	}

	private static String langNameOfStringResource(String fileBaseName) {
		ResourceBundle iResourceBundle = ResourceBundle
				.getBundle("resources.strings." + fileBaseName);
		return iResourceBundle.getString("NameInLanguageList");
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
			final ZipEntry ze = (ZipEntry) e.nextElement();
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

		return resourcesList;
	}
}
