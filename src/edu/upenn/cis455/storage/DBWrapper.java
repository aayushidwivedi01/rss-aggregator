package edu.upenn.cis455.storage;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

public class DBWrapper {
	
	private static String envDirectory = null;
	
	private static Environment myEnv = null;
	private static EntityStore store;
		
	
	public static void setup(String dirName) throws DatabaseException{
		File dbDir = new File(dirName);
		
		if (!(dbDir.isDirectory())){
			if (dbDir.mkdir()) {
				System.out.println("Directory creation successful");
			} else {
				System.out.println("Failed to create new directory");
			}
		}
		
		EnvironmentConfig envConfig = new EnvironmentConfig();
		StoreConfig storeConfig = new StoreConfig();
		
		envConfig.setAllowCreate(true);
		storeConfig.setAllowCreate(true);
			
		myEnv = new Environment(dbDir, envConfig);
		store = new EntityStore(myEnv, "Entity Store", storeConfig);	
						
		
	}
	
	public static EntityStore getStore(){
		return store;
	}
	
	public static void shutdown() throws DatabaseException{
		if (myEnv != null) 
			myEnv.close();
			
		if (store != null)
			store.close();	
	}
	
	
	
//	public static void  main(String args[]){
//		setup("./testDb");
//		
//		UserEntityClass usr = new UserEntityClass();
//		
//		UserDA userDA = new UserDA(store);
//		
//		usr.setUsename("Aayushi");
//		usr.setPassword("anda");
//		
//		userDA.pIdx.put(usr);
//		
//		System.out.println(userDA.pIdx.get("Aayushi"));
//		
//	}
	
	
}
