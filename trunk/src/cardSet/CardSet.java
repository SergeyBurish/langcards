package cardSet;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
import lngCard.LngCard;

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
		
		// Cards
		Element cards = iDoc.createElement("Cards");
		root.appendChild(cards);
		
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
	
	public void Save(String fileName) throws TransformerFactoryConfigurationError, TransformerException {
		//  Create transformer
		Transformer tFormer = TransformerFactory.newInstance().newTransformer();
		
		//  Output Types (text/xml/html)
		tFormer.setOutputProperty(OutputKeys.METHOD, "xml");
		
		//  Write the document to a file
		Source source = new DOMSource(iDoc);
		Result result = new StreamResult(new File(fileName));
		tFormer.transform(source, result);
	}
	
	public String Name() {
		return iName;
	}
	
	public void AddNewCard(LngCard lngCard) throws XPathExpressionException, LangCardsExeption {
		Node cards = getUniqNode("Set/Cards");
		NodeList nList = cards.getChildNodes();
		int cardsCount = nList.getLength();
		
		// new card
		Element card = iDoc.createElement("Card");
		card.setAttribute("id", "" + (cardsCount+1));
		cards.appendChild(card);
		
		// From Language phrase
		Element phrase = iDoc.createElement("Phrase");
		phrase.setAttribute("Language", LanguageFrom());
		card.appendChild(phrase);
		
		for (int i = 0; i < lngCard.FromPhraseCount(); i++) {
			Element val = iDoc.createElement("Value");
			val.setTextContent(lngCard.GetFromPhrase(i));
			//add Transcription, Example
			phrase.appendChild(val);
		}
		
		// To Language phrase
		phrase = iDoc.createElement("Phrase");
		phrase.setAttribute("Language", LanguageTo());
		card.appendChild(phrase);
		
		for (int i = 0; i < lngCard.ToPhraseCount(); i++) {
			Element val = iDoc.createElement("Value");
			val.setTextContent(lngCard.GetToPhrase(i));
			//add Transcription, Example
			phrase.appendChild(val);
		}
	}
	
	public String LanguageFrom() throws XPathExpressionException, LangCardsExeption {
		return getAttributeValue(getUniqNode("Set/Settings/Languages"), "From");
	}
	
	public String LanguageTo() throws XPathExpressionException, LangCardsExeption {
		return getAttributeValue(getUniqNode("Set/Settings/Languages"), "To");
	}
	
	private Node getUniqNode (String nodePath) throws XPathExpressionException, LangCardsExeption {
		XPathExpression expr = iXpath.compile(nodePath);
		NodeList nl = (NodeList) expr.evaluate(iDoc, XPathConstants.NODESET);
		
		if (nl.getLength() == 1) { // node is unique
			return nl.item(0);
		}
		
		throw new LangCardsExeption("no unique \"" + nodePath + "\" node");
	}
	
	private String getAttributeValue(Node nd, String attrName) throws LangCardsExeption {
		NamedNodeMap nnm = nd.getAttributes();
		
		for (int i = 0; i < nnm.getLength(); i++) {
			Node nd1 = nnm.item(i);
			
			if (nd1.getNodeName().compareTo(attrName) == 0) {
				return nd1.getNodeValue();
			}
		}			
		
		throw new LangCardsExeption("no \"" + attrName + "\" attribute");
	}
}
