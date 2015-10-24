package edu.upenn.cis455.xpathengine;

import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.xml.sax.helpers.DefaultHandler;

public class XPathEngineImpl implements XPathEngine {

	ArrayList<String> xpaths = new ArrayList<>();
	XPathValidity xpathValidity;

	public XPathEngineImpl() {
		// Do NOT add arguments to the constructor!!
	}

	public void setXPaths(String[] s) {
		/* Store the XPath expressions that are given to this method */
		for (String xpath : s) {
			xpaths.add(xpath);
		}

	}

	public boolean isValid(int i) {
		/* Check which of the XPath expressions are valid */
		xpathValidity = new XPathValidity(xpaths.get(i));
		xpathValidity.xpath();
		return xpathValidity.valid;
	}

	public boolean[] evaluate(Document d) {
		/* Check whether the document matches the XPath expressions */
		boolean[] found = new boolean[xpaths.size()];

		for (int i = 0; i < xpaths.size(); i++) {
			if (isValid(i)) {

				DomParser domParser = new DomParser();
				found[i] = domParser.parse(xpathValidity.allNodes,
						d.getDocumentElement());

			} else {
				found[i] = false;
			}

		}

		return found;
	}

	@Override
	public boolean isSAX() {
		return false;
	}

	@Override
	public boolean[] evaluateSAX(InputStream document, DefaultHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}

}
