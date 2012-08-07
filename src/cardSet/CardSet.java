package cardSet;

import java.io.File;
import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import LangCards.LCui;
import langCardsExeption.LangCardsExeption;

public class CardSet {
	private Document iDoc;
	private String iName;
	
	XPathFactory iXPathfactory = XPathFactory.newInstance();
	XPath iXpath = iXPathfactory.newXPath();
	
	public CardSet() {
		// new
		iName = "New set";
		iDoc = LCui.mainFrame.parser.newDocument();
		InitDoc();
	}
	
	public CardSet(File file) throws SAXException, IOException {
		iName = file.getName();
		iDoc = LCui.mainFrame.parser.parse(file);
		iDoc.createElement("rootTT");
	}
	
	public void InitDoc() {
		// Set
		Element root = iDoc.createElement("Set");
		root.setAttribute("name", iName);
		iDoc.appendChild(root);
		
		// Settings
		Element settings = iDoc.createElement("Settings");
		root.appendChild(settings);
		
		// Languages
		Element languages = iDoc.createElement("Languages");
		languages.setAttribute("From", "English");
		languages.setAttribute("To", "Russian");
		settings.appendChild(languages);		
		
		/*
		//  Create transformer - to check xml structure
		Transformer tFormer = null;
		try {
			tFormer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//  Output Types (text/xml/html)
		tFormer.setOutputProperty(OutputKeys.METHOD, "xml");
		
		//  Write the document to a file
		Source source = new DOMSource(iDoc);
		Result result = new StreamResult(new File("Test0EEnn.xml"));
		try {
			tFormer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	public String Name() {
		return iName;
	}
	
	public String LanguageFrom() throws XPathExpressionException, LangCardsExeption {
		Node nd = getUnicNode("Set/Settings/Languages");
		if (nd != null) {
			return getAttributeValue(nd, "From");
		}
		
		throw new LangCardsExeption("Error: no \"Languages\" node");
	}
	
	public String LanguageTo() throws XPathExpressionException, LangCardsExeption {
		Node nd = getUnicNode("Set/Settings/Languages");
		if (nd != null) {
			return getAttributeValue(nd, "To");
		}
		
		throw new LangCardsExeption("Error: no \"Languages\" node");
	}
	
	private Node getUnicNode (String path) throws XPathExpressionException {
		XPathExpression expr = iXpath.compile(path);
		NodeList nl = (NodeList) expr.evaluate(iDoc, XPathConstants.NODESET);
		
		if (nl.getLength() == 1) { // node is unic
			return nl.item(0);
		}
		return null;
	}
	
	private String getAttributeValue(Node nd, String attrName) throws LangCardsExeption {
		NamedNodeMap nnm = nd.getAttributes();
		
		for (int i = 0; i < nnm.getLength(); i++) {
			Node nd1 = nnm.item(i);
			
			if (nd1.getNodeName().compareTo(attrName) == 0) {
				return nd1.getNodeValue();
			}				
		}			
		
		throw new LangCardsExeption("Error: no \"" + attrName + "\" attribute");
	}
}
