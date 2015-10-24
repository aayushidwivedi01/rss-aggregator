package edu.upenn.cis455.crawler;

import java.util.ArrayList;

import com.sleepycat.persist.EntityCursor;

import edu.upenn.cis455.crawler.info.DocumentInfo;
import edu.upenn.cis455.storage.ChannelDA;
import edu.upenn.cis455.storage.ChannelEntityClass;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.UserDA;
import edu.upenn.cis455.storage.UserEntityClass;

public class DataHandler {

	public void storeCrawlerData(String url, DocumentInfo doc) {
		CrawledDA crawledDA = new CrawledDA(DBWrapper.getStore());
		CrawledEntityClass crawled = new CrawledEntityClass();
		crawled.setUrl(url);
		crawled.setDoc(doc);
		crawledDA.pIdx.put(crawled);

	}

	public CrawledEntityClass getCrawledData(String url) {
		CrawledDA crawledDA = new CrawledDA(DBWrapper.getStore());
		return crawledDA.pIdx.get(url);

	}
	
	public UserEntityClass getUserData(String name){
		UserDA userDA = new UserDA(DBWrapper.getStore());
		UserEntityClass user = new UserEntityClass();
		return userDA.pIdx.get(name);
	}
	
	public ArrayList<String> getAllChannels(String dir){
		ArrayList<String>allChannels = new ArrayList<>();
		DBWrapper.setup(dir);
		ChannelDA channelDA = new ChannelDA(DBWrapper.getStore());
		EntityCursor<ChannelEntityClass> cursor = channelDA.pIdx.entities();
		for (ChannelEntityClass channel : cursor){
			allChannels.add(channel.getChannelName());
		}
		cursor.close();
		DBWrapper.shutdown();
		return allChannels;
		
		
	}

}
