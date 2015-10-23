package edu.upenn.cis455.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import edu.upenn.cis455.crawler.info.RobotsTxtInfo;
import edu.upenn.cis455.crawler.info.RobotsTxtParse;
import edu.upenn.cis455.extractor.HtmlLinkExtractor;
import edu.upenn.cis455.httpClient.HttpClient;
import edu.upenn.cis455.httpClient.HttpResponse;

public class CrawlerThread extends Thread {
	private LinkedList<String>urlQueue;
	private ConcurrentHashMap<String, RobotsTxtInfo> hostnameRobotsMap = new ConcurrentHashMap<>();
	private String protocol;
	
	public CrawlerThread(LinkedList<String> queue){
		this.urlQueue = queue;
	}
	
	public String relativeURL(String url, String hostname){
		String temp;
		if (url.startsWith("https")){
			protocol = "https";
			temp = "https://"+hostname;
			
		}
		else {
			protocol = "http";
			temp = "http://" + hostname;
		}
		
		temp = url.substring(temp.length());
		return temp;
	}
	
	
	
	public boolean canCrawl(String docURL, String hostname){
		String url = relativeURL(docURL, hostname);
		//System.out.println("Relative url:" + url + "\n" + "Full: " + docURL);
		RobotsTxtInfo robotsTxtInfo = hostnameRobotsMap.get(hostname);
		String userAgent = "cis455crawler";
		
		long currTime = new Date().getTime()/1000;
		long lastCrawled = robotsTxtInfo.getLastCrawled();
		long crawlDelay = -1;
		if (robotsTxtInfo.containsUserAgent(userAgent)){
			crawlDelay = robotsTxtInfo.getCrawlDelay(userAgent);
		}
		else if (robotsTxtInfo.containsUserAgent("*")){
			crawlDelay = robotsTxtInfo.getCrawlDelay(userAgent);
		}
		if (currTime - lastCrawled < crawlDelay)
			return false;
		
		boolean crawl = true;
		//robotsTxtInfo.print();
		if (robotsTxtInfo.containsUserAgent(userAgent)){
			//check crwal delay
			ArrayList<String> allowedLinks = robotsTxtInfo.getAllowedLinks(userAgent);
			if (allowedLinks != null && allowedLinks.contains(userAgent)){
				return true;
			}
			else {
				ArrayList<String> disAllowedLinks = robotsTxtInfo.getAllowedLinks(userAgent);
				if ( disAllowedLinks != null && disAllowedLinks.contains(userAgent)){
					return false;
				}
				else if ( disAllowedLinks != null){
					//check if any of the paths are a parent in url
					for (String link : disAllowedLinks){
						if (url.startsWith(link)){
							return false;
						}
					}
					
				}
				return true;
			}
		} 
		else if (robotsTxtInfo.containsUserAgent("*")){
			userAgent = "*";
			
			ArrayList<String> allowedLinks = robotsTxtInfo.getAllowedLinks(userAgent);
			if (allowedLinks != null && allowedLinks.contains(userAgent)){
				return true;
			}
			else {
				ArrayList<String> disAllowedLinks = robotsTxtInfo.getAllowedLinks(userAgent);
				if (disAllowedLinks != null && disAllowedLinks.contains(userAgent)){
					return false;
				}
				else if (disAllowedLinks != null) {
					//check if any of the paths are a parent in url
					for (String link : disAllowedLinks){
						if (url.startsWith(link)){
							return false;
						}
					}
					
				}
				return true;
			}
			
		}
		
		return true;
	}
	
	public void run(){
		
		while(true){
			synchronized(urlQueue){
				if(urlQueue.isEmpty()){
					try {
						System.out.println("Waiting");
						urlQueue.wait();
					} catch (InterruptedException e) {
						System.out.println("Crawler thread interrupted while waiting");
						
						e.printStackTrace();
					}
						
				}
				else {
						String url = urlQueue.removeFirst();
						
						HttpClient httpClient = new HttpClient();
						String hostname = httpClient.getHostName(url);
						
						//check if url in hostname-robot map
						if (hostnameRobotsMap != null && hostnameRobotsMap.containsKey(hostname)){
							// have robots.txt for this hostname
							//check user agent,etc fields
							if( canCrawl(url, hostname)){
								//crawl
								System.out.println("Can crawl");
								String userAgent = "cis455crawler";
								HttpResponse response = httpClient.getResponse(url, "GET",userAgent );
								
								//check content type, content len, return type;
								if (response != null){
									//if html crawl, if xml save to DB
									if (httpClient.CONTENT_TYPE.equals("html")){
										//TO-DO
										//crawl
										System.out.println("Crawling");
									}
									else if (httpClient.CONTENT_TYPE.equals("xml")){
										//TO-DO
										//save to DB
									}
								}
							}
							else{
								continue;
							}
							
						}
						else {
							//TO-DO: crawl only if it is html
							// send GET request for urlHostName/robots.txt
							//parse robots.txt and populate RobotsTxtInfo
							//put details in urlRobotsMap
							
							String robotsTxtUrl = httpClient.getRobotsTxtURL(url);
							
							HttpResponse response ;
							if (robotsTxtUrl != null){
								httpClient.setIsRobot(true);
								response = httpClient.getResponse(robotsTxtUrl, "GET", "cis455crawler");
								httpClient.setIsRobot(false);
								
							} else {
								System.out.println("robots.txt URL null;");
								continue;
							}
							
							//RobotsTxtParse robotsTxtParse = new RobotsTxtParse(response.body);
							
							RobotsTxtInfo info = new RobotsTxtParse(response.body).parse();
							synchronized(hostnameRobotsMap){
								hostnameRobotsMap.put(hostname, info);
							}
							
							response = httpClient.getResponse(url, "GET", "cis455crawler");
							if (canCrawl(url, hostname)){
								
								//System.out.println("Can crawl");
								
								//extract links from html
								//check content type, content len, return type;
								if (response != null){
									//if html crawl, if xml save to DB
									if (httpClient.CONTENT_TYPE.equals("html")){
										//TO-DO
										//crawl
										System.out.println("Now Crawling " + url);
										HtmlLinkExtractor htmlLink = new HtmlLinkExtractor(response.body,url, urlQueue);
										htmlLink.extract();
										System.out.print("Done crawling this link");
									}
									else if (httpClient.CONTENT_TYPE.equals("xml")){
										//TO-DO
										//save to DB
										System.out.print("Add XML to DB");
									}
									
								}
							}
							
							else{
								continue;
							}
							
							
							
							
						}
						//System.exit(-1);
				}
			}
		}
	}
	
	
	

}
