package lc;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import lc.cardSet.CardSet;
import lc.editView.EditView;
import lc.langCardsExeption.LangCardsExeption;
import lc.lessonView.LessonView;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class LCmain extends JFrame
					implements ActionListener {
	
	public static LCmain mainFrame;
	public Container iContainer;
	public GroupLayout iLayout;
	
	CardSet iCardSet;
	
	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	
	public static final String LC_TITLE = "Language Cards";
	private static final String LC_FILE_EXT = "lngcards";
	private static final String LC_LOG_FILE = "langCardsLog.txt";
	
	JFileChooser iFileChooser = new JFileChooser();
	FileFilter iFilefilter = null;

	public DocumentBuilder iParser;
	Document doc;
	
	Vector<JDialog> iCloseArray= new Vector<JDialog>();

	public static void main(String[] args) {
		mainFrame = new LCmain();
		mainFrame.Init();
		mainFrame.setVisible(true);
	}
	
	public LCmain() {
		setTitle(LC_TITLE); // no i18n
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setDefaultLookAndFeelDecorated(true);
	}
	
	public void Init() {
		initLogger();
		
		// set default locale: "en_EN"
		LCutils.SetLocale("en_EN");
		
		// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		iContainer = getContentPane();
		iLayout = new GroupLayout(iContainer);
		iContainer.setLayout(iLayout);
		iLayout.setAutoCreateContainerGaps(true);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			iParser = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			ShowErr(e);
			return;
		}
		
		SetFileFilterPrompt(LCutils.String("Language_Cards_file"));

		// load the last set, otherwise create new
		try {
			NewSet();
		} catch (XPathExpressionException e) {
			ShowErr(e);
			return;
		} catch (LangCardsExeption e) {
			ShowErr(e);
			return;
		}
		
		CreateMenu();
		pack();
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

	public void CreateMenu() {
		this.setJMenuBar(null); // remove menu
		
		menuBar = new JMenuBar();
		menu = new JMenu(LCutils.String("Set"));
		menuBar.add(menu);
		
		menuItem = new JMenuItem(LCutils.String("New"));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem(LCutils.String("Open"));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem(LCutils.String("Save_As") + "...");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		this.setJMenuBar(menuBar);
	}
	
	public void ShowErr(Exception e) {
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

		pack();
	}
	
	public void AddToCloseArray(JDialog dlg) {
		iCloseArray.add(dlg);
	}

	public void RemoveFromCloseArray(JDialog dlg) {
		iCloseArray.remove(dlg);
	}

	// ActionListener
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			
			actionPerformedThrow(arg0);
			
		} catch (SAXException e) {
			ShowErr(e);
		} catch (IOException e) {
			ShowErr(e);
		} catch (TransformerException e) {
			ShowErr(e);
		} catch (XPathExpressionException e) {
			ShowErr(e);
		} catch (LangCardsExeption e) {
			ShowErr(e);
		}
	}

	private void actionPerformedThrow(ActionEvent arg0) throws SAXException,
			IOException, TransformerException, XPathExpressionException,
			LangCardsExeption {
		String actionCmd = arg0.getActionCommand();
		if (actionCmd.equals(LCutils.String("New"))) {
			// iFileChooser.showDialog(this, "New");
			NewSet();
		} else if (actionCmd.equals(LCutils.String("Open"))) {
			if (iFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = iFileChooser.getSelectedFile();
				iCardSet = new CardSet(file);
				EditView editView = new EditView(iCardSet);
				editView.Show();
			}
		} else if (actionCmd.equals(LCutils.String("Save_As") + "...")) {
			if (iFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = iFileChooser.getSelectedFile();
				
				String fName = file.toString();
				fName = FilenameUtils.removeExtension(fName);
				iCardSet.Save(fName + "." + LC_FILE_EXT);
			}
		} else if (actionCmd.equals(LCutils.String("Start_lesson"))) {
			LessonView lessonView = new LessonView(iCardSet);
			lessonView.Show();
		}
	}
	
	private void NewSet() throws XPathExpressionException, LangCardsExeption {
		iCardSet = new CardSet();
		EditView editView = new EditView(iCardSet);
		editView.Show();
	}
	
	public void SetFileFilterPrompt(String ffPrompt) {
		if (iFilefilter != null) {
			iFileChooser.removeChoosableFileFilter(iFilefilter);
		}
		
		iFilefilter = new FileNameExtensionFilter(ffPrompt + " (*." + LC_FILE_EXT + ")", LC_FILE_EXT);
		iFileChooser.addChoosableFileFilter(iFilefilter);
	}
	
	public void ChangeSetNameInTitle(String setName) {
		setTitle(setName + " - " + LC_TITLE);
	}
}
