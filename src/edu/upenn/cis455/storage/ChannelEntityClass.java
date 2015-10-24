package edu.upenn.cis455.storage;

import java.util.ArrayList;
import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class ChannelEntityClass {

	@PrimaryKey
	private String channelId;
	private String username;
	private String channelName;
	private ArrayList<String> xpaths = new ArrayList<>();
	

	@Override
	public String toString() {
		return "ChannelEntityClass [channelId=" + channelId + ", username="
				+ username + ", channelName=" + channelName + ", xpaths="
				+ xpaths + ", documentLinks=" + documentLinks + "]";
	}

	private ArrayList<String> documentLinks = new ArrayList<>();
	
	public void setUsername(String username){
		this.username = username;
		
	}
	
	public void setChannelName(String channelName){
		this.channelName = channelName;
		
	}
	
	public void addXPaths(String xpath){
		xpaths.add(xpath);
		
	}
	
	public void addDocument(String document){
		documentLinks.add(document);		
	}
	
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelId() {
		return channelId;
	}

	

	public ArrayList<String> getXpaths() {
		return xpaths;
	}

	public ArrayList<String> getDocumentLinks() {
		return documentLinks;
	}

	public String getUsername() {
		return username;
	}

	public String getChannelName() {
		return channelName;
	}
	
	

}
