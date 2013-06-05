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

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class LCmain extends JFrame {

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
	
	JFileChooser iFileChooser = null;
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
	
	private void Init() {
		initLogger();
		
		// set default locale: "en_EN"
		LCutils.SetLocale("en_EN");

		iFileChooser = new JFileChooser() {
			@Override
			public void approveSelection() {
				String filePath = filePathWithLcExt(getSelectedFile());
				File file = new File(filePath);
				if (file.exists() && getDialogType() == SAVE_DIALOG) {

					String fName = FilenameUtils.getName(filePath);

					Object[] options = {
							LCutils.String("Yes"),
							LCutils.String("No"),
							LCutils.String("Cancel")};

					int result = JOptionPane.showOptionDialog(LCmain.mainFrame,
							String.format(LCutils.String("The_file_F_exists_overwrite"), fName),
							LCutils.String("Existing_file"),
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
			ShowErr(e);
			return;
		}
		
		SetFileFilterPrompt(LCutils.String("Language_Cards_file"));

		// load the last set, otherwise create new
		try {
			NewOpen(null);
		}
		catch (XPathExpressionException e)	{ShowErr(e);return;}
		catch (LangCardsException e)			{ShowErr(e);return;}
		catch (SAXException e)				{ShowErr(e);return;}
		catch (IOException e)				{ShowErr(e);return;}

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

		//----------------------New----------------------
		menuItem = new JMenuItem(LCutils.String("New"));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					NewOpen(null);
				}
				catch (SAXException e)				{ShowErr(e);}
				catch (IOException e)				{ShowErr(e);}
				catch (LangCardsException e)			{ShowErr(e);}
				catch (XPathExpressionException e)	{ShowErr(e);}
			}
		});
		menu.add(menuItem);

		//----------------------Open----------------------
		menuItem = new JMenuItem(LCutils.String("Open"));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (iFileChooser.showOpenDialog(LCmain.this) == JFileChooser.APPROVE_OPTION) {
					File file = iFileChooser.getSelectedFile();
					try {
						NewOpen(file);
					}
					catch (SAXException e)				{ShowErr(e);}
					catch (IOException e)				{ShowErr(e);}
					catch (LangCardsException e)			{ShowErr(e);}
					catch (XPathExpressionException e)	{ShowErr(e);}
				}
			}
		});
		menu.add(menuItem);

		//----------------------Save_As----------------------
		menuItem = new JMenuItem(LCutils.String("Save_As") + "...");
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
				ShowErr(e);
				return false;
			}

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

	private void NewOpen(File file) throws XPathExpressionException, LangCardsException, IOException, SAXException {
		iCardSet = (file == null) ? new CardSet() : new CardSet(file);
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
