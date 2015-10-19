package test.edu.upenn.cis455;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RunAllTests extends TestCase 
{
  public static Test suite() 
  {
    try {
      Class[]  testClasses = {
    		  test.edu.upenn.cis455.XPathDOMParserTest.class,
    		  test.edu.upenn.cis455.XPathServletTest.class,
    		  test.edu.upenn.cis455.XPathValidationTest.class
   
      };   
      
      return new TestSuite(testClasses);
    } catch(Exception e){
      e.printStackTrace();
    } 
    
    return null;
  }
}
