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
	
	private JTextField input = new JTextField("Test", 5);
		
	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	
	JFileChooser iFileChooser = new JFileChooser();
	
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	public DocumentBuilder parser;
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

		if ( !InitParser() ) {
			ShowErr("fail init xml parser");
			return;
		}
		
		// load the last set, otherwise create new
		NewSet();
		
		CreateMenu();
		pack();		
	}
	
	private boolean InitParser() {
		try {
			parser = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		}
		return true;
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
		
	public void ShowErr(String err) {
		setTitle("Error Language Cards");
		
		this.setJMenuBar(null); // remove menu
		
		iContainer.removeAll(); // remove all ui controls
		
		JLabel label = new JLabel(err);
		
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
			e.printStackTrace();
			ShowErr(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			ShowErr(e.getMessage());
		} catch (TransformerException e) {
			e.printStackTrace();
			ShowErr(e.getMessage());
		}
	}

	
	private void actionPerformedThrow(ActionEvent arg0) throws SAXException, IOException, TransformerException {
		String actionCmd = arg0.getActionCommand();
		if (actionCmd.equals("New")) {
			//iFileChooser.showDialog(this, "New");
			NewSet();
		} else if (actionCmd.equals("Open")) {
			int ret = iFileChooser.showOpenDialog(this);
			
			if (ret == JFileChooser.APPROVE_OPTION) {
				File file = iFileChooser.getSelectedFile();
				
				CardSet cs = new CardSet(file);
				cs.Name();
				
				try {
					ParseFile(file);
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (actionCmd.equals("Save As...")) {
			FileFilter filter = new FileNameExtensionFilter("Language Cards file", "lngcards");
			iFileChooser.addChoosableFileFilter(filter);
			if ( iFileChooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION ) {
				File file = iFileChooser.getSelectedFile();
				
				String  fName = file.toString();
				fName = FilenameUtils.removeExtension(fName);
				iCardSet.Save(fName + ".lngcards");
			}
		}
	}
	
	private void NewSet() {
		iCardSet = new CardSet();
		
		EditView editView = new EditView(iCardSet);
		
		try {
			editView.Show();
		} catch (XPathExpressionException e) {
			ShowErr(e.getMessage());
			e.printStackTrace();
		} catch (LangCardsExeption e) {
			ShowErr(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void ParseFile(File aFile) throws ParserConfigurationException, SAXException, IOException {
		parser = factory.newDocumentBuilder();
		doc = parser.parse(aFile);
		
		Node node = doc.getDocumentElement();
		String root = node.getNodeName();
		input.setText(root);
	}
}
