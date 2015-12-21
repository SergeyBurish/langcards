package lc.cardSet.lngCard;

import java.util.ArrayList;

import lc.langCardsException.LangCardsException;
import lc.cardSet.lngPhrase.LngPhrase;

public class LngCard {
	String id = "";
	ArrayList<LngPhrase> iFrstPrases;
	ArrayList<LngPhrase> iScndPrases;

	public LngCard() {
		this("");
	}
	
	public LngCard(String id) {
		this.id = id;
		iFrstPrases = new ArrayList<LngPhrase>();
		iScndPrases = new ArrayList<LngPhrase>();
	}
	
	public void addFrstPhrase(LngPhrase lngPhrase) {
		iFrstPrases.add(lngPhrase);
	}
	
	public void addScndPhrase(LngPhrase lngPhrase) {
		iScndPrases.add(lngPhrase);
	}
	
	public int frstPhraseCount() {
		return iFrstPrases.size();
	}
	
	public int scndPhraseCount() {
		return iScndPrases.size();
	}
	
	public LngPhrase getFrstPhrase(int i) throws LangCardsException {
		if (i >= 0 && i < iFrstPrases.size()) {
			return iFrstPrases.get(i);
		}
		
		throw new LangCardsException("getFrstPhrase: out of bounds");
	}
	
	public LngPhrase getScndPhrase(int i) throws LangCardsException {
		if (i < iScndPrases.size()) {
			return iScndPrases.get(i);
		}
		
		throw new LangCardsException("getScndPhrase: out of bounds");
	}

	public String id() {
		return id;
	}

	public void clear() {
		iFrstPrases.clear();
		iScndPrases.clear();
	}
}
