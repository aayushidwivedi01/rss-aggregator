package edu.upenn.cis455.storage;


import java.util.ArrayList;
import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import static com.sleepycat.persist.model.Relationship.*;

import com.sleepycat.persist.model.SecondaryKey;

import edu.upenn.cis455.xpathengine.PathNode;

@Entity
public class ChannelEntityClass {

	@PrimaryKey
	private String channelId;
	private String username;
	private ArrayList<XPath>xPathSet;
	private Date lastCrawledTime;
	private byte[] document;
	
	
	
	
	
}