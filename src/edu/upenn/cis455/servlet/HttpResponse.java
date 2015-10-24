package edu.upenn.cis455.servlet;

/**
 * This class is used by HttpClient to
 * parse the response received from the host
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

public class HttpResponse {

	public Map<String, List<String>> headers = new HashMap<>();
	private String mainRequest;
	public String code = null;
	public String status = null;
	public String body;
	private ArrayList<String> otherRequests = new ArrayList<String>();

	public HttpResponse(String mainRequest, ArrayList<String> otherRequests) {
		this.mainRequest = mainRequest;
		this.otherRequests = otherRequests;
	}

	public HttpResponse() {

	}

	// sets the status and code of the response
	public void parseResponseHeaders() {

		String[] splitRequest = mainRequest.split(" ");

		code = splitRequest[1].trim();
		status = splitRequest[2].trim();

	}

	// parses rest of the headers in the response
	public void parseOtherHeaders() {
		for (String req : otherRequests) {
			String[] pair = req.split(":", 2);
			if (!headers.containsKey(pair[0].trim())) {
				ArrayList<String> value = new ArrayList<String>();
				value.add(pair[1].trim());
				headers.put(pair[0].trim().toLowerCase(), value);
			} else {
				headers.get(pair[0].trim()).add(pair[1].trim());
			}

		}
	}

	public void setBody(String body) {
		this.body = body;
	}

}