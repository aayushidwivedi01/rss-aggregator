package edu.upenn.cis455.servlet;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

public class HttpClient {
	private void parse(StringBuilder content){
		int count = 0;
		
	}
    public String request(String  docURL){
        try {
            URL url = new URL(docURL);
            int port;
            if (url.getPort() != -1)
                port = url.getPort();
            else
                port = url.getDefaultPort();

            Socket clientSocket = new Socket(url.getHost(), port);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            String request, line;
            request = "GET " + url + " HTTP/1.0\r\n\r\n";
            out.print(request);
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            StringBuilder response = new StringBuilder();
            int count = 0;
//            while ((line = in.readLine()) != null){
//            	if(line.length() == 0)
//            		count ++;
//            	if (count > 0){
//            		response.append(line);
//            	}
//               
//            }
            
            HttpResponseParser httpResponseParser = null;
            ArrayList<String> responseContent = new ArrayList<>();
            while((line = in.readLine()) != null){
				  if(line.length() == 0){
					  String mainRequest = responseContent.remove(0);
					  httpResponseParser = new HttpResponseParser(mainRequest,responseContent);
					  httpResponseParser.parseResponseHeaders();
					  httpResponseParser.parseOtherHeaders();						  
					  break;
				  }
				  responseContent.add(line);
				 				  
			  	}
            
            if (httpResponseParser.code.equals("200") && httpResponseParser.status.equals("OK")){
            	int contentLen = Integer.valueOf(httpResponseParser.otherHeaders.get("content-length"));
            	char[] cbuf = new char[contentLen];
            	in.read(cbuf, 0, contentLen);
            	for(char c : cbuf){
					response.append(c);
				}
            }
            else {
            	return "[ERROR] File could not be fetched";
            	
            }
            
            out.close();
            in.close();
            clientSocket.close();
            
            if (httpResponseParser.otherHeaders.containsKey("content-type")){
            	String contentType = httpResponseParser.otherHeaders.get("content-type");
            	if (contentType.contains("XML") || contentType.contains("xml")){
            		return response.toString();
            	}            		
            	else if(contentType.contains("html") || contentType.contains("HTML")){
            		return response.toString();
            		
            	}
            }
            else
            	return "[ERROR] Content-type unknown";
            
            return "ERROR";
           
           
        } catch (UnknownHostException e) {
        	return "[ERROR] Unknown Host";

        } catch (MalformedURLException e) {
        	return "[ERROR] Malformed URL";
        } catch (IOException e){
        	return "[ERROR] Error While Reading From Socket";

        } 
    }
}