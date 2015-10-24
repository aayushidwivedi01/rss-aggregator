package edu.upenn.cis455.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;

import edu.upenn.cis455.crawler.HttpClient;
import edu.upenn.cis455.xpathengine.XPathEngine;
import edu.upenn.cis455.xpathengine.XPathEngineFactory;

@SuppressWarnings("serial")
public class XPathServlet extends HttpServlet {

	/**
	 * implements doPost method for the servlet Uses HttpClient to fetch the
	 * document from the given URL
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		

//		String xpath = request.getParameter("xpath");
//		String docURL = request.getParameter("url");
//
//		String[] xpaths = xpath.split(";");
//
//		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
//		xPathEngine.setXPaths(xpaths);
//
//		HttpClient httpClient = new HttpClient();
//		Document doc = httpClient.request(docURL);
//
//		StringBuilder html = new StringBuilder();
//		html.append("<html><body>");
//
//		if (doc == null) {
//			html.append("Error while parsing HTML/XML to DOM");
//
//		} else {
//			boolean[] matches = xPathEngine.evaluate(doc);
//			for (int i = 0; i < xpaths.length; i++) {
//				if (matches[i]) {
//					html.append("Success:" + xpaths[i] + "</br>");
//				} else {
//					html.append("Failure:" + xpaths[i] + "</br>");
//				}
//			}
//		}
//
//		html.append("</body></html>");
//		response.setContentType("text/html");
//
//		PrintWriter out = response.getWriter();
//		out.println(html.toString());
//		response.flushBuffer();
		
		String pathInfo = request.getPathInfo();
		
		if (pathInfo.equals("verifyNewAccount")){
			
		}

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		/* TODO: Implement user interface for XPath engine here */
		// String html =
		// "<form action =\"/servlet/xpath\" method= \"post\"> <h2>XPath Matcher</h2>"
		// +
		// "Name: Aayushi Dwivedi</br>Login: aayushi</br></br><b>Enter XPath</b></br>"
		// + "Use \";\" to separate multiple XPaths:</br>"
		// +
		// "<input type=\"text\" name=\"xpath\"></br></br><b>Enter HTML/XML URL</b></br>"
		// + "<input type=\"text\" name=\"url\"></br>"
		// + "<input type=\"submit\" value=\"Submit\"></form>";

		String html = null;
		String pathInfo = request.getPathInfo();
		
		if (pathInfo == null || pathInfo.equals("/")){
			html = HTMLPages.homepage();
		}
		
		else if (pathInfo.equals("/login")){
			html = HTMLPages.loginPage();
		}
		
		else if (pathInfo.equals("/createaccount")){
			html = HTMLPages.createAccountPage();
		}
		else if (pathInfo.equals("/viewchannels")){
			String dbDir = getServletContext().getInitParameter("BDBstore");
			System.out.println("DB: " + dbDir);
			html = HTMLPages.viewChannelsPage(dbDir);
		}

		response.setContentType("text/html");
		response.setContentLength(html.length());

		PrintWriter out = response.getWriter();
		out.write(html);
		response.flushBuffer();

	}

}
