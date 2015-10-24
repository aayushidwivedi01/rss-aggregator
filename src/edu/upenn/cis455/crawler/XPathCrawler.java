package edu.upenn.cis455.crawler;

import java.util.HashMap;
import java.util.LinkedList;

import edu.upenn.cis455.crawler.info.RobotsTxtInfo;
import edu.upenn.cis455.storage.DBWrapper;

public class XPathCrawler {

	private final static int MAX_POOL_SIZE = 2;
	private static String store;
	private static long maxFileSize;
	private static int maxNumFiles;
	private int numFilesCrawled = -1;

	static CrawlerThread[] crawlerThread = new CrawlerThread[MAX_POOL_SIZE];

	private static void generateCrawlerPool(LinkedList<String> urlQueue,
			HashMap<String, RobotsTxtInfo> map) {
		for (int i = 0; i < MAX_POOL_SIZE; i++) {
			crawlerThread[i] = new CrawlerThread(urlQueue, map);
			crawlerThread[i].setName("Crawler " + i);

			crawlerThread[i].start();
		}

	}

	public static long getMaxFileSize() {
		return maxFileSize;
	}

	public static int getMaxNumFiles() {
		return maxNumFiles;
	}

	public static void usage() {
		// TO-DO
		System.out.println("Needs following parameters in order:");
		System.out.println("SEED URL:  URL of the Web page at which to start");
		System.out.println("Berkley DB store dir");
		System.out.println("Max file size in MBs");
		System.out.println("[Optional] No. of files to retrieve");
	}

	public static void main(String args[]) {

		LinkedList<String> urlQueue = new LinkedList<>();
		HashMap<String, RobotsTxtInfo> hostRobotsTxtMap = new HashMap<>();

		if (args.length < 3) {
			usage();
			System.exit(-1);

		}

		if (args.length == 4) {
			try {
				maxNumFiles = Integer.parseInt(args[3]);
			} catch (Exception e) {
				System.out.println("Invalid max num of files");
				System.exit(-1);
			}
		}
		// set maxFileSize
		try {
			maxFileSize = Long.valueOf(args[2]) * 1024 * 1024;

		} catch (Exception e) {
			System.out.println("Invalid file size");
			System.exit(-1);
		}

		// create DB store
		store = args[1];
		DBWrapper dbWrapper = new DBWrapper();
		dbWrapper.setup(args[1]);

		// set CrawlStatus
		CrawlStatus crawlStatus = new CrawlStatus(urlQueue);

		// create worker threads
		generateCrawlerPool(urlQueue, hostRobotsTxtMap);

		crawlStatus.setPool(crawlerThread);
		crawlStatus.start();

		synchronized (urlQueue) {
			System.out.println("Seed url added");
			urlQueue.add(args[0]);
			urlQueue.notify();
		}

		for (CrawlerThread th : crawlerThread) {
			try {
				System.out.println("Thread " + th.getName() + " returned");
				th.join();
			} catch (InterruptedException e1) {
				System.out.println("[ERROR] Unable to join thread"
						+ th.getName());
			}
		}
		try {
			crawlStatus.join();
		} catch (InterruptedException e) {
			System.out.println("[ERROR] Unable to join CrawlSatus thread ");

		}
		dbWrapper.shutdown();

	}

}
