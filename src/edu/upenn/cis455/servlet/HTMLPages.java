package edu.upenn.cis455.servlet;

public class HTMLPages {

	public static String homepage() {
		StringBuilder html = new StringBuilder();

		html.append("<form action =\"/servlet/xpath\" method = \"post\"><h2>XPath Channels</h1> </br>");
		html.append("<input type=\"submit\" value=\"Login\"></br></br>");
		html.append("<input type=\"submit\" value=\"Create account\"></br></br>");
		html.append("<input type=\"submit\" value=\"View channels\"></form>");

		return html.toString();
	}

}
