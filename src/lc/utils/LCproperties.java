package lc.utils;

import lc.LCmain;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by sergey.burish on 16.10.2015.
 */
public class LCproperties {

	private static LCproperties iInstance;

	private static final String XML_PROPERTIES = "properties";
	private static final String XML_COMMENT = "comment";
	private static final String XML_ENTRY = "entry";
	private static final String XML_KEY = "key";

	private static final String XML_TEXT_VALUE = "text()";

	private Document iDoc;
	private File iFile;
	private Element iRoot;
	private XPath iXpath = XPathFactory.newInstance().newXPath();
	private XPathExpression iTextExpr;

	public static LCproperties getInstance() {
		if (iInstance == null) {
			iInstance = new LCproperties();
		}
		return iInstance;
	}

	private LCproperties() {
		initDoc();
	}

	public void clear() {
		initDoc();
	}

	private void initDoc() {
		iDoc = LCmain.mainFrame.iParser.newDocument();
		iRoot = iDoc.createElement(XML_PROPERTIES);
		iDoc.appendChild(iRoot);
	}

	public void setComment(String comment) {
		Element commentElement = iDoc.createElement(XML_COMMENT);
		commentElement.setTextContent(comment);
		iRoot.appendChild(commentElement);
	}

	public void setProperty(String key, String value) {
		Element entry = iDoc.createElement(XML_ENTRY);
		entry.setAttribute(XML_KEY, key);
		entry.setTextContent(value);
		iRoot.appendChild(entry);
	}

	public String getProperty(String key, String defaultValue) {
		String entryValue = defaultValue;
		try {
			NodeList nl = (NodeList)iXpath.evaluate(XML_PROPERTIES + "/" + XML_ENTRY + "[@" + XML_KEY + "='" + key + "']", iDoc, XPathConstants.NODESET);
			if (nl.getLength() > 0) {
				Node entryNode = nl.item(0); // take the first node with sought-for key; all duplicates are ignored
				if (iTextExpr == null) {
					iTextExpr = iXpath.compile(XML_TEXT_VALUE);
				}
				entryValue = iTextExpr.evaluate(entryNode);
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return entryValue;
	}

	public void store(String fileName) throws TransformerException {
		if (iFile == null || iFile.getName() != fileName) {
			iFile = new File(fileName);
		}
		doSave();
	}

	private void doSave() throws TransformerException {
		//  Create transformer
		Transformer tFormer = TransformerFactory.newInstance().newTransformer();

		//  Output Types (text/xml/html)
		tFormer.setOutputProperty(OutputKeys.METHOD, "xml");

		//  Write the document to a file
		Source source = new DOMSource(iDoc);
		Result result = new StreamResult(iFile);
		tFormer.transform(source, result);
	}

	public void load(String fileName) throws IOException, SAXException {
		if (iFile == null || iFile.getName() != fileName) {
			iFile = new File(fileName);
		}
		iDoc = LCmain.mainFrame.iParser.parse(iFile);
	}
}
