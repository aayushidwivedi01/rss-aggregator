package test.edu.upenn.cis455;

/**
 * Tests doPost() method in XPathServlet class
 * Uses EasyMock to mock the servlet's behavior
 * A valid XPath present in http://www.w3schools.com/xml/note.xml 
 * has been used
 */
import static org.junit.Assert.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;

import edu.upenn.cis455.servlet.XPathServlet;


public class XPathServletTest extends TestCase{

	@Test
	public void testXPathServletdoPost() throws Exception{
		
		HttpServletRequest request = EasyMock.mock(HttpServletRequest.class);
		HttpServletResponse response = EasyMock.mock(HttpServletResponse.class);
		
		expect(request.getParameter("xpath")).andReturn("/note/to");
		expect(request.getParameter("url")).andReturn("http://www.w3schools.com/xml/note.xml");
		replay(request);
		StringBuilder html = new StringBuilder();
		
		PrintWriter out = new PrintWriter("htmlOutput.txt");
		expect(response.getWriter()).andReturn(out);
		response.setContentType("text/html");
		EasyMock.expectLastCall();
		
		
		response.flushBuffer();
		EasyMock.expectLastCall();
		replay(response);
		XPathServlet xpathServlet = new XPathServlet();
		xpathServlet.doPost(request, response);
		
		out.flush();
		out.close();
		
		BufferedReader br = new BufferedReader(new FileReader("htmlOutput.txt"));
		String line;
		StringBuilder output = new StringBuilder();
		
		while ((line = br.readLine()) != null){
			output.append(line);
		}
		
		String actual = "<html><body>Success:/note/to</br></body></html>";
		assertTrue(output.toString().equals(actual));
		
		
		
	}

}
