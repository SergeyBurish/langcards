package lc.cardSet;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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

import lc.LCmain;
import lc.langCardsExeption.LangCardsExeption;
import lc.cardSet.lngCard.LngCard;

public class CardSet {
	private Document iDoc;
	private String iName;
	
	XPathFactory iXPathfactory = XPathFactory.newInstance();
	XPath iXpath = iXPathfactory.newXPath();
	
	// Path Expressions of frequent operations 
	XPathExpression iSettings_Languages_From = null;
	XPathExpression iSettings_Languages_To = null;
	
	public CardSet() {
		// new
		iName = "New set";
		iDoc = LCmain.mainFrame.iParser.newDocument();
		InitDoc();
	}
	
	public CardSet(File file) throws SAXException, IOException {
		iName = file.getName();
		iDoc = LCmain.mainFrame.iParser.parse(file);
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
		
		//TestFilling();
	}
	
	private void TestFilling() {
		try {
			LngCard lc = new LngCard();
			lc.AddFromPhrase("F111@ ");
			lc.AddToPhrase("T111@ ");
			AddNewCard(lc);
			
			lc = new LngCard();
			lc.AddFromPhrase("F222-1@ ");
			lc.AddFromPhrase("F222-222@ ");
			lc.AddToPhrase("T2@ ");
			AddNewCard(lc);
			
			lc = new LngCard();
			lc.AddFromPhrase("F3-1@ ");
			lc.AddFromPhrase("F3-2@ ");
			lc.AddToPhrase("T333-1@ ");
			lc.AddToPhrase("T333-2@ ");
			AddNewCard(lc);
			
			lc = new LngCard();
			lc.AddFromPhrase("F44@ ");
			lc.AddToPhrase("T44444@ ");
			AddNewCard(lc);
			
			lc = new LngCard();
			lc.AddFromPhrase("F5@ ");
			lc.AddToPhrase("T5@ ");
			AddNewCard(lc);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LangCardsExeption e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Save(String fileName) throws TransformerException {
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
	
	public int CardsCount() throws XPathExpressionException, LangCardsExeption {
		Node cardsNd = getUniqNode("Set/Cards");
		NodeList cardListNdL = cardsNd.getChildNodes();

		return cardListNdL.getLength();
	}
	
	public void AddNewCard(LngCard lngCard) throws XPathExpressionException, LangCardsExeption {
		Node cards = getUniqNode("Set/Cards");
		NodeList nList = cards.getChildNodes();
		int cardsCount = nList.getLength();
		
		// new card
		Element card = iDoc.createElement("Card");
		card.setAttribute("id", "" + (cardsCount+1));
		cards.appendChild(card);
		
		// From Language phrases
		for (int i = 0; i < lngCard.FromPhraseCount(); i++) {
			Element phrase = iDoc.createElement("Phrase");
			phrase.setAttribute("Language", LanguageFrom());

			Element val = iDoc.createElement("Value");
			val.setTextContent(lngCard.GetFromPhrase(i));
			
			phrase.appendChild(val);
			//add Transcription, Examples
			
			card.appendChild(phrase);
		}
		
		// To Language phrases
		for (int i = 0; i < lngCard.ToPhraseCount(); i++) {
			Element phrase = iDoc.createElement("Phrase");
			phrase.setAttribute("Language", LanguageTo());
			
			Element val = iDoc.createElement("Value");
			val.setTextContent(lngCard.GetToPhrase(i));
			
			phrase.appendChild(val);
			//add Transcription, Examples
			
			card.appendChild(phrase);
		}
	}
	
	public Vector<Vector<String>> GetAllCardsIdFromTo() throws XPathExpressionException, LangCardsExeption {
		Vector<Vector<String>> rowsVect=new Vector<Vector<String>>();
		
		Node cardsNd = getUniqNode("Set/Cards");
		NodeList cardList = cardsNd.getChildNodes();
		
		String fromLanguageStr = LanguageFrom();
		String toLanguageStr = LanguageTo();

		for (int i = 0; i < cardList.getLength(); i++) {
			Vector<String> rowVect=new Vector<String>();
			
			Node card = cardList.item(i);
			if (card == null) throw new LangCardsExeption("xml error: fail to get next \"Card\" node");
			
			String cardID = iXpath.evaluate("@id", card);  // "id" attribute of <Card/>
			rowVect.addElement(cardID);
			
			 // get the first "FROM" phrase
			NodeList fromPhraseList = (NodeList)iXpath.evaluate(String.format("Phrase[@Language='%s']", fromLanguageStr), card, XPathConstants.NODESET);
			if (fromPhraseList.getLength() < 1) throw new LangCardsExeption(String.format("invalid xml structure: no %s Phrases in the %S card", fromLanguageStr, cardID));
			Node fromPhrase = fromPhraseList.item(0);
			
			rowVect.addElement(iXpath.evaluate("Value", fromPhrase));
			
			
			 // get the first "TO" phrase
			NodeList toPhraseList = (NodeList)iXpath.evaluate(String.format("Phrase[@Language='%s']", toLanguageStr), card, XPathConstants.NODESET);
			if (toPhraseList.getLength() < 1) throw new LangCardsExeption(String.format("invalid xml structure: no %s Phrases in the %S card", toLanguageStr, cardID));
			Node toPhrase = toPhraseList.item(0);
			
			rowVect.addElement(iXpath.evaluate("Value", toPhrase));
			

			rowsVect.addElement(rowVect);
		}

		return rowsVect;
	}
	
	public String LanguageFrom() throws XPathExpressionException {
		if (iSettings_Languages_From == null) {
			iSettings_Languages_From = iXpath.compile("Set/Settings/Languages/@From");
		}
		
		return iSettings_Languages_From.evaluate(iDoc);
	}
	
	public String LanguageTo() throws XPathExpressionException {
		if (iSettings_Languages_To == null) {
			iSettings_Languages_To = iXpath.compile("Set/Settings/Languages/@To");
		}
		
		return iSettings_Languages_To.evaluate(iDoc);
	}
	
	private Node getUniqNode (String nodePath) throws XPathExpressionException, LangCardsExeption {
		NodeList nl = (NodeList) iXpath.evaluate(nodePath, iDoc, XPathConstants.NODESET);
		
		if (nl.getLength() == 1) { // node is unique
			return nl.item(0);
		}
		
		throw new LangCardsExeption("no unique \"" + nodePath + "\" node");
	}
}
