package edu.upenn.cis455.crawler;

import edu.upenn.cis455.crawler.info.DocumentInfo;
import edu.upenn.cis455.storage.CrawledDA;
import edu.upenn.cis455.storage.CrawledEntityClass;
import edu.upenn.cis455.storage.DBWrapper;

public class DataHandler {
	
	public void storeCrawlerData(String url, DocumentInfo doc){
		CrawledDA crawledDA = new CrawledDA(DBWrapper.getStore());
		CrawledEntityClass crawled = new CrawledEntityClass();
		crawled.setUrl(url);
		crawled.setDoc(doc);
		crawledDA.pIdx.put(crawled);
		
		
		
	}
	
	public CrawledEntityClass getCrawledData(String url){
		CrawledDA crawledDA = new CrawledDA(DBWrapper.getStore());
		return crawledDA.pIdx.get("https://dbappserv.cis.upenn.edu/crawltest.html");
		
	}

}
