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
import lc.LCutils;
import lc.langCardsException.LangCardsException;
import lc.cardSet.lngCard.LngCard;
import lc.cardSet.lngPhrase.LngPhrase;

public class CardSet {
	private Document iDoc;
	private String iName;
	
	XPath iXpath = XPathFactory.newInstance().newXPath();
	
	// xml structure elements
	private static final String XML_SET				= "Set";			// <Set name=...>
	private static final String XML_NAME			= "name";			// |
	private static final String XML_CARDS			= "Cards";			// |- <Cards>
	private static final String XML_CARD			= "Card";			// |  |- <Card id=... status=...>
	private static final String XML_ID				= "id";				// |     |
	private static final String XML_STATUS			= "status";			// |     |
	private static final String XML_PHRASE			= "Phrase";			// |     |- <Phrase Language=...> 
	private static final String XML_LANGUAGE		= "Language";		// |        |
	private static final String XML_VALUE			= "Value";			// |        |- <Value>...</>
	private static final String XML_TRANSCRIPTION	= "Transcription";	// |        |- <Transcription>...</>
	private static final String XML_EXAMPLE			= "Example";		// |        |- <Example>...</>
	private static final String XML_SETTINGS		= "Settings";		// |- <Settings>
	private static final String XML_LANGUAGES		= "Languages";		//    |- <Languages Frst=... Scnd=...>
	private static final String XML_FRST			= "Frst";
	private static final String XML_SCND			= "Scnd";
	
	// Path Expressions of frequent operations
	XPathExpression iSetCards = null;                // Set/Cards
	XPathExpression iCardsCard = null;               // Set/Cards/Card
	XPathExpression iLessonCards = null;             // Set/Cards/Card[@status='lesson']
	XPathExpression iIdExpr = null;                  // @id
	XPathExpression iValueExpr = null;               // Value
	XPathExpression iTranscriptionExpr = null;       // Transcription
	XPathExpression iExampleExpr = null;             // Example
	XPathExpression iPhraseLanguageFrst = null;      // Phrase[@Language='frstLanguage']
	XPathExpression iPhraseLanguageScnd = null;      // Phrase[@Language='scndLanguage']
	XPathExpression iSettings_Languages_Frst = null; // Set/Settings/Languages/@Frst
	XPathExpression iSettings_Languages_Scnd = null; // Set/Settings/Languages/@Scnd
	
	public CardSet() {
		// new
		iName = LCutils.String("Unnamed");
		iDoc = LCmain.mainFrame.iParser.newDocument();
		InitDoc();
	}
	
	public CardSet(File file) throws SAXException, IOException {
		iName = file.getName();
		iDoc = LCmain.mainFrame.iParser.parse(file);
	}
	
	public void InitDoc() {
		// Set
		Element root = iDoc.createElement(XML_SET);
		root.setAttribute(XML_NAME, iName);
		iDoc.appendChild(root);
		
		// Cards
		Element cards = iDoc.createElement(XML_CARDS);
		root.appendChild(cards);
		
		// Settings
		Element settings = iDoc.createElement(XML_SETTINGS);
		root.appendChild(settings);
		
		// Languages
		Element languages = iDoc.createElement(XML_LANGUAGES);
		languages.setAttribute(XML_FRST, "English");
		languages.setAttribute(XML_SCND, "Русский");
		settings.appendChild(languages);
		
		TestFilling();
	}
	
	private void TestFilling() {
		try {
			LngCard lc = new LngCard();
			lc.AddFrstPhrase(new LngPhrase("F111-1@ "));
			lc.AddFrstPhrase(new LngPhrase("F111-2@ "));
			lc.AddScndPhrase(new LngPhrase("T111@ "));
			AddNewCard(lc);
			
			lc = new LngCard();
			LngPhrase lngPhrase = new LngPhrase("F222-1@ ");
			lngPhrase.iTranscription = "[trans F222-1]";
			lngPhrase.iExamples.add("Examp1 F222-1");
			lngPhrase.iExamples.add("Examp2 F222-1");
			lc.AddFrstPhrase(lngPhrase);
			
			lngPhrase = new LngPhrase("F222-222@ ");
			lngPhrase.iTranscription = "[trans F222-222]";
			lc.AddFrstPhrase(lngPhrase);
			
			lc.AddScndPhrase(new LngPhrase("T2@ "));
			AddNewCard(lc);
			
			lc = new LngCard();
			lc.AddFrstPhrase(new LngPhrase("F3-1@ "));
			lc.AddFrstPhrase(new LngPhrase("F3-2@ "));
			lc.AddScndPhrase(new LngPhrase("T333-1@ "));
			
			lngPhrase = new LngPhrase("T333-2@ ");
			lngPhrase.iTranscription = "[trans T333-2]";
			lngPhrase.iExamples.add("Examp1 T333-2");
			lc.AddScndPhrase(lngPhrase);
			AddNewCard(lc);
			
			lc = new LngCard();
			lc.AddFrstPhrase(new LngPhrase("F44@ "));
			lc.AddScndPhrase(new LngPhrase("T44444@ "));
			AddNewCard(lc);
			
			lc = new LngCard();
			lc.AddFrstPhrase(new LngPhrase("F5-1@ "));
			lc.AddFrstPhrase(new LngPhrase("F5-2@ "));
			
			lngPhrase = new LngPhrase("F5-3@ ");
			lngPhrase.iTranscription = "[trans F5-3]";
			lc.AddFrstPhrase(lngPhrase);
			
			lc.AddFrstPhrase(new LngPhrase("F5-4@ "));
			lc.AddFrstPhrase(new LngPhrase("F5-5@ "));
			lc.AddScndPhrase(new LngPhrase("T5@ "));
			AddNewCard(lc);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LangCardsException e) {
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
	
	public void SetName(String name) {
		if (name != null && !name.isEmpty()) {
			iName = name;
			
			Element root = iDoc.getDocumentElement(); // get root (XML_SET node)
			String nameString = root.getAttribute(XML_NAME);
			if (nameString != null && !nameString.isEmpty()) {
				root.removeAttribute(XML_NAME);
				root.setAttribute(XML_NAME, name);
			}
		}
	}
	
	public int CardsCount() throws XPathExpressionException{
		return  CardsList().getLength();
	}
	
	public int  LessonCardsCount() throws XPathExpressionException {
		return  LessonCardsList().getLength();
	}
	
	public void AddNewCard(LngCard lngCard) throws XPathExpressionException, LangCardsException {
		Node cards = CardsNode();
		
		// new card
		Element card = iDoc.createElement(XML_CARD);
		card.setAttribute(XML_ID, "" + FreeCardId());
		card.setAttribute(XML_STATUS, "lesson");
		cards.appendChild(card);
		
		// Frst Language phrases
		for (int i = 0; i < lngCard.FrstPhraseCount(); i++) {
			Element phrase = LngPhraseToXmlElement(lngCard.GetFrstPhrase(i));
			phrase.setAttribute(XML_LANGUAGE, LanguageFrst());
			card.appendChild(phrase);
		}
		
		// Scnd Language phrases
		for (int i = 0; i < lngCard.ScndPhraseCount(); i++) {
			Element phrase = LngPhraseToXmlElement(lngCard.GetScndPhrase(i));
			phrase.setAttribute(XML_LANGUAGE, LanguageScnd());
			card.appendChild(phrase);
		}
	}
	
	private Element LngPhraseToXmlElement(LngPhrase lngPhrase) {
		Element phrase = iDoc.createElement(XML_PHRASE);
		
		// Value
		Element val = iDoc.createElement(XML_VALUE);
		val.setTextContent(lngPhrase.iValue);
		phrase.appendChild(val);
		
		// Transcription
		if (lngPhrase.iTranscription != null && !lngPhrase.iTranscription.isEmpty()) {
			Element transcr = iDoc.createElement(XML_TRANSCRIPTION);
			transcr.setTextContent(lngPhrase.iTranscription);
			phrase.appendChild(transcr);
		}
		
		// Example
		for (int j = 0; j < lngPhrase.iExamples.size(); j++) {
			Element example = iDoc.createElement(XML_EXAMPLE);
			example.setTextContent(lngPhrase.iExamples.get(j));
			phrase.appendChild(example);
		}
		
		return phrase;
	}
	
	// get all cards, use "ID-Frst-Scnd" format
	public Vector<Vector<String>> GetAllCardsIdFrstScnd() throws XPathExpressionException, LangCardsException {
		Vector<Vector<String>> rowsVect=new Vector<Vector<String>>();
		
		NodeList cardList = CardsList();
		
		String frstLanguageStr = LanguageFrst();
		String scndLanguageStr = LanguageScnd();
		
		for (int i = 0; i < cardList.getLength(); i++) {
			Vector<String> rowVect=new Vector<String>();
			
			Node card = cardList.item(i);
			if (card == null) throw new LangCardsException("xml error: fail to get next \"Card\" node");
			
			rowVect.addElement(CardId(card));
			
			 // get the first "Frst" Language phrase
			NodeList frstPhraseList = FrstPhrasesOfCard(card, frstLanguageStr);
			Node frstPhrase = frstPhraseList.item(0);
			rowVect.addElement(ValueOfXmlPhrase(frstPhrase));
			
			 // get the first "Scnd" Language phrase
			NodeList scndPhraseList = ScndPhrasesOfCard(card, scndLanguageStr);
			Node scndPhrase = scndPhraseList.item(0);
			rowVect.addElement(ValueOfXmlPhrase(scndPhrase));
			

			rowsVect.addElement(rowVect);
		}

		return rowsVect;
	}
	
	public Lesson newLesson() throws XPathExpressionException {
		return new Lesson(this);
	}
	
	public LngCard XmlNodeToLngCard(Node node) throws XPathExpressionException, LangCardsException {
		if (node == null) return null;
		
		LngCard lc = new LngCard();
		
		NodeList frstPhraseList = FrstPhrasesOfCard(node, LanguageFrst());
		
		for (int i = 0; i < frstPhraseList.getLength(); i++) {
			Node frstPhrase = frstPhraseList.item(i);
			lc.AddFrstPhrase(XmlNodeToLngPhrase(frstPhrase));
		}
		
		
		NodeList scndPhraseList = FrstPhrasesOfCard(node, LanguageScnd());
		
		for (int i = 0; i < scndPhraseList.getLength(); i++) {
			Node scndPhrase = scndPhraseList.item(i);
			lc.AddScndPhrase(new LngPhrase(ValueOfXmlPhrase(scndPhrase)));
		}
		
		return lc;
	}
	
	private LngPhrase XmlNodeToLngPhrase(Node phraseNode) throws XPathExpressionException {
		// Value
		LngPhrase phrase = new LngPhrase(ValueOfXmlPhrase(phraseNode));
		
		// Transcription
		String tr = TranscriptionOfXmlPhrase(phraseNode);
		if (tr != null && !tr.isEmpty()) {
			phrase.iTranscription = TranscriptionOfXmlPhrase(phraseNode);
		}
		
		// Example
		NodeList examplesList = ExamplesOfXmlPhrase(phraseNode);
		for (int i = 0; i < examplesList.getLength(); i++) {
			Node example = examplesList.item(i);
			String exmpStr = example.getTextContent();
			
			if (exmpStr != null && !exmpStr.isEmpty()) {
				phrase.iExamples.add(exmpStr);
			}
		}
		return phrase;
	}
	
	private Node CardsNode () throws XPathExpressionException {
		if (iSetCards == null) {
			iSetCards = iXpath.compile(XML_SET + "/" + XML_CARDS);
		}
		
		return (Node)iSetCards.evaluate(iDoc, XPathConstants.NODE);
	}
	
	private NodeList CardsList() throws XPathExpressionException {
		if (iCardsCard == null) {
			iCardsCard = iXpath.compile(XML_SET + "/" + XML_CARDS + "/" + XML_CARD);
		}
		
		return (NodeList) iCardsCard.evaluate(iDoc, XPathConstants.NODESET);
	}
	
	public NodeList LessonCardsList() throws XPathExpressionException {
		if (iLessonCards == null) {
			iLessonCards = iXpath.compile(XML_SET + "/" + XML_CARDS + "/" + XML_CARD + "[@" + XML_STATUS + "='lesson']");
		}
		
		return (NodeList) iLessonCards.evaluate(iDoc, XPathConstants.NODESET);
	}
	
	private String CardId(Node card) throws XPathExpressionException {
		if (iIdExpr == null) {
			iIdExpr = iXpath.compile("@" + XML_ID);
		}
		
		return iIdExpr.evaluate(card);
	}
	
	private NodeList FrstPhrasesOfCard(Node card, String frstLanguage) throws XPathExpressionException, LangCardsException {
		if (iPhraseLanguageFrst == null) {
			iPhraseLanguageFrst = iXpath.compile(String.format(XML_PHRASE + "[@" + XML_LANGUAGE + "='%s']", frstLanguage));
		}
		
		NodeList frstPhraseList = (NodeList)iPhraseLanguageFrst.evaluate(card, XPathConstants.NODESET);
		if (frstPhraseList.getLength() < 1) throw new LangCardsException(String.format("invalid xml structure: no %s Phrases in the %S card", frstLanguage, CardId(card)));
		
		return frstPhraseList;
	}
	
	private NodeList ScndPhrasesOfCard(Node card, String scndLanguage) throws XPathExpressionException, LangCardsException {
		if (iPhraseLanguageScnd == null) {
			iPhraseLanguageScnd = iXpath.compile(String.format(XML_PHRASE + "[@" + XML_LANGUAGE + "='%s']", scndLanguage));
		}
		
		NodeList scndPhraseList = (NodeList)iPhraseLanguageScnd.evaluate(card, XPathConstants.NODESET);
		if (scndPhraseList.getLength() < 1) throw new LangCardsException(String.format("invalid xml structure: no %s Phrases in the %S card", scndLanguage, CardId(card)));
		
		return scndPhraseList;
	}
	
	private String ValueOfXmlPhrase(Node phrase) throws XPathExpressionException {
		if (iValueExpr == null) {
			iValueExpr = iXpath.compile(XML_VALUE);
		}
		
		return iValueExpr.evaluate(phrase);
	}
	
	private String TranscriptionOfXmlPhrase(Node phrase) throws XPathExpressionException {
		if (iTranscriptionExpr == null) {
			iTranscriptionExpr = iXpath.compile(XML_TRANSCRIPTION);
		}
		
		return iTranscriptionExpr.evaluate(phrase);
	}
	
	private NodeList ExamplesOfXmlPhrase(Node phrase) throws XPathExpressionException {
		if (iExampleExpr == null) {
			iExampleExpr = iXpath.compile(XML_EXAMPLE);
		}
		
		return (NodeList)iExampleExpr.evaluate(phrase, XPathConstants.NODESET);
	}
	
	private int FreeCardId() throws XPathExpressionException {
		return CardsCount() + 1; // temporary
	}
	
	public String LanguageFrst() throws XPathExpressionException {
		if (iSettings_Languages_Frst == null) {
			iSettings_Languages_Frst = iXpath.compile(XML_SET + "/" + XML_SETTINGS + "/" + XML_LANGUAGES + "/@" + XML_FRST);
		}
		
		return iSettings_Languages_Frst.evaluate(iDoc);
	}
	
	public String LanguageScnd() throws XPathExpressionException {
		if (iSettings_Languages_Scnd == null) {
			iSettings_Languages_Scnd = iXpath.compile(XML_SET + "/" + XML_SETTINGS + "/" + XML_LANGUAGES + "/@" + XML_SCND);
		}
		
		return iSettings_Languages_Scnd.evaluate(iDoc);
	}
}
