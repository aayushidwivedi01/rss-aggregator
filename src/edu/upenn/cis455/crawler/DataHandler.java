package edu.upenn.cis455.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.sleepycat.persist.EntityCursor;

import edu.upenn.cis455.crawler.info.DocumentInfo;
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
	
	public void storeUserData(String dir, String name, String password){
		DBWrapper.setup(dir);
		UserDA userDA = new UserDA(DBWrapper.getStore());
		UserEntityClass user = new UserEntityClass();
		user.setUsername(name);
		user.setPassword(password);
		userDA.pIdx.put(user);
		DBWrapper.shutdown();
	}
	
	
	public String getUsername(String dir, String name){
		DBWrapper.setup(dir);
		UserDA userDA = new UserDA(DBWrapper.getStore());
		UserEntityClass user = userDA.pIdx.get(name);
		if (user != null){
			DBWrapper.shutdown();
			return user.getUsername();
		}
		DBWrapper.shutdown();
		return null;
		
	}
	
	public boolean verifyPassword(String dir, String name, String passwd){
		DBWrapper.setup(dir);
		UserDA userDA = new UserDA(DBWrapper.getStore());
		String password;
		UserEntityClass user = userDA.pIdx.get(name);
		if (user == null){
			DBWrapper.shutdown();
			return false;
		}
			
		password = user.getPassword();
		
		DBWrapper.shutdown();

		if (password != null && password.equals(passwd)){
			return true;
		}
		return false;
	}
	
	public boolean addChannel(String dir, String channelName, String xpaths, String username){
		DBWrapper.setup(dir);
		ChannelDA channelDA = new ChannelDA(DBWrapper.getStore());
		ChannelEntityClass channel= channelDA.pIdx.get(channelName);
		if (channel != null){
			DBWrapper.shutdown();
			return false;
		}
		ChannelEntityClass newChannel = new ChannelEntityClass();
		newChannel.setChannelName(channelName);
		newChannel.setXPaths(xpaths);
		newChannel.setUsername(username);
		channelDA.pIdx.put(newChannel);
		DBWrapper.shutdown();
		return true;
		
		
		

		
	}
	
	public ArrayList<String> getAllChannelNames(String dir){
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
	public ArrayList<ChannelEntityClass> getAllChannels(){
		ArrayList<ChannelEntityClass>allChannels = new ArrayList<>();
		ChannelDA channelDA = new ChannelDA(DBWrapper.getStore());
		EntityCursor<ChannelEntityClass> cursor = channelDA.pIdx.entities();
		for (ChannelEntityClass channel : cursor){
			allChannels.add(channel);
		}
		cursor.close();
		return allChannels;
		
		
	}
	
	
	
	public ArrayList<DocumentInfo> getChannelContent(String dir, String channelName){
		
		ArrayList<DocumentInfo>content = new ArrayList<DocumentInfo>();
		DBWrapper.setup(dir);
		ChannelDA channelDA = new ChannelDA(DBWrapper.getStore());
		ChannelEntityClass channel = channelDA.pIdx.get(channelName);
		if (channel == null){
			DBWrapper.shutdown();
			return null;
		}
		HashSet<String>documentLinks = channel.getDocumentLinks();
		CrawledDA crawled = new CrawledDA(DBWrapper.getStore());
		for (String url : documentLinks){
			CrawledEntityClass crawledData = crawled.pIdx.get(url);
			if (crawledData != null){
				content.add(crawledData.getDoc());	
			}
			
		}
		DBWrapper.shutdown();
		return content;
		
	}
	
	public boolean deleteChannel(String dir, String channelName, String username){
		DBWrapper.setup(dir);
		ChannelDA channelDA = new ChannelDA(DBWrapper.getStore());
		ChannelEntityClass channel = channelDA.pIdx.get(channelName);
		if (!channel.getUsername().equals(username)){
			DBWrapper.shutdown();
			return false;
		}
			
		if (channel != null){
			channelDA.pIdx.delete(channelName);
		}
		DBWrapper.shutdown();
		return true;
	}
	

}
