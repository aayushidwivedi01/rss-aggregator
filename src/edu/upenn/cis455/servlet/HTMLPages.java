package edu.upenn.cis455.servlet;

import java.util.ArrayList;

import edu.upenn.cis455.crawler.DataHandler;

public class HTMLPages {

	public static String homepage() {
		StringBuilder html = new StringBuilder();

		html.append("<form action =\"/servlet/xpath/login\" method = \"get\"><h2>XPath Channels</h1> </br>");
		html.append("<input type=\"submit\" value=\"Login\"></form></br></br>");
		
		html.append("<form action =\"/servlet/xpath/createaccount\" method = \"get\">");
		html.append("<input type=\"submit\" value=\"Create account\"></form></br></br>");
		
		html.append("<form action =\"/servlet/xpath/viewchannels\" method = \"get\">");
		html.append("<input type=\"submit\" value=\"View channels\"></form>");

		return html.toString();
	}
	
	public static String loginPage() {
		StringBuilder html = new StringBuilder();

		html.append("<form action =\"/servlet/xpath/validate\" method = \"post\"><h2>Login Page</h1> </br>Username:");
		html.append("<input type=\"text\" name=\"Username:\"></br>Passwod:");
		html.append("<input type=\"password\" name=\"Password:\"></br>");
		html.append("<input type=\"submit\" value=\"Login\"></form>");
		

		return html.toString();
	}
	
	public static String createAccountPage() {
		StringBuilder html = new StringBuilder();

		html.append("<form action =\"/servlet/xpath/verifyNewAccount\" "
				+ "method = \"post\"><h2>Create New Account</h1> </br>Username:");
		html.append("<input type=\"text\" name=\"Username:\"></br>Password:");
		html.append("<input type=\"password\" name=\"Password:\"></br> XPaths (separat using ;)");
		html.append("<input type=\"submit\" value=\"Create Account\"></form>");
		

		return html.toString();
	}
	

	public static String viewChannelsPage(String dbDir) {
		StringBuilder html = new StringBuilder();
		DataHandler dataHandler = new DataHandler();
		ArrayList<String> allChannels =  dataHandler.getAllChannels(dbDir);
		html.append("<html><h1> All Channels</h1>");
		for(String channel : allChannels){
			html.append("<body>"+channel+"</br>");
		}
		html.append("</body></html>");
		
	

		return html.toString();
	}

	
	
}
