package lngCard;

import java.util.ArrayList;

import langCardsExeption.LangCardsExeption;
import lngPhrase.LngPhrase;

public class LngCard {
	ArrayList<LngPhrase> iFromPrases;
	ArrayList<LngPhrase> iToPrases;
	
	public LngCard() {
		iFromPrases = new ArrayList<LngPhrase>();
		iToPrases = new ArrayList<LngPhrase>();
	}
	
	public void AddFromPhrase(String val) {
		LngPhrase lngPhrase = new LngPhrase(val);
		iFromPrases.add(lngPhrase);
	}
	
	public void AddToPhrase(String val) {
		LngPhrase lngPhrase = new LngPhrase(val);
		iToPrases.add(lngPhrase);
	}
	
	public int FromPhraseCount() {
		return iFromPrases.size();
	}
	
	public int ToPhraseCount() {
		return iToPrases.size();
	}
	
	public String GetFromPhrase(int i) throws LangCardsExeption {
		if (i >= 0 && i < iFromPrases.size()) {
			LngPhrase lngPhrase = iFromPrases.get(i);
			return lngPhrase.iVal;
		}
		
		throw new LangCardsExeption("GetFromPhrase: out of bounds");
	}
	
	public String GetToPhrase(int i) throws LangCardsExeption {
		if (i < iToPrases.size()) {
			LngPhrase lngPhrase = iToPrases.get(i);
			return lngPhrase.iVal;
		}
		
		throw new LangCardsExeption("GetToPhrase: out of bounds");
	}
}
