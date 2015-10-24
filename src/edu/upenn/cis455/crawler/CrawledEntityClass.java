package edu.upenn.cis455.crawler;

import java.util.ArrayList;
import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import edu.upenn.cis455.crawler.info.DocumentInfo;

@Entity
public class CrawledEntityClass {

	@Override
	public String toString() {
		return "CrawledEntityClass [url=" + url + ", doc=" + doc + "]";
	}

	@PrimaryKey
	private String url;
	private DocumentInfo doc;

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDoc(DocumentInfo doc) {
		this.doc = doc;
	}

	public String getUrl() {
		return this.url;
	}

	public DocumentInfo getDoc() {
		return this.doc;
	}
}
