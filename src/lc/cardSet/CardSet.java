package lc.cardSet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import lc.LCmain;
import lc.utils.LCutils;
import lc.langCardsException.LangCardsException;
import lc.cardSet.lngCard.LngCard;
import lc.cardSet.lngPhrase.LngPhrase;

public class CardSet {
	private Document iDoc;
	private String iName;
	private File iFile = new File("");
	boolean iChanged = false;
	
	XPath iXpath = XPathFactory.newInstance().newXPath();
	Random random = null;
	
	// xml structure elements
	private static final String XML_SET				= "Set";			// <Set name=...>
	private static final String XML_NAME			= "name";			// |
	private static final String XML_CARDS			= "Cards";			// |- <Cards>
	private static final String XML_CARD			= "Card";			// |  |- <Card id=... status=... hits=...>
	private static final String XML_ID				= "id";				// |     |
	private static final String XML_STATUS			= "status";			// |     |
	private static final String XML_HITS			= "hits";			// |     |
	private static final String XML_PHRASE			= "Phrase";			// |     |- <Phrase Language=...> 
	private static final String XML_LANGUAGE		= "Language";		// |        |
	private static final String XML_VALUE			= "Value";			// |        |- <Value>...</>
	private static final String XML_TRANSCRIPTION	= "Transcription";	// |        |- <Transcription>...</>
	private static final String XML_EXAMPLE			= "Example";		// |        |- <Example>...</>
	private static final String XML_SETTINGS		= "Settings";		// |- <Settings>
	private static final String XML_LANGUAGES		= "Languages";		//    |- <Languages Frst=... Scnd=...>
	private static final String XML_FRST			= "Frst";
	private static final String XML_SCND			= "Scnd";

	// XML_STATUS values
	private static final String STATUS_IDLE = "idle";
	private static final String STATUS_LESSON = "lesson";
	private static final String STATUS_LEARNED = "learned";

	// Path Expressions of frequent operations
	XPathExpression iSetCards = null;                // Set/Cards
	XPathExpression iCardsCard = null;               // Set/Cards/Card
	XPathExpression iLessonCards = null;             // //Card[@status='lesson']
	XPathExpression iIdleCards = null;               // //Card[@status='idle']
	XPathExpression iLessonCardsCount = null;        // "count(//Card[@status='lesson'])"
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
		iName = LCutils.string("Unnamed");
		iDoc = LCmain.mainFrame.iParser.newDocument();
		initDoc();
	}
	
	public CardSet(File file) throws SAXException, IOException {
		iFile = file;
		iName = file.getName();
		iDoc = LCmain.mainFrame.iParser.parse(file);
	}
	
	private void initDoc() {
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
		languages.setAttribute(XML_FRST, LCutils.string("LanguageEnglish"));
		languages.setAttribute(XML_SCND, LCutils.string("LanguageRussian"));
		settings.appendChild(languages);
		
		testFilling();
	}
	
	private void testFilling() {
		try {
			LngCard lc = new LngCard();
			lc.addFrstPhrase(new LngPhrase("F111-1@ "));
			lc.addFrstPhrase(new LngPhrase("F111-2@ "));
			lc.addScndPhrase(new LngPhrase("111"));
			addNewCard(lc);
			
			lc = new LngCard();
			LngPhrase lngPhrase = new LngPhrase("F222-1@ ");
			lngPhrase.iTranscription = "[trans F222-1]";
			lngPhrase.iExamples.add("Examp1 F222-1");
			lngPhrase.iExamples.add("Examp2 F222-1");
			lc.addFrstPhrase(lngPhrase);
			
			lngPhrase = new LngPhrase("F222-222@ ");
			lngPhrase.iTranscription = "[trans F222-222]";
			lc.addFrstPhrase(lngPhrase);
			
			lc.addScndPhrase(new LngPhrase("222"));
			addNewCard(lc);
			
			lc = new LngCard();
			lc.addFrstPhrase(new LngPhrase("F3-1@ "));
			lc.addFrstPhrase(new LngPhrase("F3-2@ "));
			lc.addScndPhrase(new LngPhrase("333"));
			
			lngPhrase = new LngPhrase("T333-2@ ");
			lngPhrase.iTranscription = "[trans T333-2]";
			lngPhrase.iExamples.add("Examp1 T333-2");
			lc.addScndPhrase(lngPhrase);
			addNewCard(lc);
			
			lc = new LngCard();
			lc.addFrstPhrase(new LngPhrase("F44@ "));
			lc.addScndPhrase(new LngPhrase("444"));
			addNewCard(lc);
			
			lc = new LngCard();
			lc.addFrstPhrase(new LngPhrase("F5-1@ "));
			lc.addFrstPhrase(new LngPhrase("F5-2@ "));
			
			lngPhrase = new LngPhrase("F5-3@ ");
			lngPhrase.iTranscription = "[trans F5-3]";
			lc.addFrstPhrase(lngPhrase);
			
			lc.addFrstPhrase(new LngPhrase("F5-4@ "));
			lc.addFrstPhrase(new LngPhrase("F5-5@ "));
			lc.addScndPhrase(new LngPhrase("555"));
			addNewCard(lc);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LangCardsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save(String fileName) throws TransformerException {
		iFile = new File(fileName);
		doSave();
	}

	public boolean save() {
		try {
			doSave();
		} catch (TransformerException e) {
			return false;
		}

		return true;
	}

	private void doSave() throws TransformerException {
		setName(iFile.getName());

		//  Create transformer
		Transformer tFormer = TransformerFactory.newInstance().newTransformer();

		//  Output Types (text/xml/html)
		tFormer.setOutputProperty(OutputKeys.METHOD, "xml");

		//  Write the document to a file
		Source source = new DOMSource(iDoc);
		Result result = new StreamResult(iFile);
		tFormer.transform(source, result);

		iChanged = false;
	}
	
	public String name() {
		return iName;
	}
	
	public void setName(String name) {
		if (name != null && !name.isEmpty()) {
			iName = name;
			
			Element root = iDoc.getDocumentElement(); // get root (XML_SET node)
			String nameString = root.getAttribute(XML_NAME);
			if (nameString != null && !nameString.isEmpty()) {
				root.removeAttribute(XML_NAME);
				root.setAttribute(XML_NAME, name);
				iChanged = true;
			}
		}
	}
	
	public int cardsCount() throws XPathExpressionException{
		return  cardsList().getLength();
	}

	public void addNewCard(LngCard lngCard) throws XPathExpressionException, LangCardsException {
		doAddNewCard(lngCard);
		iChanged = true;
	}

	public void saveCard(LngCard lngCard) throws XPathExpressionException, LangCardsException {
		doDeleteCard(lngCard.id());
		doAddNewCard(lngCard);
		iChanged = true;
	}

	public void deleteCard(String cardId) throws XPathExpressionException, LangCardsException {
		doDeleteCard(cardId);
		iChanged = true;
	}

	private void doAddNewCard(LngCard lngCard) throws XPathExpressionException, LangCardsException {
		Node cards = cardsNode();

		// new card
		Element card = iDoc.createElement(XML_CARD);

		String cardId = lngCard.id();
		if (cardId.isEmpty()) cardId = newCardId();
		card.setAttribute(XML_ID, cardId);

		int cardsInLesson = 50; // TODO get from settings
		if (lessonCardsCount() < cardsInLesson) {
			card.setAttribute(XML_STATUS, STATUS_LESSON);
		} else {
			card.setAttribute(XML_STATUS, STATUS_IDLE);
		}

		cards.appendChild(card);

		// Frst Language phrases
		for (int i = 0; i < lngCard.frstPhraseCount(); i++) {
			Element phrase = lngPhraseToXmlElement(lngCard.getFrstPhrase(i));
			phrase.setAttribute(XML_LANGUAGE, languageFrst());
			card.appendChild(phrase);
		}

		// Scnd Language phrases
		for (int i = 0; i < lngCard.scndPhraseCount(); i++) {
			Element phrase = lngPhraseToXmlElement(lngCard.getScndPhrase(i));
			phrase.setAttribute(XML_LANGUAGE, languageScnd());
			card.appendChild(phrase);
		}
	}

	private void doDeleteCard(String cardId) throws XPathExpressionException, LangCardsException {
		Node cards = cardsNode();
		cards.removeChild(cardByID(cardId));
	}

	private Element lngPhraseToXmlElement(LngPhrase lngPhrase) {
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
	public Vector<Vector<String>> getAllCardsIdFrstScnd() throws XPathExpressionException, LangCardsException {
		Vector<Vector<String>> rowsVect=new Vector<Vector<String>>();
		
		NodeList cardList = cardsList();
		
		String frstLanguageStr = languageFrst();
		String scndLanguageStr = languageScnd();
		
		for (int i = 0; i < cardList.getLength(); i++) {
			Vector<String> rowVect=new Vector<String>();
			
			Node card = cardList.item(i);
			if (card == null) throw new LangCardsException("xml error: fail to get next \"Card\" node");
			
			rowVect.addElement(cardId(card));
			
			 // get the first "Frst" Language phrase
			NodeList frstPhraseList = frstPhrasesOfCard(card, frstLanguageStr);
			Node frstPhrase = frstPhraseList.item(0);
			rowVect.addElement(valueOfXmlPhrase(frstPhrase));
			
			 // get the first "Scnd" Language phrase
			NodeList scndPhraseList = scndPhrasesOfCard(card, scndLanguageStr);
			Node scndPhrase = scndPhraseList.item(0);
			rowVect.addElement(valueOfXmlPhrase(scndPhrase));
			

			rowsVect.addElement(rowVect);
		}

		return rowsVect;
	}
	
	public Lesson newLesson() throws XPathExpressionException {
		return new Lesson(this);
	}
	
	public LngCard xmlNodeToLngCard(Node node) throws XPathExpressionException, LangCardsException {
		if (node == null) return null;
		
		LngCard lc = new LngCard(cardId(node));
		
		NodeList frstPhraseList = frstPhrasesOfCard(node, languageFrst());
		
		for (int i = 0; i < frstPhraseList.getLength(); i++) {
			Node frstPhrase = frstPhraseList.item(i);
			lc.addFrstPhrase(xmlNodeToLngPhrase(frstPhrase));
		}
		
		
		NodeList scndPhraseList = scndPhrasesOfCard(node, languageScnd());
		
		for (int i = 0; i < scndPhraseList.getLength(); i++) {
			Node scndPhrase = scndPhraseList.item(i);
			lc.addScndPhrase(xmlNodeToLngPhrase(scndPhrase));
		}
		
		return lc;
	}
	
	private LngPhrase xmlNodeToLngPhrase(Node phraseNode) throws XPathExpressionException {
		// Value
		LngPhrase phrase = new LngPhrase(valueOfXmlPhrase(phraseNode));
		
		// Transcription
		String tr = transcriptionOfXmlPhrase(phraseNode);
		if (tr != null && !tr.isEmpty()) {
			phrase.iTranscription = transcriptionOfXmlPhrase(phraseNode);
		}
		
		// Example
		NodeList examplesList = examplesOfXmlPhrase(phraseNode);
		for (int i = 0; i < examplesList.getLength(); i++) {
			Node example = examplesList.item(i);
			String exmpStr = example.getTextContent();
			
			if (exmpStr != null && !exmpStr.isEmpty()) {
				phrase.iExamples.add(exmpStr);
			}
		}
		return phrase;
	}
	
	private Node cardsNode() throws XPathExpressionException {
		if (iSetCards == null) {
			iSetCards = iXpath.compile(XML_SET + "/" + XML_CARDS);
		}
		
		return (Node)iSetCards.evaluate(iDoc, XPathConstants.NODE);
	}
	
	private NodeList cardsList() throws XPathExpressionException {
		if (iCardsCard == null) {
			iCardsCard = iXpath.compile(XML_SET + "/" + XML_CARDS + "/" + XML_CARD);
		}
		
		return (NodeList) iCardsCard.evaluate(iDoc, XPathConstants.NODESET);
	}

	public int  lessonCardsCount() throws XPathExpressionException {
		if (iLessonCardsCount == null) {
			iLessonCardsCount = iXpath.compile("count(//" + XML_CARD + "[@" + XML_STATUS + "='" + STATUS_LESSON + "'])");
		}

		return (int)(double)(Double) iLessonCardsCount.evaluate(iDoc, XPathConstants.NUMBER);
	}

	public NodeList getLessonCardsList() throws XPathExpressionException {
		int cardsInLesson = 50; // TODO get from settings
		int cardsCount = lessonCardsCount();

		addRandomIdleCardsToLesson(cardsInLesson - cardsCount);

		if (iLessonCards == null) {
			iLessonCards = iXpath.compile("//" + XML_CARD + "[@" + XML_STATUS + "='" + STATUS_LESSON + "']");
		}
		
		return (NodeList) iLessonCards.evaluate(iDoc, XPathConstants.NODESET);
	}

	private void addRandomIdleCardsToLesson(int count) throws XPathExpressionException {
		if (count > 0) {
			if (iIdleCards == null) {
				iIdleCards = iXpath.compile("//" + XML_CARD + "[@" + XML_STATUS + "='" + STATUS_IDLE + "']");
			}
			NodeList idleCardsList = (NodeList) iIdleCards.evaluate(iDoc, XPathConstants.NODESET);

			int idleCount = idleCardsList.getLength();

			if (count >= idleCount) {				// all idles to lesson
				for (int i = 0; i < idleCount; i++) {
					Node cardNode = idleCardsList.item(i);
					addCardToLesson(cardNode);
				}
			} else {								// random subset of idles to lesson
				if (0.5 > (double)count / (double)idleCount) { // Random.nextInt() is more effective than Collections.shuffle()
					if (random == null) {
						random = new Random();
					}

					HashSet<Integer> indexSet = new HashSet<Integer>();

					while (indexSet.size() < count) {
						Integer next = random.nextInt(idleCount);
						indexSet.add(next);
					}

					for (Iterator<Integer> iterator = indexSet.iterator(); iterator.hasNext();) {
						int index = iterator.next();
						Node cardNode = idleCardsList.item(index);
						addCardToLesson(cardNode);
					}
				} else {  // Collections.shuffle() is more effective than Random.nextInt()
					Vector<Integer> vector = new Vector<Integer>(); // vector of all indexes of idle card list;
					for (int i = 0; i < idleCount; i++) {
						vector.add(i);
					}
					Collections.shuffle(vector);

					for (int i = 0; i < count; i++) { // first count elements of vector (count < vector.size())
						int index = vector.elementAt(i);
						Node cardNode = idleCardsList.item(index);
						addCardToLesson(cardNode);
					}
				}
			}
		}
	}

	private void addCardToLesson(Node cardNode) {
		if(cardNode instanceof Element) {
			Element el = (Element) cardNode;
			el.setAttribute(XML_STATUS, STATUS_LESSON);
		}
	}

	private String cardId(Node card) throws XPathExpressionException {
		if (iIdExpr == null) {
			iIdExpr = iXpath.compile("@" + XML_ID);
		}
		
		return iIdExpr.evaluate(card);
	}
	
	private NodeList frstPhrasesOfCard(Node card, String frstLanguage) throws XPathExpressionException, LangCardsException {
		if (iPhraseLanguageFrst == null) {
			iPhraseLanguageFrst = iXpath.compile(String.format(XML_PHRASE + "[@" + XML_LANGUAGE + "='%s']", frstLanguage));
		}
		
		NodeList frstPhraseList = (NodeList)iPhraseLanguageFrst.evaluate(card, XPathConstants.NODESET);
		if (frstPhraseList.getLength() < 1) throw new LangCardsException(String.format("invalid xml structure: no %s Phrases in the %S card", frstLanguage, cardId(card)));
		
		return frstPhraseList;
	}
	
	private NodeList scndPhrasesOfCard(Node card, String scndLanguage) throws XPathExpressionException, LangCardsException {
		if (iPhraseLanguageScnd == null) {
			iPhraseLanguageScnd = iXpath.compile(String.format(XML_PHRASE + "[@" + XML_LANGUAGE + "='%s']", scndLanguage));
		}
		
		NodeList scndPhraseList = (NodeList)iPhraseLanguageScnd.evaluate(card, XPathConstants.NODESET);
		if (scndPhraseList.getLength() < 1) throw new LangCardsException(String.format("invalid xml structure: no %s Phrases in the %S card", scndLanguage, cardId(card)));
		
		return scndPhraseList;
	}
	
	private String valueOfXmlPhrase(Node phrase) throws XPathExpressionException {
		if (iValueExpr == null) {
			iValueExpr = iXpath.compile(XML_VALUE);
		}
		
		return iValueExpr.evaluate(phrase);
	}
	
	private String transcriptionOfXmlPhrase(Node phrase) throws XPathExpressionException {
		if (iTranscriptionExpr == null) {
			iTranscriptionExpr = iXpath.compile(XML_TRANSCRIPTION);
		}
		
		return iTranscriptionExpr.evaluate(phrase);
	}
	
	private NodeList examplesOfXmlPhrase(Node phrase) throws XPathExpressionException {
		if (iExampleExpr == null) {
			iExampleExpr = iXpath.compile(XML_EXAMPLE);
		}
		
		return (NodeList)iExampleExpr.evaluate(phrase, XPathConstants.NODESET);
	}
	
	private String newCardId() throws XPathExpressionException {
		UUID id = UUID.randomUUID();
		return id.toString();
	}

	private Element cardByID(String cardID) throws XPathExpressionException, LangCardsException {
		NodeList nl = (NodeList)iXpath.evaluate(XML_SET + "/" + XML_CARDS + "/" + XML_CARD + "[@" + XML_ID + "='" + cardID + "']", iDoc, XPathConstants.NODESET);

		if (nl.getLength() != 1) throw new LangCardsException("invalid xml file: " + nl.getLength() + " cards with id=" + cardID + " are found");
		Node cardNode = nl.item(0);

		if(cardNode instanceof Element) {
			return (Element)cardNode;
		}

		throw new LangCardsException("invalid xml file: no cards with id=" + cardID + " are found");
	}

	public LngCard getCard(String cardId) throws XPathExpressionException, LangCardsException {
		return xmlNodeToLngCard(cardByID(cardId));
	}

	public String languageFrst() throws XPathExpressionException {
		if (iSettings_Languages_Frst == null) {
			iSettings_Languages_Frst = iXpath.compile(XML_SET + "/" + XML_SETTINGS + "/" + XML_LANGUAGES + "/@" + XML_FRST);
		}
		
		return iSettings_Languages_Frst.evaluate(iDoc);
	}
	
	public String languageScnd() throws XPathExpressionException {
		if (iSettings_Languages_Scnd == null) {
			iSettings_Languages_Scnd = iXpath.compile(XML_SET + "/" + XML_SETTINGS + "/" + XML_LANGUAGES + "/@" + XML_SCND);
		}
		
		return iSettings_Languages_Scnd.evaluate(iDoc);
	}

	public void increaseHits(String cardID) throws XPathExpressionException, LangCardsException, TransformerException {
		Element cardElement = cardByID(cardID);
		String hits = cardElement.getAttribute(XML_HITS);
		int hitsInt = 0;

		if (hits == null || hits.isEmpty()) {
			hitsInt = 1;
			hits = "1";
		} else {
			hitsInt = Integer.parseInt(hits) + 1;
			hits = String.valueOf(hitsInt);
		}

		int hitsThreshold = 10; // TODO get from settings
		if (hitsInt >= hitsThreshold) {
			cardElement.setAttribute(XML_STATUS, STATUS_LEARNED);
		}

		cardElement.setAttribute(XML_HITS, hits);
		doSave();
	}

	public void decreaseHits(String cardID) throws XPathExpressionException, LangCardsException, TransformerException {
		Element cardElement = cardByID(cardID);
		String hits = cardElement.getAttribute(XML_HITS);

		if (hits == null || hits.isEmpty() || Integer.parseInt(hits) == 0) {
			return;
		}

		hits = String.valueOf(Integer.parseInt(hits) - 1);

		cardElement.setAttribute(XML_HITS, hits);
		doSave();
	}

	public boolean isSaved() {
		return iFile.isFile() && !iChanged;
	}

	public boolean unnamed() {
		return !iFile.isFile();
	}
}
