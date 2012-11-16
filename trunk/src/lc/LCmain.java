package lc;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import lc.langCardsExeption.LangCardsExeption;
import lc.lessonView.LessonView;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import lc.cardSet.CardSet;
import lc.editView.EditView;

public class LCmain extends JFrame
					implements ActionListener {
	public static LCmain mainFrame;
	public Container iContainer;
	public GroupLayout iLayout;
	
	CardSet iCardSet;
	
	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	
	JFileChooser iFileChooser = new JFileChooser();
	FileFilter iFilefilter = new FileNameExtensionFilter("Language Cards file",
			"lngcards");

	public DocumentBuilder iParser;
	Document doc;
	
	Vector<JDialog> iCloseArray= new Vector<JDialog>();

	public static void main(String[] args) {
		mainFrame = new LCmain();
		mainFrame.Init();
		mainFrame.setVisible(true);
	}
	
	public LCmain() {
		setTitle("Language Cards");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setDefaultLookAndFeelDecorated(true);
	}
	
	public void Init() {
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
		
		iFileChooser.addChoosableFileFilter(iFilefilter);

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
	
	private void CreateMenu() {
		menuBar = new JMenuBar();
		menu = new JMenu("Set");
		menuBar.add(menu);
		
		menuItem = new JMenuItem("New");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Open");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Save As...");
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
		if (actionCmd.equals("New")) {
			// iFileChooser.showDialog(this, "New");
			NewSet();
		} else if (actionCmd.equals("Open")) {
			if (iFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = iFileChooser.getSelectedFile();
				iCardSet = new CardSet(file);
				EditView editView = new EditView(iCardSet);
				editView.Show();
			}
		} else if (actionCmd.equals("Save As...")) {
			if (iFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = iFileChooser.getSelectedFile();
				
				String fName = file.toString();
				fName = FilenameUtils.removeExtension(fName);
				iCardSet.Save(fName + ".lngcards");
			}
		} else if (actionCmd.equals("Start lesson")) {
			LessonView lessonView = new LessonView(iCardSet);
			lessonView.Show();
		}
	}
	
	private void NewSet() throws XPathExpressionException, LangCardsExeption {
		iCardSet = new CardSet();
		EditView editView = new EditView(iCardSet);
		editView.Show();
	}
}
