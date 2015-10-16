package edu.upenn.cis455.servlet;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.Cookie;


public class HttpResponseParser {
	
	
	public HashMap<String, String>otherHeaders = new HashMap<>();
	private String mainRequest;
	public String code;
	public String status;
	private ArrayList<String>otherRequests = new ArrayList<String>();
	public HttpResponseParser(String mainRequest, ArrayList<String>otherRequests){
		this.mainRequest = mainRequest;
		this.otherRequests = otherRequests;
	}
	
	public  void parseResponseHeaders(){
		 
		 String[] splitRequest = mainRequest.split(" ");
			
		 code = splitRequest[1].trim();
		 status =  splitRequest[2].trim();
		 
		
	 }	
	
	public void parseOtherHeaders(){
		for( String req : otherRequests){
			 String[] pair = req.split(":", 2);
			 if (!otherHeaders.containsKey(pair[0].trim())){
				 otherHeaders.put(pair[0].trim().toLowerCase(), pair[1].trim());
			 }
			 
			 
		 }
	}
	
}