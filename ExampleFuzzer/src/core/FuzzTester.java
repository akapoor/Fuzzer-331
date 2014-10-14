package core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Fuzz testing class.
 * Checks for http response code
 * @author Anshul
 *
 */
public class FuzzTester {
	
	private URL url;
	private ArrayList<HtmlInput> inputList = new ArrayList<HtmlInput>();

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
	
	/**
	 * Fuzz a page with exploit vectors from vectors.txt
	 * @param page
	 */
	public void fuzzInputs(InputDiscovery inputDisc, HtmlPage page, String fileName){
		inputList = inputDisc.getInputs();
		//System.out.println(inputList);
		
		try {			
			Scanner in = new Scanner(new FileReader(fileName));
			System.out.println("	Fuzzing Vectors at "+ page);
			if(inputList.size() > 0){
				while (in.hasNext()) {
					String vector = in.next();
					System.out.println(inputList);
					for(HtmlInput input : inputList){
						input.setValueAttribute(vector);
					}
					inputDisc.getSubmitButton().click();
				}
				System.out.println("		No vulnerabilities found");
			}
			else
				System.out.println("		No valid inputs discovered");
			
			in.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	public void checkDataLeak(HtmlPage page, String fileName){
		Scanner in;
		try {
			in = new Scanner(new FileReader(fileName));
			System.out.println("	Sensitive Data Leak Check at "+ page);
			boolean check = false;
			while(in.hasNext()){
				String word = in.next();
				if(page.asText().contains(word)){
					System.out.println("		Data leak found. "+ word +" is disclosed.");
					check = true;
				}
									
			}
			if(!check)
				System.out.println("		No data leaks found");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
