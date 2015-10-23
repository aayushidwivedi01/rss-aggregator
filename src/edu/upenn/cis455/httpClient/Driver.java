package edu.upenn.cis455.httpClient;

import java.util.List;
import java.util.Map;

import edu.upenn.cis455.crawler.info.RobotsTxtParse;
import edu.upenn.cis455.crawler.info.RobotsTxtInfo;

public class Driver {

	public static void main(String args[]){
		String url = "https://dbappserv.cis.upenn.edu/crawltest.html";
		
		HttpClient httpClient = new HttpClient();
		httpClient.setIsRobot(true);
		HttpResponse resp = httpClient.getResponse(httpClient.getRobotsTxtURL(url), "GET", "cis455crawler");
		Map<String, List<String>> map = resp.headers;
		String body = resp.body;
		System.out.println(httpClient.getRobotsTxtURL(url));
		
		RobotsTxtParse robotTxtParse = new RobotsTxtParse(body);
		
		RobotsTxtInfo info = new RobotsTxtParse(body).parse();
		
		System.out.println(info.getUserAgent().toString());
		
		
	}
}
