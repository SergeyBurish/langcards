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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lc.LCmain;
import lc.langCardsExeption.LangCardsExeption;
import lc.cardSet.lngCard.LngCard;
import lc.cardSet.lngPhrase.LngPhrase;

public class CardSet {
	private Document iDoc;
	private String iName;
	
	XPath iXpath = XPathFactory.newInstance().newXPath();
	
	// Path Expressions of frequent operations
	XPathExpression iSetCards = null;                // Set/Cards
	XPathExpression iCardsCard = null;               // Set/Cards/Card
	XPathExpression iLessonCards = null;             // Set/Cards/Card[@status='lesson']
	XPathExpression iIdExpr = null;                  // @id
	XPathExpression iValueExpr = null;               // Value
	XPathExpression iPhraseLanguageFrom = null;      // Phrase[@Language='fromLanguage']
	XPathExpression iPhraseLanguageTo = null;        // Phrase[@Language='toLanguage']
	XPathExpression iSettings_Languages_From = null; // Set/Settings/Languages/@From
	XPathExpression iSettings_Languages_To = null;   // Set/Settings/Languages/@To
	
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
		
		TestFilling();
	}
	
	private void TestFilling() {
		try {
			LngCard lc = new LngCard();
			lc.AddFromPhrase(new LngPhrase("F111@ "));
			lc.AddToPhrase(new LngPhrase("T111@ "));
			AddNewCard(lc);
			
			lc = new LngCard();
			LngPhrase lngPhrase = new LngPhrase("F222-1@ ");
			lngPhrase.iTranscription = "[trans F222-1]";
			lngPhrase.iExamples.add("Examp1 F222-1");
			lngPhrase.iExamples.add("Examp2 F222-1");
			lc.AddFromPhrase(lngPhrase);
			
			lngPhrase = new LngPhrase("F222-222@ ");
			lngPhrase.iTranscription = "[trans F222-222]";
			lc.AddFromPhrase(lngPhrase);
			
			lc.AddToPhrase(new LngPhrase("T2@ "));
			AddNewCard(lc);
			
			lc = new LngCard();
			lc.AddFromPhrase(new LngPhrase("F3-1@ "));
			lc.AddFromPhrase(new LngPhrase("F3-2@ "));
			lc.AddToPhrase(new LngPhrase("T333-1@ "));
			
			lngPhrase = new LngPhrase("T333-2@ ");
			lngPhrase.iTranscription = "[trans T333-2]";
			lngPhrase.iExamples.add("Examp1 T333-2");
			lc.AddToPhrase(lngPhrase);
			AddNewCard(lc);
			
			lc = new LngCard();
			lc.AddFromPhrase(new LngPhrase("F44@ "));
			lc.AddToPhrase(new LngPhrase("T44444@ "));
			AddNewCard(lc);
			
			lc = new LngCard();
			lc.AddFromPhrase(new LngPhrase("F5-1@ "));
			lc.AddFromPhrase(new LngPhrase("F5-2@ "));
			lc.AddFromPhrase(new LngPhrase("F5-3@ "));
			lc.AddFromPhrase(new LngPhrase("F5-4@ "));
			lc.AddFromPhrase(new LngPhrase("F5-5@ "));
			lc.AddToPhrase(new LngPhrase("T5@ "));
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
	
	public int CardsCount() throws XPathExpressionException{
		return  CardsList().getLength();
	}
	
	public int  LessonCardsCount() throws XPathExpressionException {
		return  LessonCardsList().getLength();
	}
	
	public void AddNewCard(LngCard lngCard) throws XPathExpressionException, LangCardsExeption {
		Node cards = CardsNode();
		
		// new card
		Element card = iDoc.createElement("Card");
		card.setAttribute("id", "" + FreeCardId());
		card.setAttribute("status", "lesson");
		cards.appendChild(card);
		
		// From Language phrases
		for (int i = 0; i < lngCard.FromPhraseCount(); i++) {
			Element phrase = PhraseToElement(lngCard.GetFromPhrase(i));
			phrase.setAttribute("Language", LanguageFrom());
			card.appendChild(phrase);
		}
		
		// To Language phrases
		for (int i = 0; i < lngCard.ToPhraseCount(); i++) {
			Element phrase = PhraseToElement(lngCard.GetToPhrase(i));
			phrase.setAttribute("Language", LanguageTo());
			card.appendChild(phrase);
		}
	}
	
	private Element PhraseToElement(LngPhrase lngPhrase) {
		Element phrase = iDoc.createElement("Phrase");
		
		// Value
		Element val = iDoc.createElement("Value");
		val.setTextContent(lngPhrase.iValue);
		phrase.appendChild(val);
		
		// Transcription
		if (lngPhrase.iTranscription != null && !lngPhrase.iTranscription.isEmpty()) {
			Element transcr = iDoc.createElement("Transcription");
			transcr.setTextContent(lngPhrase.iTranscription);
			phrase.appendChild(transcr);
		}
		
		// Example
		for (int j = 0; j < lngPhrase.iExamples.size(); j++) {
			Element example = iDoc.createElement("Example");
			example.setTextContent(lngPhrase.iExamples.get(j));
			phrase.appendChild(example);
		}
		
		return phrase;
	}
	
	public Vector<Vector<String>> GetAllCardsIdFromTo() throws XPathExpressionException, LangCardsExeption {
		Vector<Vector<String>> rowsVect=new Vector<Vector<String>>();
		
		NodeList cardList = CardsList();
		
		String fromLanguageStr = LanguageFrom();
		String toLanguageStr = LanguageTo();
		
		for (int i = 0; i < cardList.getLength(); i++) {
			Vector<String> rowVect=new Vector<String>();
			
			Node card = cardList.item(i);
			if (card == null) throw new LangCardsExeption("xml error: fail to get next \"Card\" node");
			
			rowVect.addElement(CardId(card));
			
			 // get the first "FROM" phrase
			NodeList fromPhraseList = FromPhrasesOfCard(card, fromLanguageStr);
			Node fromPhrase = fromPhraseList.item(0);
			rowVect.addElement(ValueOfPhrase(fromPhrase));
			
			 // get the first "TO" phrase
			NodeList toPhraseList = ToPhrasesOfCard(card, toLanguageStr);
			Node toPhrase = toPhraseList.item(0);
			rowVect.addElement(ValueOfPhrase(toPhrase));
			

			rowsVect.addElement(rowVect);
		}

		return rowsVect;
	}
	
	public Lesson newLesson() throws XPathExpressionException {
		return new Lesson(this);
	}
	
	public LngCard NodeToCard(Node node) throws XPathExpressionException, LangCardsExeption {
		if (node == null) return null;
		
		LngCard lc = new LngCard();
		
		NodeList fromPhraseList = FromPhrasesOfCard(node, LanguageFrom());
		
		for (int i = 0; i < fromPhraseList.getLength(); i++) {
			Node fromPhrase = fromPhraseList.item(i);
			lc.AddFromPhrase(new LngPhrase(ValueOfPhrase(fromPhrase)));
		}
		
		
		NodeList toPhraseList = FromPhrasesOfCard(node, LanguageTo());
		
		for (int i = 0; i < toPhraseList.getLength(); i++) {
			Node toPhrase = toPhraseList.item(i);
			lc.AddToPhrase(new LngPhrase(ValueOfPhrase(toPhrase)));
		}
		
		return lc;
	}
	
	private Node CardsNode () throws XPathExpressionException {
		if (iSetCards == null) {
			iSetCards = iXpath.compile("Set/Cards");
		}
		
		return (Node)iSetCards.evaluate(iDoc, XPathConstants.NODE);
	}
	
	private NodeList CardsList() throws XPathExpressionException {
		if (iCardsCard == null) {
			iCardsCard = iXpath.compile("Set/Cards/Card");
		}
		
		return (NodeList) iCardsCard.evaluate(iDoc, XPathConstants.NODESET);
	}
	
	public NodeList LessonCardsList() throws XPathExpressionException {
		if (iLessonCards == null) {
			iLessonCards = iXpath.compile("Set/Cards/Card[@status='lesson']");
		}
		
		return (NodeList) iLessonCards.evaluate(iDoc, XPathConstants.NODESET);
	}
	
	private String CardId(Node card) throws XPathExpressionException {
		if (iIdExpr == null) {
			iIdExpr = iXpath.compile("@id");
		}
		
		return iIdExpr.evaluate(card);  // "id" attribute of <Card/>
	}
	
	private NodeList FromPhrasesOfCard(Node card, String fromLanguage) throws XPathExpressionException, LangCardsExeption {
		if (iPhraseLanguageFrom == null) {
			iPhraseLanguageFrom = iXpath.compile(String.format("Phrase[@Language='%s']", fromLanguage));
		}
		
		NodeList fromPhraseList = (NodeList)iPhraseLanguageFrom.evaluate(card, XPathConstants.NODESET);
		if (fromPhraseList.getLength() < 1) throw new LangCardsExeption(String.format("invalid xml structure: no %s Phrases in the %S card", fromLanguage, CardId(card)));
		
		return fromPhraseList;
	}
	
	private NodeList ToPhrasesOfCard(Node card, String toLanguage) throws XPathExpressionException, LangCardsExeption {
		if (iPhraseLanguageTo == null) {
			iPhraseLanguageTo = iXpath.compile(String.format("Phrase[@Language='%s']", toLanguage));
		}
		
		NodeList toPhraseList = (NodeList)iPhraseLanguageTo.evaluate(card, XPathConstants.NODESET);
		if (toPhraseList.getLength() < 1) throw new LangCardsExeption(String.format("invalid xml structure: no %s Phrases in the %S card", toLanguage, CardId(card)));
		
		return toPhraseList;
	}
	
	private String ValueOfPhrase(Node phrase) throws XPathExpressionException {
		if (iValueExpr == null) {
			iValueExpr = iXpath.compile("Value");
		}
		
		return iValueExpr.evaluate(phrase);
	}
	
	private int FreeCardId() throws XPathExpressionException {
		return CardsCount() + 1; // temporary
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
}
