package core;

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
	
	private static ArrayList<HtmlAnchor> onSiteLinks = new ArrayList<HtmlAnchor>();
	private static String url;

	public static void main(String[] args) throws MalformedURLException, IOException {
		WebClient webClient = new WebClient();
		webClient.setJavaScriptEnabled(true);
		String[] input = getInput();
		int inputType = getInputType(input);
		url = getWebsite(webClient, input);
		if (inputType == 0) {
			System.err.println("That is an invalid input");
			System.exit(0);
		}
		else if (inputType == 1) {
			discoverLinks(webClient);
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
	private static void discoverLinks(WebClient webClient) throws IOException, MalformedURLException {
		HtmlPage page = webClient.getPage(url);
		List<HtmlAnchor> links = page.getAnchors();
		for (HtmlAnchor link : links) {
			if (!link.getHrefAttribute().startsWith("http")) {		//Change links for different 
				System.out.println("Link discovered: " + link.asText() + " @URL=" + link.getHrefAttribute());
				onSiteLinks.add(link);
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
	
	private static String[] getInput() {
		Scanner s = new Scanner(System.in);
        String inputString = s.nextLine();
        return inputString.split(" ");
        
	}
	
	/**
	 * This code finds the console input and determines if the fuzzer needs to discover or test the site
	 * @return inputType
	 */
	private static int getInputType(String [] input) {
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
	
	private static String getWebsite(WebClient webClient, String[] input) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = webClient.getPage(input[2]);
		return input[2];
	}
	
}
