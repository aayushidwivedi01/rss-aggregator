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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

public class HttpClient {

	public boolean isRobot;

	public boolean ifModifiedFlag = false;
	public String lastCrawled;
	public static String CONTENT_TYPE;

	public boolean isHttps(String url) {

		if (url.startsWith("https"))
			return true;
		else
			return false;

	}

	public HttpClient() {
		HttpsURLConnection.setFollowRedirects(false);
	}

	public String getHostName(String docURL) {
		try {
			URL url = new URL(docURL);
			return url.getHost();
		} catch (MalformedURLException e) {

			System.out.println("Malformed URL");
			return null;
		}
	}

	public String getRobotsTxtURL(String docURL) {
		URL url;
		try {
			url = new URL(docURL);
			return url.getProtocol() + "://" + url.getHost() + "/robots.txt";

		} catch (MalformedURLException e) {

			e.printStackTrace();
			return null;
		}

	}

	public void setIsRobot(boolean value) {
		this.isRobot = value;
	}

	public String isModifiedSince(String docURL, String lastCrawled,
			String userAgent) {
		this.ifModifiedFlag = true;
		this.lastCrawled = lastCrawled;

		if (!isHttps(docURL)) {
			HttpResponse headResponse = formHttpRequest(docURL, "HEAD",
					userAgent);
			this.ifModifiedFlag = false;
			return headResponse.code;

		} else {
			HttpResponse headResponse = formHttpsRequest(docURL, "HEAD",
					userAgent);
			this.ifModifiedFlag = false;
			return headResponse.code;
		}
	}

	// TO-DO: check content length before getting the file

	public HttpResponse getResponse(String docURL, String requestType,
			String userAgent) {
		System.out.println("Handling request for URL : " + docURL);
		if (!isHttps(docURL)) {// TO-DO: incomplete: add format conditions
			if (requestType.equalsIgnoreCase("get")) {
				HttpResponse headResponse = formHttpRequest(docURL, "HEAD",
						userAgent);
				System.out.println("HEAD" + headResponse.code);
				if (headResponse.code.equals("200")
						&& headResponse.status.equals("OK")) {
					return formHttpRequest(docURL, requestType, userAgent);
				} else {
					return null;
				}
			} else {
				return formHttpRequest(docURL, requestType, userAgent);
			}
		}

		else {
			if (requestType.equalsIgnoreCase("get")) {
				HttpResponse headResponse = formHttpsRequest(docURL, "HEAD",
						userAgent);
				if (headResponse.code.equals("200")
						&& headResponse.status.equals("OK")) {
					if (headResponse.headers.containsKey("Content-Type")) {
						String contentType = headResponse.headers.get(
								"Content-Type").get(0);
						contentType = contentType.split(";")[0].trim();
						if (contentType.equalsIgnoreCase("text/html")) {
							CONTENT_TYPE = "html";
							return formHttpsRequest(docURL, requestType,
									userAgent);
						} else {
							if (isRobot) {
								CONTENT_TYPE = "robotsTXT";
								return formHttpsRequest(docURL, requestType,
										userAgent);
							}
							String[] validXMLformats = { "text/xml",
									"application/xml", "+xml" };
							for (String format : validXMLformats) {
								if (contentType.equalsIgnoreCase(format)
										|| contentType.endsWith(format)) {
									CONTENT_TYPE = "xml";
									return formHttpsRequest(docURL,
											requestType, userAgent);
								} else {
									CONTENT_TYPE = contentType;
									return null;
								}
							}
						}
					} else {
						CONTENT_TYPE = "NULL";
						return null;
					}

				}// TO-DO: Add 301: Redirect: enqueue url, 304:
					// if-modified-since
				else {

					return null;
				}
			} else {
				return formHttpsRequest(docURL, requestType, userAgent);
			}
		}
		CONTENT_TYPE = "NULL";
		return null;

	}

	public HttpResponse formHttpRequest(String docURL, String requestType,
			String userAgent) {
		System.out.println("Handling HTTP request for URL : " + docURL);

		URL url = null;
		BufferedReader in = null;
		if (!isHttps(docURL)) {
			try {
				url = new URL(docURL);
				int port;
				if (url.getPort() != -1)
					port = url.getPort();
				else
					port = url.getDefaultPort();

				Socket clientSocket = new Socket(url.getHost(), port);
				PrintWriter out = new PrintWriter(
						clientSocket.getOutputStream());
				String request, line;

				if (ifModifiedFlag) {
					request = requestType + " " + url
							+ " HTTP/1.0\r\nUser-Agent: " + userAgent
							+ "\r\nIf-Modified-Since:" + lastCrawled
							+ "\r\n\r\n";
				} else {
					request = requestType + " " + url
							+ " HTTP/1.0\r\nUser-Agent: " + userAgent
							+ "\r\n\r\n";
				}

				out.print(request);
				out.flush();

				in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));

				StringBuilder response = new StringBuilder();

				HttpResponse httpResponse = null;
				ArrayList<String> responseContent = new ArrayList<>();
				while ((line = in.readLine()) != null) {
					if (line.length() == 0) {
						String mainRequest = responseContent.remove(0);
						httpResponse = new HttpResponse(mainRequest,
								responseContent);
						httpResponse.parseResponseHeaders();
						httpResponse.parseOtherHeaders();
						break;
					}
					responseContent.add(line);

				}

				if (httpResponse.code.equals("200")
						&& httpResponse.status.equals("OK")) {
					int contentLen = Integer.valueOf(httpResponse.headers.get(
							"content-length").get(0));
					char[] cbuf = new char[contentLen];
					in.read(cbuf, 0, contentLen);
					for (char c : cbuf) {
						response.append(c);
					}

				} else {
					out.close();
					in.close();
					clientSocket.close();
					return null;

				}

				out.close();
				in.close();
				clientSocket.close();

				// TO-DO check html type as mentioned in specs
				if (requestType.equals("GET")
						&& httpResponse.headers.containsKey("Content-Type")) {
					String contentType = httpResponse.headers.get(
							"Content-Type").get(0);
					if (contentType.contains("XML")
							|| contentType.contains("xml")) {

						httpResponse.body = response.toString();
						in.close();
						return httpResponse;

					} else if (contentType.contains("html")
							|| contentType.contains("HTML")) {
						Tidy tidy = new Tidy();
						tidy.setInputEncoding("UTF-8");
						tidy.setOutputEncoding("UTF-8");
						tidy.setWraplen(Integer.MAX_VALUE);
						tidy.setMakeClean(true);
						tidy.setXmlOut(true);
						tidy.setPrintBodyOnly(true);
						httpResponse.body = response.toString();
						in.close();
						return httpResponse;
					}

					else if (isRobot) {
						httpResponse.body = response.toString();
						in.close();
						return httpResponse;
					}
				}

				httpResponse.setBody(response.toString());
				in.close();
				return httpResponse;

			} catch (MalformedURLException e) {
				return null;

			} catch (IOException e) {
				return null;
			}

		}

		else {
			try {
				url = new URL(docURL);
				HttpsURLConnection con = (HttpsURLConnection) url
						.openConnection();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						con.getInputStream()));

			} catch (MalformedURLException e) {
				return null;

			} catch (IOException e) {
				return null;
			}
		}
		return null;

	}

	public HttpResponse formHttpsRequest(String docURL, String requestType,
			String userAgent) {
		System.out.println("Handling HTTP request for URL : " + docURL);

		URL url = null;
		BufferedReader in = null;
		try {
			if (isHttps(docURL)) {
				url = new URL(docURL);
				HttpsURLConnection con = (HttpsURLConnection) url
						.openConnection();
				con.setRequestMethod(requestType);
				con.setRequestProperty("User-Agent", userAgent);
				if (ifModifiedFlag) {
					System.out.println("If modified  : " + docURL);

					con.setRequestProperty("If-Modified-Since", lastCrawled);
				}

				in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));

				String line;
				// StringBuilder response = new StringBuilder();
				// System.out.println(con.getResponseCode());

				StringBuilder response = new StringBuilder();

				while ((line = in.readLine()) != null) {

					response.append(line + "\n");
				}
				// System.out.println(response.toString());
				System.out.println("Status code : "
						+ String.valueOf(con.getResponseCode()) + url);
				HttpResponse httpResponse = new HttpResponse();
				httpResponse.code = String.valueOf(con.getResponseCode());
				httpResponse.status = con.getResponseMessage();
				httpResponse.headers = con.getHeaderFields();

				// System.out.println("headers:" + httpResponse.headers);

				// TO-DO check html type as mentioned in specs
				if (httpResponse.headers.containsKey("Content-Type")) {
					String contentType = httpResponse.headers.get(
							"Content-Type").get(0);
					if (contentType.contains("XML")
							|| contentType.contains("xml")) {

						httpResponse.body = response.toString();
						in.close();
						return httpResponse;

					} else if (contentType.contains("html")
							|| contentType.contains("HTML")) {
						httpResponse.body = response.toString();
						in.close();
						return httpResponse;
					}

					else if (isRobot) {
						httpResponse.body = response.toString();
						in.close();
						return httpResponse;
					}

					else if (ifModifiedFlag) {
						in.close();
						return httpResponse;
					}
				} else if (ifModifiedFlag) {
					in.close();
					return httpResponse;
				} else {
					in.close();
					return null;
				}

				in.close();

			} else {
				in.close();
				return null;
			}

		} catch (MalformedURLException e) {

		} catch (IOException e) {

		}
		return null;

	}

	public Document request(String docURL) {
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

			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			StringBuilder response = new StringBuilder();

			HttpResponse httpResponseParser = null;
			ArrayList<String> responseContent = new ArrayList<>();
			while ((line = in.readLine()) != null) {
				if (line.length() == 0) {
					String mainRequest = responseContent.remove(0);
					httpResponseParser = new HttpResponse(mainRequest,
							responseContent);
					httpResponseParser.parseResponseHeaders();
					httpResponseParser.parseOtherHeaders();
					break;
				}
				responseContent.add(line);

			}

			if (httpResponseParser.code.equals("200")
					&& httpResponseParser.status.equals("OK")) {
				int contentLen = Integer.valueOf(httpResponseParser.headers
						.get("content-length").get(0));
				char[] cbuf = new char[contentLen];
				in.read(cbuf, 0, contentLen);
				for (char c : cbuf) {
					response.append(c);
				}
			} else {
				out.close();
				in.close();
				clientSocket.close();
				return null;

			}

			out.close();
			in.close();
			clientSocket.close();

			if (httpResponseParser.headers.containsKey("content-type")) {
				String contentType = httpResponseParser.headers.get(
						"content-type").get(0);
				if (contentType.contains("XML") || contentType.contains("xml")) {
					DocumentBuilderFactory dbf = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(new ByteArrayInputStream(response
							.toString().getBytes()));
					return doc;
				} else if (contentType.contains("html")
						|| contentType.contains("HTML")) {
					Tidy tidy = new Tidy();
					tidy.setInputEncoding("UTF-8");
					tidy.setOutputEncoding("UTF-8");
					tidy.setWraplen(Integer.MAX_VALUE);
					tidy.setMakeClean(true);
					tidy.setXmlOut(true);
					tidy.setPrintBodyOnly(true);
					Document doc = tidy.parseDOM(new ByteArrayInputStream(
							response.toString().getBytes("UTF-8")),
							new ByteArrayOutputStream());
					return doc;
				}
			} else
				return null;

			return null;

		} catch (UnknownHostException e) {
			return null;

		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;

		} catch (SAXException e) {
			return null;

		} catch (ParserConfigurationException e) {
			return null;
		}
	}
}