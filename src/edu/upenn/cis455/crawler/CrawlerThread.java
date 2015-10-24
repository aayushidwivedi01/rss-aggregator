package edu.upenn.cis455.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import edu.upenn.cis455.crawler.info.DocumentInfo;
import edu.upenn.cis455.crawler.info.RobotsTxtInfo;
import edu.upenn.cis455.crawler.info.RobotsTxtParse;

public class CrawlerThread extends Thread {
	private LinkedList<String> urlQueue;
	private HashMap<String, RobotsTxtInfo> hostnameRobotsMap = new HashMap<>();
	private String protocol;
	private DataHandler dataHandler = new DataHandler();
	private CrawledEntityClass crawledData;

	public CrawlerThread(LinkedList<String> queue,
			HashMap<String, RobotsTxtInfo> map) {
		this.urlQueue = queue;
		this.hostnameRobotsMap = map;
	}

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

	public boolean canCrawl(String docURL, String hostname) {
		String url = relativeURL(docURL, hostname);
		// System.out.println("Relative url:" + url + "\n" + "Full: " + docURL);
		RobotsTxtInfo robotsTxtInfo = hostnameRobotsMap.get(hostname);
		String userAgent = "cis455crawler";

		long currTime = new Date().getTime() / 1000;
		long lastCrawled = robotsTxtInfo.getLastCrawled();
		long crawlDelay = -1;
		if (robotsTxtInfo.containsUserAgent(userAgent)) {
			crawlDelay = robotsTxtInfo.getCrawlDelay(userAgent);
		} else if (robotsTxtInfo.containsUserAgent("*")) {
			crawlDelay = robotsTxtInfo.getCrawlDelay(userAgent);
		}
		if (currTime - lastCrawled < crawlDelay)
			return false;

		// robotsTxtInfo.print();
		if (robotsTxtInfo.containsUserAgent(userAgent)) {
			// check crwal delay
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

	public void crawl(String url, HttpClient httpClient, HttpResponse response) {

		// extract links from html
		// check content type, content len, return type;
		if (response != null) {
			// if html crawl, if xml save to DB
			if (!response.headers.containsKey("Content-Type"))
				return;
			try {
				int fileSize = Integer.valueOf(response.headers
						.get("Content-Length").get(0).trim());
				System.out.println("File size: " + fileSize);
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

				System.out.println("Now Crawling " + url);
				HtmlLinkExtractor htmlLink = new HtmlLinkExtractor(
						response.body, url, urlQueue);
				htmlLink.extract();
				System.out.println("Done crawling this link : " + url);
			} else if (httpClient.CONTENT_TYPE.equals("xml")) {
				// save to DB
				System.out.print("Add XML to DB");
				String crawlTime = getDate(response);
				DocumentInfo doc = new DocumentInfo(url, response.body, "xml",
						crawlTime);
				dataHandler.storeCrawlerData(url, doc);
			}

		}
	}

	public String getLastCrawledTime() {
		DocumentInfo docInfo = crawledData.getDoc();
		String lastCrawled = docInfo.getLastCrawled();
		return lastCrawled;
		// httpClient.isRobot = false;
		// HttpResponse response = httpClient.getResponse(url, "HEAD",
		// "cis455crawler");
	}

	public String getCrawledDoc() {
		DocumentInfo docInfo = crawledData.getDoc();
		return docInfo.getBody();
	}

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
						System.out.println("QUEUE CONTENT: "
								+ urlQueue.toString());
						String url = urlQueue.removeFirst();
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
								System.out.println("This url is in DB: " + url
										+ "\nCHECK if modified or not");
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
											.println("URL document not modified. Crawling old doc: "
													+ url);
									// System.out.println("Crawled Doc: "+
									// getCrawledDoc());

									HtmlLinkExtractor htmlLink = new HtmlLinkExtractor(
											getCrawledDoc(), url, urlQueue);
									htmlLink.extract();

								} else if (responseCode.equals("301")) {
									System.out.println("Dont do anything");
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
								System.out.println("robots.txt URL null;");
								continue;
							}

							// RobotsTxtParse robotsTxtParse = new
							// RobotsTxtParse(response.body);

							RobotsTxtInfo info = new RobotsTxtParse(
									response.body).parse();
							synchronized (hostnameRobotsMap) {
								hostnameRobotsMap.put(hostname, info);
							}

							response = httpClient.getResponse(url, "GET",
									userAgent);
							if (canCrawl(url, hostname)) {
								crawl(url, httpClient, response);
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
