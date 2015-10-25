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

import edu.upenn.cis455.crawler.DataHandler;
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
		String html =null;
		if (pathInfo.equals("/verifyNewAccount")){
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String dbDir = getServletContext().getInitParameter("BDBstore");
			DataHandler dataHandler = new DataHandler();
			String exists = dataHandler.getUsername(dbDir,username);
			if (exists == null){
				dataHandler.storeUserData(dbDir, username, password);
				html = HTMLPages.loginPage();
			}
			else {
				html = HTMLPages.userNameExistsPage();
			}
			
		}
		
		else if (pathInfo.equals("/validate")){
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String dbDir = getServletContext().getInitParameter("BDBstore");
			DataHandler dataHandler = new DataHandler();
			if (dataHandler.verifyPassword(dbDir, username, password)){
				html = HTMLPages.loginSuccessPage(dbDir);
				HttpSession session = request.getSession(true);
				session.setAttribute("username", username);
			}
			else {
				html = HTMLPages.loginFailurePage();
			}
		}
		else if (pathInfo.equals("/verifyNewChannel")){
			String channelName = request.getParameter("channelname");
			String xpaths = request.getParameter("xpaths");
			HttpSession session = request.getSession(false);
			if (session == null || session.getAttribute("username") == null){
				//not logged in
				html = HTMLPages.needToLoginPage();
			}
			else {
				//create channel
				String username = (String) session.getAttribute("username");
				String dbDir = getServletContext().getInitParameter("BDBstore");
				DataHandler dataHandler = new DataHandler();
				boolean added = dataHandler.addChannel(dbDir, channelName, xpaths, username);
				if (added){
					
					html = HTMLPages.loginSuccessPage(dbDir);
				}
				else {
					//return duplicate channel name
					html = HTMLPages.channelExistsPage();
				}
				
			}
				
		}
		
		else if (pathInfo.equals("/channeldelete")){
			String channelName = request.getParameter("channel");
			HttpSession session = request.getSession(false);
			if (session == null || session.getAttribute("username") == null){
				//not logged in
				html = HTMLPages.needToLoginPage();
			}
			else {
				String username = (String) session.getAttribute("username");
				String dbDir = getServletContext().getInitParameter("BDBstore");
				DataHandler dataHandler = new DataHandler();
				boolean deleted = dataHandler.deleteChannel(dbDir, channelName, username);
				if (deleted)
					html = HTMLPages.loginSuccessPage(dbDir);
				else 
					html = HTMLPages.deleteChannelPage();
			}
			
		}
		
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		out.println(html);
		response.flushBuffer();

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String html = null;
		String pathInfo = request.getPathInfo();
		
		if (pathInfo == null || pathInfo.equals("/")){
			html = HTMLPages.homepage();
		}
		
		else if (pathInfo.equals("/login")){
			html = HTMLPages.loginPage();
		}
		else if (pathInfo.equals("/validate")){
			//verify username password
		}
		
		else if (pathInfo.equals("/createaccount")){
			html = HTMLPages.createAccountPage();
		}
		
		else if (pathInfo.equals("/viewchannels")){
			String dbDir = getServletContext().getInitParameter("BDBstore");
			//System.out.println("DB: " + dbDir);
			html = HTMLPages.viewChannelsPage(dbDir);
		}
		else if (pathInfo.equals("/addchannel")){
			html = HTMLPages.addChannelPage();
		}
		else if (pathInfo.equals("/channelcontent")){
			String channel = request.getParameter("channel");
			String dbDir = getServletContext().getInitParameter("BDBstore");
			html = HTMLPages.displayChannelPage(dbDir, channel);
			
		}
		

		response.setContentType("text/html");
		response.setContentLength(html.length());

		PrintWriter out = response.getWriter();
		out.write(html);
		response.flushBuffer();

	}

}
