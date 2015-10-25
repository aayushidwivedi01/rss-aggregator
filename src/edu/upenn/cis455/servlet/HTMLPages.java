package edu.upenn.cis455.servlet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.upenn.cis455.crawler.DataHandler;
import edu.upenn.cis455.crawler.info.DocumentInfo;

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
		html.append("<input type=\"text\" name=\"username\"></br>Password:");
		html.append("<input type=\"password\" name=\"password\"></br>");
		html.append("<input type=\"submit\" value=\"Login\"></form>");
		return html.toString();
	}
	
	public static String createAccountPage() {
		StringBuilder html = new StringBuilder();

		html.append("<form action =\"/servlet/xpath/verifyNewAccount\" "
				+ "method = \"post\"><h2>Create New Account</h1> </br>Username:");
		html.append("<input type=\"text\" name=\"username\"></br>Password:");
		html.append("<input type=\"password\" name=\"password\"></br> ");
		html.append("<input type=\"submit\" value=\"Create Account\"></form>");
		

		return html.toString();
	}
	
	public static String userNameExistsPage() {
		StringBuilder html = new StringBuilder();

		html.append("<form action =\"/servlet/xpath/createaccount\" "
				+ "method = \"get\"><h2>Username already exists.</h1> </br>");
				html.append("<input type=\"submit\" value=\"Try Again\"></form>");
		html.append("<form action =\"/servlet/xpath/\" "
						+ "method = \"get\"> </br>");
						html.append("<input type=\"submit\" value=\"Home\"></form>");

		return html.toString();
	}
	
	public static String loginSuccessPage(String dir) {
		StringBuilder html = new StringBuilder();
		
		html.append("<form action =\"/servlet/xpath/addchannel\""
				+ " method = \"get\"><input type=\"submit\" value=\""
				+ "Add Channel\"></form></br>");
		
		html.append("<table  style=\"width:100%>\"");
		ArrayList<String>channelNames = new DataHandler().getAllChannelNames(dir);
		
		for (String channel : channelNames){
			html.append("<tr>");
			html.append("<td>" + channel + "</td>");
			html.append("<td>" + "<form action =\"/servlet/xpath/channelcontent\""
					+ " method = \"get\" ><input type=\"hidden\" name = \"channel\" value=\""+channel+"\">"
							+ "<input type=\"submit\" value=\""
					+ "View\"></form></br>"+ "</td>");
			html.append("<td>" + "<form action =\"/servlet/xpath/channeldelete\""
					+ " method = \"post\"><input type=\"hidden\" name=\"channel\" value=\""+channel+"\">"
					+ "<input type=\"submit\" value=\""
					+ "Delete\"></form></br>"+ "</td>");
			html.append("</tr>");
		}
		html.append("</table>");

		return html.toString();
	}
	
	public static String loginFailurePage(){
		StringBuilder html = new StringBuilder();

		html.append("<form action =\"/servlet/xpath/login\" "
				+ "method = \"get\"><h2>Username or Password Incorrect</h1> </br>");
				html.append("<input type=\"submit\" value=\"Try Again\"></form></br>");
		html.append("<form action =\"/servlet/xpath/\" "
						+ "method = \"get\"> </br>");
						html.append("<input type=\"submit\" value=\"Home\"></form>");
		

		return html.toString();
	}
	

	public static String viewChannelsPage(String dbDir) {
		StringBuilder html = new StringBuilder();
		
		
		html.append("<table  style=\"width:100%>\"");
		ArrayList<String>channelNames = new DataHandler().getAllChannelNames(dbDir);
		
		for (String channel : channelNames){
			html.append("<tr>");
			html.append("<td>" + channel + "</td>");
			html.append("<td>" + "<form action =\"/servlet/xpath/channelcontent\""
					+ " method = \"get\" ><input type=\"hidden\" name = \"channel\" value=\""+channel+"\">"
							+ "<input type=\"submit\" value=\""
					+ "View\"></form></br>"+ "</td>");
			html.append("<td>" + "<form action =\"/servlet/xpath/channeldelete\""
					+ " method = \"post\"><input type=\"hidden\" name=\"channel\" value=\""+channel+"\">"
					+ "<input type=\"submit\" value=\""
					+ "Delete\"></form></br>"+ "</td>");
			html.append("</tr>");
		}
		html.append("</table>");

	

		return html.toString();
	}
	
	
	
	
	public static String addChannelPage(){
		StringBuilder html = new StringBuilder();

		html.append("<form action =\"/servlet/xpath/verifyNewChannel\" "
				+ "method = \"post\"><h2>Create New Account</h1> </br>Channelname:");
		html.append("<input type=\"text\" name=\"channelname\"></br>XPaths (separate using ;):");
		html.append("<input type=\"text\" name=\"xpaths\"></br> ");
		html.append("<input type=\"submit\" value=\"Create Channel\"></form>");
		
		return html.toString();
		
	}

	public static String channelExistsPage(){
		StringBuilder html = new StringBuilder();

		html.append("<form action =\"/servlet/xpath/addchannel\" "
				+ "method = \"get\"><h2>Channel Already Exists</h1> </br>");
				html.append("<input type=\"submit\" value=\"Try Again\"></form></br>");
		html.append("<form action =\"/servlet/xpath/\" "
						+ "method = \"get\"> </br>");
						html.append("<input type=\"submit\" value=\"Home\"></form>");
		

		return html.toString();
	}
	
	public static String needToLoginPage(){
		StringBuilder html = new StringBuilder();

		html.append("<form action =\"/servlet/xpath/login\" "
				+ "method = \"get\"><h2>You are not logged in</h1> </br>");
				html.append("<input type=\"submit\" value=\"Login\"></form></br>");


		return html.toString();
	}
	
	public static String displayChannelPage(String dir, String channel){
		StringBuilder html = new StringBuilder();		
		html.append("<html><h1>Channel Content</h1>");
		ArrayList<DocumentInfo>docs = new DataHandler().getChannelContent(dir, channel);
		if (docs.isEmpty()){
			html.append("Nothing to display! Stay tuned..");
		}
		else{
			for(DocumentInfo doc : docs){
				String date = doc.getLastCrawled();
				//date = parseDate(date);
				
				String url = doc.getUrl();
				String body = doc.getBody();
				body.replace("<", "&lt;");
				body.replace(">", "&gt;");
				html.append("<p> Crawled on: "+ getDate(date) + "T"+getTime(date)+ "</br>");
				html.append("Location: " + url);
				html.append("<div style = \"color:#0000FF\">"+body+"</div></p>");
				
				
				
			}
		}
		html.append("</html>");
		return html.toString();
	}
	
	
	public static String deleteChannelPage(){
		StringBuilder html = new StringBuilder();		
		html.append("<html>Sorry, you cannot delete this channel!<html>");
		return html.toString();
	}
	
	public static String getDate(String str){
//		Sat, 24 Oct 2015 20:46:47 GMT
//		EE, DD mm yyyy HH:mm:ss z
		String newFormat = "YYYY-MM-DD";
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		try {
			Date date = sdf.parse(str.trim());
			sdf.applyPattern(newFormat);
			return sdf.format(date);

		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getTime(String str){
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		String newFormat = "hh:mm:ss";
		try {

			Date date = sdf.parse(str.trim());
			sdf.applyPattern(newFormat);
			return sdf.format(date);

		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

	
	

