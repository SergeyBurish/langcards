package LangCards;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Comment;
import org.xml.sax.SAXException;

import cardSet.CardSet;
import editView.EditView;

public class LCui extends JFrame
					implements ActionListener {
	public static LCui mainFrame;
	public Container iContainer;
	public GroupLayout iLayout;
	
	private JTextField input = new JTextField("Test", 5);
	private JLabel label = new JLabel();
	
	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	
	JFileChooser fc = new JFileChooser();
	
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	public DocumentBuilder parser;
	Document doc;
	
	public LCui() {
		setTitle("Language Cards");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setDefaultLookAndFeelDecorated(true);
		
		// init layout
		iContainer = getContentPane();
		iLayout = new GroupLayout(iContainer);//EditView
		iContainer.setLayout(iLayout);
		iLayout.setAutoCreateContainerGaps(true);

		if ( !InitParser() ) {
			ShowErr("fail init xml parser");
			return;
		}
		
		label.setText("label AAAAAAA");
				
		iLayout.setHorizontalGroup(
				iLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(label)
		);
		
		iLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {label, input});
		
		iLayout.setVerticalGroup(
				iLayout.createSequentialGroup()
				.addComponent(input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(label)
		);
		
		CreateMenu();
		pack();
	}
	
	public static void main(String[] args) {
		mainFrame = new LCui();
		mainFrame.setVisible(true);
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
		
		this.setJMenuBar(menuBar);
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
	
	private void ShowErr(String err) {
		setTitle("Error Language Cards");
		
		this.setJMenuBar(null); // remove menu
		
		iContainer.removeAll(); // remove all ui controls
		
		label.setText(err);
		
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
		}
	}

	
	private void actionPerformedThrow(ActionEvent arg0) throws SAXException, IOException {
		
        String actionCmd = arg0.getActionCommand();
        if (actionCmd.equals("New")) {
        	//fc.showDialog(this, "New");
        	
        	CardSet cs = new CardSet();
        	cs.f();
        	
        	EditView editView = new EditView(cs);
        	editView.Show();
        	
        	try {
				CreateFile();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (actionCmd.equals("Open")) {
        	int ret = fc.showOpenDialog(this);
        	
        	if (ret == JFileChooser.APPROVE_OPTION) {
        		File file = fc.getSelectedFile();
        		
        		CardSet cs = new CardSet(file);
        		cs.f();
        		
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
        }
	}
		
	private void CreateFile() throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		parser = factory.newDocumentBuilder();
		doc = parser.newDocument();
		
		// create root
		Element root = doc.createElement("rootTT");
		doc.appendChild(root);
		
		// create a comment
		Comment comment = doc.createComment("This is commentTT");
		root.appendChild(comment);
		
		// create child element 1
		Element childElement = doc.createElement("Child1");
		childElement.setAttribute("attribute1","The val of Attribute 1");
		root.appendChild(childElement);		
		
		// create child element 2
		childElement = doc.createElement("Child2");
		childElement.setAttribute("attr2","The val of Attribute 2");
		childElement.setTextContent("ZZZxxxccc Val");
		root.appendChild(childElement);
		
		//  Create transformer
		Transformer tFormer = TransformerFactory.newInstance().newTransformer();
		
		//  Output Types (text/xml/html)
		tFormer.setOutputProperty(OutputKeys.METHOD, "xml");
		
		//  Write the document to a file
		Source source = new DOMSource(doc);
		Result result = new StreamResult(new File("Test01.xml"));
		tFormer.transform(source, result);
	}
	
	private void ParseFile(File aFile) throws ParserConfigurationException, SAXException, IOException {
		parser = factory.newDocumentBuilder();
		doc = parser.parse(aFile);
		
		Node node = doc.getDocumentElement();
		String root = node.getNodeName();
		input.setText(root);
	}
}
