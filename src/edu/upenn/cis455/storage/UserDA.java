package edu.upenn.cis455.storage;


import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import static com.sleepycat.persist.model.Relationship.*;

import com.sleepycat.persist.model.SecondaryKey;


public class UserDA {
	
	PrimaryIndex<String,UserEntityClass> pIdx;
	
	public UserDA(EntityStore store){
		pIdx = store.getPrimaryIndex(String.class, UserEntityClass.class);
		
	}

}
