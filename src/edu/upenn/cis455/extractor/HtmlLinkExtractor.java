package edu.upenn.cis455.extractor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

public class HtmlLinkExtractor {
	
	private  String doc;
	private String url;
	private LinkedList<String> queue;
	public HtmlLinkExtractor(String doc, String url, LinkedList<String> queue){
		
		this.doc = doc;
		this.url = url;
		this.queue = queue;
		
	}
	
	public Document parse(){		
	    	Document document = Jsoup.parse(doc, url);
	    	return document;
		
		
	}
	public void extract(){
		Document document = parse();
		Elements links = document.select("a[href]");
		System.out.println("Built Urls for  "+ url );
		for (Element link : links) {
			System.out.println(link.attr("abs:href"));
			synchronized(queue){
				queue.add(link.attr("abs:href"));
			}
		}
            
		return;

	} 
	

}
