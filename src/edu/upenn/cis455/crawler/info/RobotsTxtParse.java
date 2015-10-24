package edu.upenn.cis455.crawler.info;

import java.util.ArrayList;

public class RobotsTxtParse {

	private RobotsTxtInfo robotsTxtInfo = new RobotsTxtInfo();
	private String robotsTxt;
	private String currentUserAgent = null;

	public RobotsTxtParse(String file) {
		this.robotsTxt = file;
	}

	public RobotsTxtInfo parse() {

		String[] allLines = robotsTxt.split("\n");

		for (String line : allLines) {
			if (line.startsWith("User-agent")) {
				String agent = line.split(":", 2)[1].trim();
				currentUserAgent = agent;
				robotsTxtInfo.addUserAgent(agent.trim());

			}

			else if (line.startsWith("Allow")) {
				String allow = line.split(":", 2)[1].trim();
				robotsTxtInfo.addAllowedLink(currentUserAgent, allow);
			}

			else if (line.startsWith("Disallow")) {
				String disallow = line.split(":", 2)[1].trim();
				robotsTxtInfo.addDisallowedLink(currentUserAgent, disallow);
			} else if (line.startsWith("Crawl-delay")) {
				int crawlDelay = Integer.valueOf(line.split(":", 2)[1].trim());
				robotsTxtInfo.addCrawlDelay(currentUserAgent, crawlDelay);
			} else if (line.startsWith("Sitemap")) {
				String sitemap = line.split(":", 2)[1].trim();
				robotsTxtInfo.addSitemapLink(sitemap);
			}
		}

		return robotsTxtInfo;
	}

}
