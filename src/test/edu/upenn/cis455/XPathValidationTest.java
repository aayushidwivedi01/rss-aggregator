package test.edu.upenn.cis455;

/**
 * Testcases for XPathValidity class
 * Tests different XPath cases depending on the Grammar for servlet
 */
import static org.junit.Assert.*;
import junit.framework.TestCase;
import edu.upenn.cis455.xpathengine.XPathEngine;
import edu.upenn.cis455.xpathengine.XPathEngineFactory;

import org.junit.Test;

public class XPathValidationTest extends TestCase {

	// tests whether a valid xpath is correctly identified
	@Test
	public void testValidXPath() {
		String xpathInput = "/this/is/a/valid/xpath";
		String[] xpaths = xpathInput.split(";");
		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
		xPathEngine.setXPaths(xpaths);

		assertTrue("XPath is valid but output is false", xPathEngine.isValid(0));
	}

	// tests whether an invalid xpath is correctly identified
	@Test
	public void testInValidXPath() {
		String xpathInput = "/this/is/an//invalid/xpath]]";
		String[] xpaths = xpathInput.split(";");
		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
		xPathEngine.setXPaths(xpaths);

		assertFalse("XPath is invalid but output is true",
				xPathEngine.isValid(0));
	}

	// tests whether a valid xpath containing contains(text(), "..") filter is
	// correctly identified
	@Test
	public void testXPathContainsValid() {
		String xpathInput = "/note/from[contains (text(), \"Jen\")]";
		String[] xpaths = xpathInput.split(";");
		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
		xPathEngine.setXPaths(xpaths);

		assertTrue("XPath is valid but output is false", xPathEngine.isValid(0));
	}

	// tests whether a path containing invalid contains(text(), "..") filter is
	// correctly identified
	@Test
	public void testXPathContainsInValid() {
		String xpathInput = "/note/from[contains (text(dfdfd), \"Jen\")]";
		String[] xpaths = xpathInput.split(";");
		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
		xPathEngine.setXPaths(xpaths);

		assertFalse("XPath is valid but output is false",
				xPathEngine.isValid(0));
	}

	// tests whether a valid xpath containing text = ".." filter is correctly
	// identified
	@Test
	public void testXPathTextValid() {
		String xpathInput = "/note/from[text() = \"Valid\"]";
		String[] xpaths = xpathInput.split(";");
		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
		xPathEngine.setXPaths(xpaths);

		assertTrue("XPath is invalid but output is true",
				xPathEngine.isValid(0));
	}

	// tests whether a valid xpath containing @name = ".." filter is correctly
	// identified
	@Test
	public void testXPathAttributeValid() {
		String xpathInput = "/note/from[ @attname = \"Jen\"]";
		String[] xpaths = xpathInput.split(";");
		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
		xPathEngine.setXPaths(xpaths);

		assertTrue("XPath is valid but output is false", xPathEngine.isValid(0));
	}

	// tests whether apath containing invalid @name = ".." filter is correctly
	// identified
	@Test
	public void testXPathAttributeInValid() {
		String xpathInput = "/note/from[ @attname = Jen]";
		String[] xpaths = xpathInput.split(";");
		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
		xPathEngine.setXPaths(xpaths);

		assertFalse("XPath is invalid but output is true",
				xPathEngine.isValid(0));
	}

	// tests whether a valid xpath with nested filter is correctly identified

	@Test
	public void testNestedXPathValid() {
		String xpathInput = "/note/from[to/from/when]";
		String[] xpaths = xpathInput.split(";");
		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
		xPathEngine.setXPaths(xpaths);

		assertTrue("XPath is invalid but output is true",
				xPathEngine.isValid(0));
	}

}
