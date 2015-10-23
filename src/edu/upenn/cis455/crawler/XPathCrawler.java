package edu.upenn.cis455.crawler;

import java.util.HashMap;
import java.util.LinkedList;

import edu.upenn.cis455.crawler.info.RobotsTxtInfo;
import edu.upenn.cis455.storage.DBWrapper;


public class XPathCrawler {
	
	private final static  int MAX_POOL_SIZE  = 1;
	private static String store;
	
	static CrawlerThread[] crawlerThread = new CrawlerThread[MAX_POOL_SIZE];
	
	
	private static void generateCrawlerPool(LinkedList<String> urlQueue, HashMap<String, RobotsTxtInfo> map){
		for(int i = 0; i < MAX_POOL_SIZE; i++){
			crawlerThread[i] = new CrawlerThread(urlQueue, map);
			crawlerThread[i].setName("Crawler "+i);
    		
			crawlerThread[i].start();
    	}
		
	}
	
	
	public static void usage(){
		//TO-DO
		System.out.println("Needs following parameters in order:");
		System.out.println("SEED URL:  URL of the Web page at which to start");
		System.out.println("Berkley DB store dir");
		System.out.println("Max file size in MBs");
		System.out.println("[Optional] No. of files to retrieve");
	}
	
	public static void main(String args[]){
		
		LinkedList<String> urlQueue = new LinkedList<>();
		HashMap<String, RobotsTxtInfo> hostRobotsTxtMap = new HashMap<>();
		
		if (args.length < 3){
			usage();
			System.exit(-1);
			
		}
		
		if (args.length == 4){
			try{
				int maxNumFiles = Integer.parseInt(args[3]);
			} catch (Exception e){
				System.out.println("Invalid file size");
				System.exit(-1);
			}
		}
		
		//create DB store
		store = args[1];
		DBWrapper dbWrapper = new DBWrapper();
		dbWrapper.setup(args[1]);
		
		//create worker threads
		generateCrawlerPool(urlQueue, hostRobotsTxtMap);
		
		synchronized(urlQueue){
			System.out.println("Seed url added");
			urlQueue.add(args[0]);
			urlQueue.notify();
		}
		
		for( CrawlerThread th : crawlerThread){
			try {
				th.join();
			} catch (InterruptedException e1) {
				System.out.println("[ERROR] Unable to join thread" + th.getName());
			}
		}
		
		dbWrapper.shutdown();
		
		
	}
	
}
