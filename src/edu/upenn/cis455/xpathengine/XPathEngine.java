package edu.upenn.cis455.xpathengine;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.xml.sax.helpers.DefaultHandler;

public interface XPathEngine {


	/**
	 * Sets the XPath expression(s) that are to be evaluated.
	 * @param expressions
	 */
	public void setXPaths(String[] expressions);

	/**
	 * Returns true if the i.th XPath expression given to the last setXPaths() call
	 * was valid, and false otherwise. If setXPaths() has not yet been called, the
	 * return value is undefined. 
	 * @param i
	 * @return
	 */
	public boolean isValid(int i);

	/**
	 * Returns true if the implementation is a SAX rather than DOM parser.
	 * i.e., the caller should call evaluateSAX rather than evaluate.
	 * 
	 * @return
	 */
	public boolean isSAX();

	/**
	 * DOM Parser evaluation.
	 * Takes a DOM root node as its argument, which contains the representation of the 
	 * HTML or XML document. Returns an array of the same length as the 'expressions'
	 * argument to setXPaths(), with the i.th element set to true if the document 
	 * matches the i.th XPath expression, and false otherwise. If setXPaths() has not
	 * yet been called, the return value is undefined.
	 *
	 * @param d Document root node
	 * @return bit vector of matches
	 */
	public boolean[] evaluate(Document d);

	/**
	 * SAX parser evaluation. (Optional extra credit.)
	 * 
	 * Takes a stream as input, as well as a Handler produced by the
	 * XPathEngineFactory.   Returns an array of the same length as the 'expressions'
	 * argument to setXPaths(), with the i.th element set to true if the document 
	 * matches the i.th XPath expression, and false otherwise. If setXPaths() has not
	 * yet been called, the return value is undefined.
	 * 
	 * @param document Document stream
	 * @param handler SAX handler implementation (from factory)
	 * @return bit vector of matches
	 */
	public boolean[] evaluateSAX(InputStream document, DefaultHandler handler);

}
