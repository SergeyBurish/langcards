package LangCards;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import langCardsExeption.LangCardsExeption;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import cardSet.CardSet;
import editView.EditView;

public class LCui extends JFrame
					implements ActionListener {
	public static LCui mainFrame;
	public Container iContainer;
	public GroupLayout iLayout;
	
	CardSet iCardSet;
	
	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	
	JFileChooser iFileChooser = new JFileChooser();
	FileFilter iFilefilter = new FileNameExtensionFilter("Language Cards file", "lngcards");
	
	public DocumentBuilder iParser;
	Document doc;
	
	public static void main(String[] args) {
		mainFrame = new LCui();
		mainFrame.Init();
		mainFrame.setVisible(true);
	}
	
	public LCui() {
		setTitle("Language Cards");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setDefaultLookAndFeelDecorated(true);
	}
	
	public void Init() {
		iContainer = getContentPane();
		iLayout = new GroupLayout(iContainer);
		iContainer.setLayout(iLayout);
		iLayout.setAutoCreateContainerGaps(true);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			iParser = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			ShowErr(e);
		}
		
		iFileChooser.addChoosableFileFilter(iFilefilter);

		// load the last set, otherwise create new
		try {
			NewSet();
		} catch (XPathExpressionException e) {
			ShowErr(e);
		} catch (LangCardsExeption e) {
			ShowErr(e);
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

	
	private void actionPerformedThrow(ActionEvent arg0) throws SAXException, IOException, TransformerException, XPathExpressionException, LangCardsExeption {
		String actionCmd = arg0.getActionCommand();
		if (actionCmd.equals("New")) {
			//iFileChooser.showDialog(this, "New");
			NewSet();
		} else if (actionCmd.equals("Open")) {
			if (iFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = iFileChooser.getSelectedFile();
				iCardSet = new CardSet(file);
				EditView editView = new EditView(iCardSet);
				editView.Show();
			}
		} else if (actionCmd.equals("Save As...")) {
			if ( iFileChooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
				File file = iFileChooser.getSelectedFile();
				
				String  fName = file.toString();
				fName = FilenameUtils.removeExtension(fName);
				iCardSet.Save(fName + ".lngcards");
			}
		}
	}
	
	private void NewSet() throws XPathExpressionException, LangCardsExeption {
		iCardSet = new CardSet();
		EditView editView = new EditView(iCardSet);
		editView.Show();
	}
}
