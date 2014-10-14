package core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.Cookie;

public class InputDiscovery {
	
	private ArrayList<HtmlInput> inputs = new ArrayList<HtmlInput>();
	
	/**
	 * Discover all inputs and cookies at a particular url
	 * @param webClient
	 * @param url
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public void discover(WebClient webClient, HtmlPage page) throws IOException, MalformedURLException  {
		
		System.out.println("	Input Discovery at "+ page);
		
		List<HtmlForm> forms = page.getForms(); //list of all forms on a page
		for (HtmlForm form : forms) {
			
			for (HtmlElement e : form.getHtmlElementsByTagName("input")) {
				System.out.println("		Input discovered: " + e);
			}
			for (HtmlElement e : form.getHtmlElementsByTagName("textarea")) {
				System.out.println("		Input discovered " + e);
				HtmlTextInput eIn = (HtmlTextInput)e;
				inputs.add(eIn);
			}
			for (HtmlElement e : form.getHtmlElementsByTagName("password")) {
				System.out.println("		Input discovered " + e);
				HtmlPasswordInput eIn = (HtmlPasswordInput)e;
				inputs.add(eIn);
			}
			for (HtmlElement e : form.getHtmlElementsByTagName("text")) {
				System.out.println("		Input discovered " + e);
				HtmlTextInput eIn = (HtmlTextInput)e;
				inputs.add(eIn);
			}
		}
		
		//Find cookies
		for (Cookie cookie : webClient.getCookieManager().getCookies()) {
			System.out.println("		Cookie discovered: "  + cookie.toString());		
		}
	}
	
	public ArrayList<HtmlInput> getInputs() {
		return inputs;
	}
}
