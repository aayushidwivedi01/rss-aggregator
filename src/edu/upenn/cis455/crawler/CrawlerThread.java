package edu.upenn.cis455.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import edu.upenn.cis455.crawler.info.DocumentInfo;
import edu.upenn.cis455.crawler.info.RobotsTxtInfo;
import edu.upenn.cis455.crawler.info.RobotsTxtParse;

/**
 * Worker thread for crawler
 * Does all the crawling 
 * @queue : Share queue containing links to be crawled
 * Crawls HTMLS only;
 * Stores documents (both html and xml) in Berkeley Data Store
 */
public class CrawlerThread extends Thread {
	private LinkedList<String> urlQueue;
	private HashMap<String, RobotsTxtInfo> hostnameRobotsMap = new HashMap<>();
	private HashSet<String>visitedURLS = new HashSet<>();
	private String protocol;
	private DataHandler dataHandler = new DataHandler();
	private CrawledEntityClass crawledData;

	public CrawlerThread(LinkedList<String> queue,
			HashMap<String, RobotsTxtInfo> map) {
		this.urlQueue = queue;
		this.hostnameRobotsMap = map;
	}
	
	
	//method to find url relative to hostname
	// @url: to be crawled
	//@hostname
	public String relativeURL(String url, String hostname) {
		String temp;
		if (url.startsWith("https")) {
			protocol = "https";
			temp = "https://" + hostname;

		} else {
			protocol = "http";
			temp = "http://" + hostname;
		}

		temp = url.substring(temp.length());
		return temp;
	}

	/**
	 * 
	 * @param docURL : URL to be crawled
	 * @param hostname : Hostname of the URL
	 * @return true if can crawl , based on robots.txt
	 */
	public boolean canCrawl(String docURL, String hostname) {
		String url = relativeURL(docURL, hostname);
		// System.out.println("Relative url:" + url + "\n" + "Full: " + docURL);
		RobotsTxtInfo robotsTxtInfo = hostnameRobotsMap.get(hostname);
		String userAgent = "cis455crawler";

		long currTime = new Date().getTime() / 1000;
		long lastCrawled = robotsTxtInfo.getLastCrawled();
		long crawlDelay = 0;
		if (robotsTxtInfo.containsUserAgent(userAgent)) {
			crawlDelay = robotsTxtInfo.getCrawlDelay(userAgent);
		} else if (robotsTxtInfo.containsUserAgent("*")) {
			crawlDelay = robotsTxtInfo.getCrawlDelay(userAgent);
		}
		if (currTime - lastCrawled < crawlDelay)
			return false;

		// robotsTxtInfo.print();
		if (robotsTxtInfo.containsUserAgent(userAgent)) {
			// check crl delay
			ArrayList<String> allowedLinks = robotsTxtInfo
					.getAllowedLinks(userAgent);
			if (allowedLinks != null && allowedLinks.contains(url)) {
				return true;
			} else {
				ArrayList<String> disAllowedLinks = robotsTxtInfo
						.getDisallowedLinks(userAgent);
				if (disAllowedLinks != null
						&& disAllowedLinks.contains(url)) {
						return false;
				} else if (disAllowedLinks != null) {
					// check if any of the paths are a parent in url
					for (String link : disAllowedLinks) {
						if (url.startsWith(link)) {
							return false;
						}
					}

				}
				return true;
			}
		} else if (robotsTxtInfo.containsUserAgent("*")) {
			userAgent = "*";

			ArrayList<String> allowedLinks = robotsTxtInfo
					.getAllowedLinks(userAgent);
			if (allowedLinks != null && allowedLinks.contains(userAgent)) {
				return true;
			} else {
				ArrayList<String> disAllowedLinks = robotsTxtInfo
						.getAllowedLinks(userAgent);
				if (disAllowedLinks != null
						&& disAllowedLinks.contains(userAgent)) {
					return false;
				} else if (disAllowedLinks != null) {
					// check if any of the paths are a parent in url
					for (String link : disAllowedLinks) {
						if (url.startsWith(link)) {
							return false;
						}
					}

				}
				return true;
			}

		}

		return true;
	}

	/**
	 * return date present in HttpResponse header
	 * Used to set time of crawling
	 * @param resp
	 * @return
	 */
	public String getDate(HttpResponse resp) {
		if (resp.headers.containsKey("Date")) {
			return resp.headers.get("Date").get(0);

		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss z");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date date = new Date();
			return sdf.format(date);
		}

	}

	/**
	 * Main crawling function
	 * @param url to be crawled
	 * @param httpClient contains content_type of document
	 * @param response contains info related to html/xml document
	 */
	public void crawl(String url, HttpClient httpClient, HttpResponse response) {

		// extract links from html
		// check content type, content len, return type;
		if (visitedURLS.contains(url)){
			System.out.println("Has been crawled");
			return;
		}
			
		if (response != null) {
			// if html crawl, if xml save to DB
			if (!response.headers.containsKey("Content-Type"))
				return;
			try {
				int fileSize = Integer.valueOf(response.headers
						.get("Content-Length").get(0).trim());
				
				if (fileSize > XPathCrawler.getMaxFileSize()) {
					System.out.println("File too big. Skip crawling");
					return;
				}
			} catch (Exception e) {
				System.out.println("Invalid file size format in header");
				return;
			}
			if (httpClient.CONTENT_TYPE.equals("html")) {
				// crawl
				// Store the html in DB
				
				String crawlTime = getDate(response);
				DocumentInfo doc = new DocumentInfo(url, response.body, "html",
						crawlTime);
				dataHandler.storeCrawlerData(url, doc);

				
				System.out.println(url + " :Downloading");
				HtmlLinkExtractor htmlLink = new HtmlLinkExtractor(
						response.body, url, urlQueue);
				htmlLink.extract();
				visitedURLS.add(url);
				
			} else if (httpClient.CONTENT_TYPE.equals("xml")) {
				
				//add to visited
				visitedURLS.add(url);
				
				
				// save to DB
				
				String crawlTime = getDate(response);
				DocumentInfo doc = new DocumentInfo(url, response.body, "xml",
						crawlTime);
				dataHandler.storeCrawlerData(url, doc);
				XMLHandler.matchXPaths(response.body, url);
			}

		}
	}
	//returns last crawled time of the url that is being crawled
	public String getLastCrawledTime() {
		DocumentInfo docInfo = crawledData.getDoc();
		String lastCrawled = docInfo.getLastCrawled();
		return lastCrawled;
		// httpClient.isRobot = false;
		// HttpResponse response = httpClient.getResponse(url, "HEAD",
		// "cis455crawler");
	}
	//returns body of the url being crawled
	public String getCrawledDoc() {
		DocumentInfo docInfo = crawledData.getDoc();
		return docInfo.getBody();
	}
	
	//checks if a url pre-exists in Berkeley database
	public boolean isInDB(String url) {

		// check in DB
		crawledData = null;
		if ((crawledData = dataHandler.getCrawledData(url)) != null) {
			// url exists

			return true;
		} else {
			return false;
		}

	}
	//manages the thread functions
	public void run() {
		try {
			while (!CrawlStatus.signalStop()) {
				synchronized (urlQueue) {
					if (urlQueue.isEmpty()) {
						try {
							System.out.println("Waiting");
							urlQueue.wait();
						} catch (InterruptedException e) {
							if (CrawlStatus.signalStop()) {
								System.out.println(Thread.currentThread()
										.getName() + " is stopped");
							} else {
								System.out
										.println("Crawler thread interrupted while waiting");

								e.printStackTrace();
							}

						}

					} else {
						String url = urlQueue.removeFirst();
						XPathCrawler.decrement();
						if (CrawlStatus.signalStop()){
							break;
						}
						String userAgent = "cis455crawler";
						HttpClient httpClient = new HttpClient();
						String hostname = httpClient.getHostName(url);

						// check if url in hostname-robot map
						if (hostnameRobotsMap != null
								&& hostnameRobotsMap.containsKey(hostname)) {
							// have robots.txt for this hostname
							// check user agent,etc fields
							// TO-DO:what to do when robots present in map
							
							if (isInDB(url)) {
								String lastCrawled = getLastCrawledTime();
								String responseCode = httpClient
										.isModifiedSince(url, lastCrawled,
												userAgent);
								if (responseCode.equals("200")) {
									// has been modified
									HttpResponse response = httpClient
											.getResponse(url, "GET", userAgent);
									crawl(url, httpClient, response);
								} else if (responseCode.equals("304")) {
									System.out
											.println("Not modified. Crawling old doc: "
													+ url);
									if (url.endsWith(".xml")){
										XMLHandler.matchXPaths(getCrawledDoc(), url);
										
									}
									else {
										HtmlLinkExtractor htmlLink = new HtmlLinkExtractor(
												getCrawledDoc(), url, urlQueue);
										htmlLink.extract();
										
									}
									

								} else if (responseCode.equals("301")) {
									
								}
								
								synchronized(hostnameRobotsMap){
									hostnameRobotsMap.get(hostname).setLastCrawled(new Date().getTime());
								}
							} else {
								// crawl normally
								HttpResponse response = httpClient.getResponse(
										url, "GET", userAgent);

								crawl(url, httpClient, response);

							}

						} else {
							// TO-DO: crawl only if it is html
							// send GET request for urlHostName/robots.txt
							// parse robots.txt and populate RobotsTxtInfo
							// put details in urlRobotsMap

							String robotsTxtUrl = httpClient
									.getRobotsTxtURL(url);

							HttpResponse response;
							if (robotsTxtUrl != null) {
								httpClient.setIsRobot(true);
								response = httpClient.getResponse(robotsTxtUrl,
										"GET", userAgent);
								httpClient.setIsRobot(false);

							} else {
								continue;
							}

							// RobotsTxtParse robotsTxtParse = new
							// RobotsTxtParse(response.body);

							RobotsTxtInfo info = new RobotsTxtParse(
									response.body).parse();
							synchronized (hostnameRobotsMap) {
								hostnameRobotsMap.put(hostname, info);
							}

							
							if (canCrawl(url, hostname)) {
								response = httpClient.getResponse(url, "GET",
										userAgent);
								crawl(url, httpClient, response);
								synchronized(hostnameRobotsMap){
									hostnameRobotsMap.get(hostname).setLastCrawled(new Date().getTime());
								}
								
							} else
								continue;

						}
						// System.exit(-1);
					}
				}
			}

		} catch (Exception e) {
			if (CrawlStatus.signalStop()) {
				System.out.println(Thread.currentThread().getName()
						+ " is stopped");
			} else {
				System.out.println(e);
			}
		}

	}

}
