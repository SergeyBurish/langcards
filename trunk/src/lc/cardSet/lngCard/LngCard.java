package lc.cardSet.lngCard;

import java.util.ArrayList;

import lc.langCardsException.LangCardsException;
import lc.cardSet.lngPhrase.LngPhrase;

public class LngCard {
	ArrayList<LngPhrase> iFrstPrases;
	ArrayList<LngPhrase> iScndPrases;
	
	public LngCard() {
		iFrstPrases = new ArrayList<LngPhrase>();
		iScndPrases = new ArrayList<LngPhrase>();
	}
	
	public void AddFrstPhrase(LngPhrase lngPhrase) {
		iFrstPrases.add(lngPhrase);
	}
	
	public void AddScndPhrase(LngPhrase lngPhrase) {
		iScndPrases.add(lngPhrase);
	}
	
	public int FrstPhraseCount() {
		return iFrstPrases.size();
	}
	
	public int ScndPhraseCount() {
		return iScndPrases.size();
	}
	
	public LngPhrase GetFrstPhrase(int i) throws LangCardsException {
		if (i >= 0 && i < iFrstPrases.size()) {
			return iFrstPrases.get(i);
		}
		
		throw new LangCardsException("GetFrstPhrase: out of bounds");
	}
	
	public LngPhrase GetScndPhrase(int i) throws LangCardsException {
		if (i < iScndPrases.size()) {
			return iScndPrases.get(i);
		}
		
		throw new LangCardsException("GetScndPhrase: out of bounds");
	}
}
