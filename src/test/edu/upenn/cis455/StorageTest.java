package test.edu.upenn.cis455;

import static org.junit.Assert.assertEquals;
import junit.framework.TestCase;

import org.junit.Test;

import edu.upenn.cis455.crawler.ChannelDA;
import edu.upenn.cis455.crawler.ChannelEntityClass;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.UserDA;
import edu.upenn.cis455.storage.UserEntityClass;

public class StorageTest extends TestCase{

	@Test
	public void usernameStoredCorrectly(){
		String dir = "testDB1";
		DBWrapper.setup(dir);
		UserDA userDA = new UserDA(DBWrapper.getStore());
		UserEntityClass user = new UserEntityClass();
		String username = "Gandalf";
		String password = "Shadowfax";
		user.setUsername(username);
		user.setPassword(password);
		userDA.pIdx.put(user);
		user = userDA.pIdx.get(username);
		String actualUsername = user.getUsername();
		String actualPassword = user.getPassword();
		DBWrapper.shutdown();
		assertEquals(username, actualUsername);
		assertEquals(password, actualPassword);
	}
	
	@Test
	public void channelStorageTest(){
		String dir = "testDB2";
		DBWrapper.setup(dir);
		ChannelDA channelDA = new ChannelDA(DBWrapper.getStore());
		
		ChannelEntityClass channel= new ChannelEntityClass();
		channel.setChannelName("LOTR");
		channelDA.pIdx.put(channel);
		String actualChannelName = channelDA.pIdx.get("LOTR").getChannelName();
		assertEquals("LOTR", actualChannelName);
	}
}
