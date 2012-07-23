package cardSet;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import LangCards.LCui;

public class CardSet {
	private Document iDoc;
	
	public CardSet() {
		// new
		iDoc = LCui.parser.newDocument();
		iDoc.createElement("rootTT");
	}
	
	public CardSet(File file) throws SAXException, IOException {
		iDoc = LCui.parser.parse(file);
		iDoc.createElement("rootTT");
	}

	public void f() {
	}
}
