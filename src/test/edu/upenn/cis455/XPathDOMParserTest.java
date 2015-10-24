package test.edu.upenn.cis455;

/**
 * Test cases to test DOMParser class
 **/
import static org.junit.Assert.*;

import java.net.URL;

import junit.framework.TestCase;
import edu.upenn.cis455.servlet.HttpClient;
import edu.upenn.cis455.xpathengine.XPathEngine;
import edu.upenn.cis455.xpathengine.XPathEngineFactory;

import org.junit.Test;
import org.w3c.dom.Document;

public class XPathDOMParserTest extends TestCase {

	// tests whether a valid and existing path is correctly
	// identified
	@Test
	public void testXPathInDoc() {
		String xpathInput = "/note/to;/note/from[text()=\"Jani\"]";
		String[] xpaths = xpathInput.split(";");

		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
		xPathEngine.setXPaths(xpaths);

		String docURL = "http://www.w3schools.com/xml/note.xml";
		HttpClient httpClient = new HttpClient();
		Document doc = httpClient.request(docURL);

		boolean[] matchesActual = xPathEngine.evaluate(doc);

		assertTrue("XPath is valid but output is false", matchesActual[0]);
		assertTrue("XPath is valid but output is false", matchesActual[1]);
	}

	// tests whether an valid but non-existing path is correctly
	// identified
	@Test
	public void testXPathNotInDoc() {
		String xpathInput = "/note/to/from;/note/from";
		String[] xpaths = xpathInput.split(";");

		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
		xPathEngine.setXPaths(xpaths);

		String docURL = "http://www.w3schools.com/xml/note.xml";
		HttpClient httpClient = new HttpClient();
		Document doc = httpClient.request(docURL);

		boolean[] matchesActual = xPathEngine.evaluate(doc);

		assertFalse("XPath is invalid but output is true", matchesActual[0]);
	}

}
