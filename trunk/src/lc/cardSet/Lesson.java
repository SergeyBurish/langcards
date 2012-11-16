package lc.cardSet;

import javax.xml.xpath.XPathExpressionException;

import lc.cardSet.lngCard.LngCard;
import lc.langCardsExeption.LangCardsExeption;

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
	
	public LngCard NextCard() throws XPathExpressionException, LangCardsExeption {
		Node nextNode = iCards.item(iPos++);
		LngCard lc = iSet.NodeToCard(nextNode);
		return lc;
	}
}
