package lc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import lc.cardSet.CardSet;
import lc.editView.EditView;
import lc.langCardsException.LangCardsException;

import lc.lessonView.LessonView;
import lc.utils.LCutils;
import org.apache.commons.io.FilenameUtils;
import org.xml.sax.SAXException;

public class LCmain extends JFrame {

	public static LCmain mainFrame;
	private static LCutils.Settings iSettings;
	public Container iContainer;
	public GroupLayout iLayout;
	
	CardSet iCardSet;
	
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem menuItem;
	
	public static final String LC_TITLE = "Language Cards";
	private static final String LC_FILE_EXT = "lngcards";
	private static final String LC_LOG_FILE = "langCardsLog.txt";
	
	JFileChooser iFileChooser = null;
	FileFilter iFilefilter = null;

	public DocumentBuilder iParser;

	Vector<JFrame> iCloseArray= new Vector<JFrame>();

	enum State {
		EDIT,
		LESSON,
	}
	State iState;

	public static void main(String[] args) {
		mainFrame = new LCmain();
		mainFrame.init();
		mainFrame.setVisible(true);
	}
	
	public LCmain() {
		setTitle(LC_TITLE); // no i18n
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setDefaultLookAndFeelDecorated(true);
	}
	
	private void init() {
		initLogger();
		
		// set default locale: "en_EN"
		LCutils.setLocale("en_EN");

		iFileChooser = new JFileChooser() {
			@Override
			public void approveSelection() {
				String filePath = filePathWithLcExt(getSelectedFile());
				File file = new File(filePath);
				if (file.exists() && getDialogType() == SAVE_DIALOG) {

					String fName = FilenameUtils.getName(filePath);

					Object[] options = {
							LCutils.string("Yes"),
							LCutils.string("No"),
							LCutils.string("Cancel")};

					int result = JOptionPane.showOptionDialog(LCmain.mainFrame,
							String.format(LCutils.string("The_file_F_exists_overwrite"), fName),
							LCutils.string("Existing_file"),
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[0]);

					switch (result) {
						case JOptionPane.YES_OPTION:
							super.approveSelection();
							return;
						case JOptionPane.NO_OPTION:
							return;
						case JOptionPane.CLOSED_OPTION:
							return;
						case JOptionPane.CANCEL_OPTION:
							cancelSelection();
							return;
					}
				}
				else {
					super.approveSelection();
				}
			}
		};

		// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		iContainer = getContentPane();
		iLayout = new GroupLayout(iContainer);
		iContainer.setLayout(iLayout);
		iLayout.setAutoCreateContainerGaps(true);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			iParser = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			showErr(e);
			return;
		}
		
		setFileFilterPrompt(LCutils.string("Language_Cards_file"));

		// load the last set, otherwise create new
		try {
			newOpen(null);
		}
		catch (XPathExpressionException e)	{
			showErr(e);return;}
		catch (LangCardsException e)			{
			showErr(e);return;}
		catch (SAXException e)				{
			showErr(e);return;}
		catch (IOException e)				{
			showErr(e);return;}

		// initial state
		iState = State.EDIT;
		setViewBounds();

		createMenu();

		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent windowEvent) {

			}

			@Override
			public void windowClosing(WindowEvent windowEvent) {
				Rectangle frameBounds = getBounds();

				if (iSettings == null) {
					iSettings = new LCutils.Settings();
					iSettings.editWidth = frameBounds.width;
					iSettings.editHeight = frameBounds.height;
					iSettings.lessonWidth = frameBounds.width;
					iSettings.lessonHeight = frameBounds.height;
				} else {
					switch (iState) {
						case EDIT:
							iSettings.editWidth = frameBounds.width;
							iSettings.editHeight = frameBounds.height;
							break;
						case LESSON:
							iSettings.lessonWidth = frameBounds.width;
							iSettings.lessonHeight = frameBounds.height;
							break;
					}
				}

				iSettings.xPos = frameBounds.x;
				iSettings.yPos = frameBounds.y;

				LCutils.saveSettings(iSettings);
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent windowEvent) {

			}

			@Override
			public void windowIconified(WindowEvent windowEvent) {

			}

			@Override
			public void windowDeiconified(WindowEvent windowEvent) {

			}

			@Override
			public void windowActivated(WindowEvent windowEvent) {

			}

			@Override
			public void windowDeactivated(WindowEvent windowEvent) {

			}
		});
	}

	private void initLogger() {
		// reset all default loggers
		LogManager.getLogManager().reset();

		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

		// log format
		Formatter formatter = new Formatter() {
			@Override
			public String format(LogRecord arg0) {
				StringBuilder b = new StringBuilder();
				Date date = new Date();
				DateFormat df = DateFormat.getDateTimeInstance();
				b.append(df.format(date));
				b.append(" ");
				b.append(arg0.getSourceClassName());
				b.append(" ");
				b.append(arg0.getSourceMethodName());
				b.append(" ");
				b.append(arg0.getLevel());
				b.append("\t\t");
				b.append(arg0.getMessage());
				b.append(System.getProperty("line.separator"));
				return b.toString();
			}
		};

		// log to console
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(formatter);
		logger.addHandler(consoleHandler);

		// log to file
		try {
			FileHandler fileHandler = new FileHandler(LC_LOG_FILE);
			fileHandler.setFormatter(formatter);
			logger.addHandler(fileHandler);
		} catch (SecurityException e1) { // ignore all exceptions
			e1.printStackTrace();
		} catch (IOException e1) { // ignore all exceptions
			e1.printStackTrace();
		}
	}

	public void createMenu() {
		this.setJMenuBar(null); // remove menu

		menuBar = new JMenuBar();
		menu = new JMenu(LCutils.string("Set"));
		menuBar.add(menu);

		//----------------------New----------------------
		menuItem = new JMenuItem(LCutils.string("New"));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					newOpen(null);
				}
				catch (SAXException e)				{
					showErr(e);}
				catch (IOException e)				{
					showErr(e);}
				catch (LangCardsException e)			{
					showErr(e);}
				catch (XPathExpressionException e)	{
					showErr(e);}
			}
		});
		menu.add(menuItem);

		//----------------------Open----------------------
		menuItem = new JMenuItem(LCutils.string("Open"));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (iFileChooser.showOpenDialog(LCmain.this) == JFileChooser.APPROVE_OPTION) {
					File file = iFileChooser.getSelectedFile();
					try {
						newOpen(file);
					}
					catch (SAXException e)				{
						showErr(e);}
					catch (IOException e)				{
						showErr(e);}
					catch (LangCardsException e)			{
						showErr(e);}
					catch (XPathExpressionException e)	{
						showErr(e);}
				}
			}
		});
		menu.add(menuItem);

		//----------------------Save_As----------------------
		menuItem = new JMenuItem(LCutils.string("Save_As") + "...");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				saveAs();
			}
		});
		menu.add(menuItem);
		
		this.setJMenuBar(menuBar);
	}

	public boolean saveAs() {
		if (iFileChooser.showSaveDialog(LCmain.this) == JFileChooser.APPROVE_OPTION) {
			String fName = filePathWithLcExt(iFileChooser.getSelectedFile());

			try {
				iCardSet.save(fName);
			}
			catch (TransformerException e) {
				showErr(e);
				return false;
			}

			changeSetNameInTitle(iCardSet.name());
			return true;
		}

		return false;
	}

	private String filePathWithLcExt(File file) {
		String fName = file.toString();

		if (!LC_FILE_EXT.equals(FilenameUtils.getExtension(fName).toLowerCase())) {
			fName = fName + "." + LC_FILE_EXT;
		}

		return fName;
	}
	
	public void showErr(Exception e) {
		e.printStackTrace();
		
		for (int i = 0; i < iCloseArray.size(); i++) {
			iCloseArray.get(i).dispose();
		}
		iCloseArray.removeAllElements();

		setTitle("Internal Error");
		this.setJMenuBar(null); // remove menu
		iContainer.removeAll(); // remove all ui controls
		JLabel label = new JLabel(e.getMessage());
		
		iLayout.setHorizontalGroup(
				iLayout.createSequentialGroup()
				.addComponent(label)
		);
		
		iLayout.setVerticalGroup(
				iLayout.createSequentialGroup()
						.addComponent(label)
		);
	}
	
	public void addToCloseArray(JFrame dlg) {
		iCloseArray.add(dlg);
	}

	public void removeFromCloseArray(JFrame dlg) {
		iCloseArray.remove(dlg);
	}

	private void newOpen(File file) throws XPathExpressionException, LangCardsException, IOException, SAXException {
		if (file != null) {
			iCardSet = new CardSet(file);
		}

		showEdit();
	}

	public void showEdit() throws XPathExpressionException, LangCardsException {
		if (iCardSet == null) {
			iCardSet = new CardSet();
		}
		changeSetNameInTitle(iCardSet.name());

		EditView editView = new EditView(iCardSet);
		editView.show();

		if (iState == State.LESSON) {
			saveLessonSizes();
			iState = State.EDIT;
			setViewBounds();
		}
	}

	public void showLesson() throws XPathExpressionException, LangCardsException {
		if (iCardSet == null) {
			throw new LangCardsException("Lesson can't be started: card set isn't ready");
		}

		LessonView lessonView = new LessonView(iCardSet);
		lessonView.show();

		if (iState == State.EDIT) {
			saveEditSizes();
			iState = State.LESSON;
			setViewBounds();
		}
	}

	public void setFileFilterPrompt(String ffPrompt) {
		if (iFilefilter != null) {
			iFileChooser.removeChoosableFileFilter(iFilefilter);
		}

		iFileChooser.setAcceptAllFileFilterUsed(false);

		iFilefilter = new FileNameExtensionFilter(ffPrompt + " (*." + LC_FILE_EXT + ")", LC_FILE_EXT);
		iFileChooser.addChoosableFileFilter(iFilefilter);

		iFileChooser.setAcceptAllFileFilterUsed(true);
	}
	
	public void changeSetNameInTitle(String setName) {
		setTitle(setName + " - " + LC_TITLE);
	}

	private void setViewBounds() {
		if (iSettings == null) {
			iSettings = LCutils.loadSettings();
		}

		switch (iState) {
			case EDIT:
				if (iSettings.editWidth > 0 && iSettings.editHeight > 0) {
					mainFrame.setBounds(iSettings.xPos, iSettings.yPos, iSettings.editWidth, iSettings.editHeight);
					return;
				}
				break;
			case LESSON:
				if (iSettings.lessonWidth > 0 && iSettings.lessonHeight > 0) {
					mainFrame.setBounds(iSettings.xPos, iSettings.yPos, iSettings.lessonWidth, iSettings.lessonHeight);
					return;
				}
				break;
		}

		mainFrame.pack();
	}

	public void saveEditSizes() {
		if (iSettings == null) {
			iSettings = LCutils.loadSettings();
		}
		Rectangle frameBounds = getBounds();
		iSettings.xPos = frameBounds.x;
		iSettings.yPos = frameBounds.y;
		iSettings.editWidth = frameBounds.width;
		iSettings.editHeight = frameBounds.height;
	}

	public void saveLessonSizes() {
		if (iSettings == null) {
			iSettings = LCutils.loadSettings();
		}
		Rectangle frameBounds = getBounds();
		iSettings.xPos = frameBounds.x;
		iSettings.yPos = frameBounds.y;
		iSettings.lessonWidth = frameBounds.width;
		iSettings.lessonHeight = frameBounds.height;
	}

	public static LCutils.Settings getSettings() {
		if (iSettings == null) {
			iSettings = LCutils.loadSettings();
		}
		return iSettings;
	}
}
