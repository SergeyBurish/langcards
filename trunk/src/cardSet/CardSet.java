package cardSet;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import LangCards.LCui;

public class CardSet {
	private Document iDoc;
	private String iName;
	
	public CardSet() {
		// new
		iName = "New set";
		iDoc = LCui.mainFrame.parser.newDocument();
		iDoc.createElement("rootTT");
	}
	
	public CardSet(File file) throws SAXException, IOException {
		iName = file.getName();
		iDoc = LCui.mainFrame.parser.parse(file);
		iDoc.createElement("rootTT");
	}

	public void f() {
	}

	public String Name() {
		return iName;
	}
}
