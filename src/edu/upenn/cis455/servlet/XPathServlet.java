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

import edu.upenn.cis455.xpathengine.XPathEngine;
import edu.upenn.cis455.xpathengine.XPathEngineFactory;


@SuppressWarnings("serial")
public class XPathServlet extends HttpServlet {
	//static final Logger logger = Logger.getLogger(HttpServer.class);

	
	/* TODO: Implement user interface for XPath engine here */
	
	/* You may want to override one or both of the following methods */

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		/* TODO: Implement user interface for XPath engine here */
		Map<String, String> paramMap= request.getParameterMap();
		
		System.out.println(paramMap.toString());
		String xpath = request.getParameter("xpath");
		String docURL= request.getParameter("url");
		
		String[] xpaths = xpath.split(";");
		
		XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
		xPathEngine.setXPaths(xpaths);
		
		HttpClient httpClient = new HttpClient();
		Document doc = httpClient.request(docURL);
		
		
		
//		URL sourceUrl = new URL(docURL);
//		String sourceFileName = sourceUrl.getFile();
//		File sourceFile = new File("/usr/share/jetty/webapps/"+sourceFileName);
//		sourceFile.mkdirs();
//		Path destinationPath = new File("/usr/share/jetty/webapps/"+sourceFileName).toPath();
//		Files.copy(sourceUrl.openStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
		
		StringBuilder html = new StringBuilder();
		html.append("<html><body>" );
		
		if (doc == null){
			html.append("Error while parsing HTML/XML to DOM");
			
		}
		else {
			boolean[] matches = xPathEngine.evaluate(doc);
			for (int i = 0; i < xpaths.length; i++ ){
				if (matches[i]){
					html.append("Success:" + xpaths[i]+ "</br>");
				}
				else{
					html.append("Failure:" + xpaths[i] + "</br>");
				}
			}
		}
		
		
		html.append("</body></html>");
		response.setContentType("text/html");
		response.setContentLength(html.length());
	    PrintWriter out = response.getWriter();
	    out.write(html.toString());
	    response.flushBuffer();
		
		
		
		
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		/* TODO: Implement user interface for XPath engine here */
		String html = "<form action =\"/servlet/xpath\" method= \"post\">"
				+ "Name: Aayushi Dwived</br>Login: aayushi</br>XPath</br>"
				+ "Enter \";\" separated xpaths:</br>"
				+ "<input type=\"text\" name=\"xpath\"></br>HTML/XML URL</br>"
				+ "<input type=\"text\" name=\"url\"></br>"
				+ "<input type=\"submit\" value=\"Submit\"></form>";

        response.setContentType("text/html");
        response.setContentLength(html.length());

        PrintWriter out = response.getWriter();
        out.write(html);
        response.flushBuffer();

		
	}

}









