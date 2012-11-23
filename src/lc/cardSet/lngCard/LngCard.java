package lc.cardSet.lngCard;

import java.util.ArrayList;

import lc.langCardsExeption.LangCardsExeption;
import lc.cardSet.lngPhrase.LngPhrase;

public class LngCard {
	ArrayList<LngPhrase> iFromPrases;
	ArrayList<LngPhrase> iToPrases;
	
	public LngCard() {
		iFromPrases = new ArrayList<LngPhrase>();
		iToPrases = new ArrayList<LngPhrase>();
	}
	
	public void AddFromPhrase(LngPhrase lngPhrase) {
		iFromPrases.add(lngPhrase);
	}
	
	public void AddToPhrase(LngPhrase lngPhrase) {
		iToPrases.add(lngPhrase);
	}
	
	public int FromPhraseCount() {
		return iFromPrases.size();
	}
	
	public int ToPhraseCount() {
		return iToPrases.size();
	}
	
	public LngPhrase GetFromPhrase(int i) throws LangCardsExeption {
		if (i >= 0 && i < iFromPrases.size()) {
			return iFromPrases.get(i);
		}
		
		throw new LangCardsExeption("GetFromPhrase: out of bounds");
	}
	
	public LngPhrase GetToPhrase(int i) throws LangCardsExeption {
		if (i < iToPrases.size()) {
			return iToPrases.get(i);
		}
		
		throw new LangCardsExeption("GetToPhrase: out of bounds");
	}
}
