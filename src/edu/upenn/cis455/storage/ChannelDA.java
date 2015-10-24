package edu.upenn.cis455.storage;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ChannelDA {

		public PrimaryIndex<String, ChannelEntityClass> pIdx;

		public ChannelDA(EntityStore store) {
			pIdx = store.getPrimaryIndex(String.class, ChannelEntityClass.class);

		}

}
