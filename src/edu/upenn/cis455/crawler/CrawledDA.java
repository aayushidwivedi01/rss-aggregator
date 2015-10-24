package edu.upenn.cis455.crawler;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class CrawledDA {

	public PrimaryIndex<String, CrawledEntityClass> pIdx;

	public CrawledDA(EntityStore store) {
		pIdx = store.getPrimaryIndex(String.class, CrawledEntityClass.class);

	}

}
