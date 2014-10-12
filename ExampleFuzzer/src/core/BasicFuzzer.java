package core;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class BasicFuzzer {
	
	private static ArrayList<HtmlPage> onSiteLinks = new ArrayList<HtmlPage>();
	private static ArrayList<HtmlPage> visitedPages = new ArrayList<HtmlPage>();
	
	static HtmlAnchor dwvaLink = null;
	static HtmlPage homePage = null;

	public static void main(String[] args) throws MalformedURLException, IOException {
		WebClient webClient = new WebClient();
		webClient.setJavaScriptEnabled(true);
		int inputType = getInputType(args);
		webClient.getCookieManager().clearCookies();
		
		HtmlPage page = getWebsite(webClient, args); //construct HtmlPage from args
		
		//***********************Discovery Links******************
		if (inputType == 0) {
			System.err.println("That is an invalid input");
			System.exit(0);
		}
		else if (inputType == 1) {
			System.out.println("Crawling page: http://127.0.0.1/");
			discoverLinks(webClient, page);
			//useRemainingParams(args, webClient, page);
		}
				
		//************************Authenticate*******************
		//Get the name of application to authenticate
		String appName = "";
		for(int i=0; i< args.length; i++){
			if(args[i].contains("auth")){
				appName = args[i].substring(14);
			}	
		}
		logIn(webClient, appName, (HtmlPage)dwvaLink.click());
		
		
		//************************Input Discovery*****************
		//String path = "http://127.0.0.1/dvwa/vulnerabilities/sqli/";
		InputDiscovery discoverInputs = new InputDiscovery();
		//discoverInputs.discover(webClient, path);
		
		onSiteLinks.remove(0);
		//**********************Once log in, crawl through links on home page*********************
		ListIterator<HtmlPage> itr = onSiteLinks.listIterator();
		while(itr.hasNext()){
			HtmlPage htmlPage = itr.next();
			if(!visitedPages.contains(htmlPage)) {
				System.out.println("\nCrawling page: " + htmlPage);
				visitedPages.add(htmlPage);
				discoverLinks(webClient, htmlPage);
				discoverInputs.discover(webClient, htmlPage);
				//useRemainingParams(args, webClient, htmlPage);
			}
			System.out.println("On site page " + onSiteLinks.size());
		}
		
		/*for (HtmlPage htmlPage : onSiteLinks) {
			
		}*/
		doFormPost(webClient);
		webClient.closeAllWindows();
	}

	/**
	 * This code is for showing how you can get all the links on a given page, and visit a given URL
	 * @param webClient
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static void discoverLinks(WebClient webClient, HtmlPage page) throws IOException, MalformedURLException {
		List<HtmlAnchor> links = page.getAnchors();
		System.out.println("	Link Discovery at "+ page);
		for (HtmlAnchor link : links) {
			if( link.getHrefAttribute().equals("http://127.0.0.1/dvwa")){
				System.out.println("		Link discovered: " + link.asText() + " @URL=" + link.getHrefAttribute());
				dwvaLink = link;
			}
			else if(!link.getHrefAttribute().contains("http://")){
				System.out.println("		Link discovered: " + link.asText() + " @URL=" + link.getHrefAttribute());
				HtmlPage page1 = webClient.getPage("http://127.0.0.1/dvwa/"+link.getHrefAttribute());
				onSiteLinks.add(page1);
			}
		}
	}

	/**
	 * This code is for demonstrating techniques for submitting an HTML form. Fuzzer code would need to be
	 * more generalized
	 * @param webClient
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static void doFormPost(WebClient webClient) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = webClient.getPage("http://www.se.rit.edu/~swen-331/projects/fuzzer/");
		List<HtmlForm> forms = page.getForms();
		for (HtmlForm form : forms) {
			HtmlInput input = form.getInputByName("quantity");
			input.setValueAttribute("2");
			HtmlSubmitInput submit = (HtmlSubmitInput) form.getFirstByXPath("//input[@id='submit']");
			System.out.println(submit.<HtmlPage> click().getWebResponse().getContentAsString());
		}
	}
	
	/**
	 * This code finds the console input and determines if the fuzzer needs to discover or test the site
	 * @return inputType
	 */
	private static int getInputType(String[] input) {
		int inputType = 0;
		if (!input[0].equals("fuzz")) {
        	return inputType;
        }
        if (input[1].equals("discover")) {
        	inputType = 1;
        }
        else if (input[2].equals("test")) {
        	inputType = 2;
        }
        return inputType;
	}
	
	/**
	 * Get the website page
	 * @param webClient
	 * @param input
	 * @return
	 * @throws FailingHttpStatusCodeException
	 * @throws IOException
	 */
	private static HtmlPage getWebsite(WebClient webClient, String[] input) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = webClient.getPage(input[2]);
		onSiteLinks.add(page);
		//visitedPages.add(page);
		return page;
	}
	
	/**
	 * Call pageGuessing With Commands
	 * @param args
	 * @param webClient
	 * @param page
	 */
	private static void useRemainingParams(String[] args, WebClient webClient, HtmlPage page) {
		for (String arg : args) {
			if (arg.startsWith("--common-words=")) {
				pageGuessingWithCommonWords(arg.substring(15), webClient, page);
			}
		}
	}
	
	/**
	 * Use words from words.txt and guess unlinked pages
	 * @param fileName
	 * @param webClient
	 * @param page
	 */
	private static void pageGuessingWithCommonWords(String fileName, WebClient webClient, HtmlPage page)  {
		try {
			OutputStream output = new FileOutputStream("NUL:");
			PrintStream nullOut = new PrintStream(output);
			System.setErr(nullOut);
			
			String pageUrl = page.getUrl().toString();
			pageUrl = pageUrl.substring(0, pageUrl.lastIndexOf('/') + 1);
			Scanner in = new Scanner(new FileReader(fileName));
			System.out.println("	Page Guessing at "+ page);
			while (in.hasNext()) {
				String word = in.next();
				String newUrl = pageUrl + word;
				if (checkUrl(newUrl, webClient)) {
					HtmlPage newPage = webClient.getPage(newUrl); //print error if no page found
					//onSiteLinks.add(newPage);
					System.out.println("		Unlinked Page Found: @URL=" + newUrl);
				}
				String phpUrl = newUrl + ".php";
				if (checkUrl(phpUrl, webClient)) {
					HtmlPage newPage = webClient.getPage(phpUrl);
					//onSiteLinks.add(newPage);
					System.out.println("		Unlinked Page Found: @URL=" + phpUrl);
				}
				String jspUrl = newUrl + ".jsp";
				if (checkUrl(jspUrl, webClient)) {
					HtmlPage newPage = webClient.getPage(jspUrl);
					//onSiteLinks.add(newPage);
					System.out.println("		Unlinked Page Found: @URL=" + jspUrl);
				}
			}
			in.close();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Check if onsite contains url
	 * @param pageUrl
	 * @param webClient
	 * @return
	 */
	private static boolean checkUrl(String pageUrl, WebClient webClient) {
		try {
			HtmlPage pageTest = webClient.getPage(pageUrl);
			if (onSiteLinks.contains(pageTest.getUrl().toString())) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * LogInto application
	 * @param webClient
	 * @param name
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static void logIn(WebClient webClient, String name, HtmlPage pageToLogin) throws IOException, MalformedURLException  {
	    
			// Get the first page
		    //final HtmlPage page1 = webClient.getPage("http://127.0.0.1/dvwa/login.php");

		    // Get the form that we are dealing with and within that form, 
		    // find the submit button and the field that we want to change.
		    
		    final List<HtmlForm> fList = pageToLogin.getForms();
		    
		    final HtmlForm form = fList.get(0);

		    final HtmlSubmitInput button = form.getInputByName("Login");
		    final HtmlTextInput userField = form.getInputByName("username");
		    final HtmlPasswordInput passwordField = form.getInputByName("password");
		    
		    // Change the value of the text field
		    userField.setValueAttribute("admin");
		    passwordField.setValueAttribute("password");

		    
		    // Now submit the form by clicking the button and get back the second page.
		    final HtmlPage page2 = button.click();
		    		    
		    System.out.println("Logged into "+ name);
		    homePage = page2;
		    onSiteLinks.add(page2);
		   
	}
	
}
