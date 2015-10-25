package edu.upenn.cis455.crawler;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public class ChannelDA {

		public PrimaryIndex<String, ChannelEntityClass> pIdx;
		
		public ChannelDA(EntityStore store) {
			pIdx = store.getPrimaryIndex(String.class, ChannelEntityClass.class);
			
		}

}
