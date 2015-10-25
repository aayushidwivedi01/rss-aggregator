package edu.upenn.cis455.crawler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.xpathengine.XPathEngine;
import edu.upenn.cis455.xpathengine.XPathEngineFactory;

public class XMLHandler {
	
	public static ArrayList<ChannelEntityClass> getChannels(){
		String store = XPathCrawler.getStore();
		ArrayList<ChannelEntityClass> channelMap =
				new DataHandler().getAllChannels();
		return channelMap;
	}
	
	
	public static void matchXPaths(String xmlBody, String url){
		
		ArrayList<ChannelEntityClass>channels = getChannels();
		try{
			for (ChannelEntityClass channel : channels){
				String channelName = channel.getChannelName();
				String[] xpaths = channel.getXpaths();
				System.out.println("XPATHS: " + xpaths.length );
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(new ByteArrayInputStream(xmlBody
						.toString().getBytes()));
				
				XPathEngine xPathEngine = XPathEngineFactory.getXPathEngine();
				xPathEngine.setXPaths(xpaths);
				System.out.println("Mathcing+++++++++++++" + url);
				boolean[] matches = xPathEngine.evaluate(doc);
				for (int i = 0; i < xpaths.length; i++) {
					if (matches[i]) {
						System.out.println("Mathced+++++++++++++" + url);
						channel.addDocumentLink(url);
					} 
				}
				
				ChannelDA channelDA = new ChannelDA(DBWrapper.getStore());
				channelDA.pIdx.put(channel);
			}
		} catch (SAXException e){
			System.out.println("XML not well formed");
		} catch (IOException e){
			System.out.println("Error parsing XML to DOM object");
		} catch (ParserConfigurationException e){
			System.out.println("Error parsing XML to DOM object");
		}
	
		
		
		
	}
	

}
