package edu.upenn.cis455.servlet;

/**
 * This class implements a simple HttpClient
 * It fetches the data from the requested url,
 * parses it into Document
 * @param content: Address / url of the xml/html file in StringBuilder format
 * return : Document
 */

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
    public Document request(String  docURL){
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
            	out.close();
                in.close();
                clientSocket.close();
            	return null;
            	
            }
            
            out.close();
            in.close();
            clientSocket.close();
            
            if (httpResponseParser.otherHeaders.containsKey("content-type")){
            	String contentType = httpResponseParser.otherHeaders.get("content-type");
            	if (contentType.contains("XML") || contentType.contains("xml")){
            		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        			DocumentBuilder db = dbf.newDocumentBuilder();
        			Document doc = db.parse( new ByteArrayInputStream( response.toString().getBytes() ) );
            		return doc;
            	}            		
            	else if(contentType.contains("html") || contentType.contains("HTML")){
            		Tidy tidy = new Tidy();
            		tidy.setInputEncoding("UTF-8");
            		tidy.setOutputEncoding("UTF-8");
            		tidy.setWraplen(Integer.MAX_VALUE);
            		tidy.setMakeClean( true );
            		tidy.setXmlOut( true);
            		tidy.setPrintBodyOnly(true);
            		Document doc = tidy.parseDOM( new ByteArrayInputStream( response.toString().getBytes("UTF-8") ) ,  new ByteArrayOutputStream());
            		return doc;
            	}
            }
            else
            	return null;
            
            return null;
           
           
        } catch (UnknownHostException e) {
        	return null;

        } catch (MalformedURLException e) {
        	return null;
        } catch (IOException e){
        	return null;

        } catch (SAXException e){
        	return null;
 		   
 		} catch(ParserConfigurationException e){
 			return null;
 		}
    }
}