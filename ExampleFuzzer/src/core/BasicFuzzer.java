package core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class BasicFuzzer {
	
	private static ArrayList<HtmlAnchor> onSiteLinks = new ArrayList<HtmlAnchor>();

	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient webClient = new WebClient();
		webClient.setJavaScriptEnabled(true);
		discoverLinks(webClient);
		doFormPost(webClient);
		webClient.closeAllWindows();
	}

	/**
	 * This code is for showing how you can get all the links on a given page, and visit a given URL
	 * @param webClient
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static void discoverLinks(WebClient webClient) throws IOException, MalformedURLException {
		HtmlPage page = webClient.getPage("http://www.se.rit.edu/~swen-331/projects/fuzzer/");
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
}
