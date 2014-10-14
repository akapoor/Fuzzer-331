package core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Fuzz testing class.
 * Checks for http response code
 * @author Anshul
 *
 */
public class FuzzTester {
	
	private URL url;

	public FuzzTester() {
	}
	
	/**
	 * Gets the response code from the given url
	 * @param urlParam url 
	 * @return int response code
	 */
	public int getHttpResponse(String urlParam) {
		int code = 0;
		try {
			url = new URL(urlParam);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			code = connection.getResponseCode(); //get http response code
		} catch (IOException e) {
			e.printStackTrace();
		}
		return code;
	}
	
	/**
	 * Check for the page load time.
	 * If the response takes longer than a pre-defined threshold, 
	 * then there's a potential of a denial-of-service vulnerablity.
	 * @param urlParam input url
	 * @return boolean 
	 */
	public boolean checkResponseTime(HtmlPage page){
		boolean DoSExist = false;
		double avgTime = 6.5; // mean page load time
		
		double loadTime = (double)page.getWebResponse().getLoadTime()/1000;
		
		if(loadTime > avgTime){
			DoSExist = true;
			System.out.print("	Load time: "+loadTime + " sec. "); //load time will be printed if it takes long time
		}
		return DoSExist;
	}

}
