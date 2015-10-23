package edu.upenn.cis455.crawler.info;

import java.util.Date;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class DocumentInfo {
	
	
	private String url;
	private String body;
	private String contentType;
	private String lastCrawled;
	
	public DocumentInfo(String url, String body, String contentType, String date){
		this.url = url;
		this.body = body;
		this.contentType = contentType;
		this.lastCrawled = date;
			
	}
	
	public DocumentInfo(){
		
	}
	
	public String getUrl(){
		return this.url;
	}

	public String getBody(){
		return this.body;
	}
	
	public String getContentType(){
		return this.contentType;
	}

	public String getLastCrawled(){
		return this.lastCrawled;
	}
}
