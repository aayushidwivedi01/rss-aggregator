package edu.upenn.cis455.crawler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class ChannelEntityClass {

	@PrimaryKey
	private String channelName;
	private String username;
	private String[] xpaths ;
	@SecondaryKey(relate=Relationship.MANY_TO_MANY, relatedEntity = CrawledEntityClass.class, onRelatedEntityDelete = DeleteAction.NULLIFY)
	private HashSet<String> documentLinks = new HashSet<>();
	

	@Override
	public String toString() {
		return "ChannelEntityClass [username="
				+ username + ", channelName=" + channelName + ", xpaths="
				+ xpaths + ", documentLinks=" + documentLinks + "]";
	}


	
	public void setUsername(String username){
		this.username = username;
		
	}
	
	public void setChannelName(String channelName){
		this.channelName = channelName;
		
	}
	
	public void setXPaths(String xpath){
		String[] temp = xpath.split(";");
		xpaths = new String[temp.length];
		xpaths = temp;
		
		
	}
	
	public void addDocumentLink(String url){
		documentLinks.add(url);		
	}

	

	public String[] getXpaths() {
		return xpaths;
	}

	public HashSet<String> getDocumentLinks() {
		return documentLinks;
	}

	public String getUsername() {
		return username;
	}

	public String getChannelName() {
		return channelName;
	}
	
	

}
