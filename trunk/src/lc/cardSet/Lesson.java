package lc.cardSet;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import lc.cardSet.lngCard.LngCard;
import lc.langCardsException.LangCardsException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Lesson {
	CardSet iSet;
	NodeList iCards = null;
	int iPos = 0;
	
	public Lesson(CardSet set) throws XPathExpressionException {
		iSet = set;
		iCards = iSet.LessonCardsList();
	}
	
	public LngCard NextCard() throws XPathExpressionException, LangCardsException {
		Node nextNode = iCards.item(iPos++);
		return iSet.XmlNodeToLngCard(nextNode);
	}
	
	public int CurrentCardPos() {
		return iPos;		
	}

	public void markCorrect(LngCard lngCard) throws XPathExpressionException, LangCardsException, TransformerException {
		iSet.increaseHits(lngCard.id());
	}

	public void markWrong(LngCard lngCard) throws TransformerException, XPathExpressionException, LangCardsException {
		iSet.decreaseHits(lngCard.id());
	}
}
