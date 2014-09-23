package core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class BasicFuzzer {
	
	private static ArrayList<String> onSiteLinks = new ArrayList<String>();
	private static HtmlPage page;

	public static void main(String[] args) throws MalformedURLException, IOException {
		WebClient webClient = new WebClient();
		webClient.setJavaScriptEnabled(true);
		int inputType = getInputType(args);
		getWebsite(webClient, args);
		if (inputType == 0) {
			System.err.println("That is an invalid input");
			System.exit(0);
		}
		else if (inputType == 1) {
			discoverLinks();
			useRemainingParams(args, webClient);
		}
		doFormPost(webClient);
		Authentication auth = new Authentication();
		auth.submittingForm(webClient);
		webClient.closeAllWindows();
	}

	/**
	 * This code is for showing how you can get all the links on a given page, and visit a given URL
	 * @param webClient
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static void discoverLinks() throws IOException, MalformedURLException {
		List<HtmlAnchor> links = page.getAnchors();
		for (HtmlAnchor link : links) {
			if (!link.getHrefAttribute().startsWith("http")) {		//Change links for different 
				System.out.println("Link discovered: " + link.asText() + " @URL=" + link.getHrefAttribute());
				onSiteLinks.add(link.getHrefLangAttribute());
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
	
	private static HtmlPage getWebsite(WebClient webClient, String[] input) throws FailingHttpStatusCodeException, IOException {
		page = webClient.getPage(input[2]);
		return page;
	}
	
	private static void useRemainingParams(String[] args, WebClient webClient) {
		for (String arg : args) {
			if (arg.startsWith("--common-words=")) {
				pageGuessingWithCommonWords(arg.substring(15), webClient);
			}
		}
	}
	
	private static void pageGuessingWithCommonWords(String fileName, WebClient webClient)  {
		try {
			String pageUrl = page.getUrl().toString();
			pageUrl = pageUrl.substring(0, pageUrl.lastIndexOf('/') + 1);
			Scanner in = new Scanner(new FileReader(fileName));
			while (in.hasNext()) {
				String word = in.next();
				String newUrl = pageUrl + word;
				if (checkUrl(newUrl, webClient)) {
					System.out.println("Unlinked Page Found: @URL=" + newUrl);
				}
				String phpUrl = newUrl + ".php";
				if (checkUrl(phpUrl, webClient)) {
					System.out.println("Unlinked Page Found: @URL=" + phpUrl);
				}
				String jspUrl = newUrl + ".jsp";
				if (checkUrl(jspUrl, webClient)) {
					System.out.println("Unlinked Page Found: @URL=" + jspUrl);
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
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
	
}
