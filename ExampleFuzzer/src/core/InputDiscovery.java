package core;

import java.io.IOException;
import java.net.MalformedURLException;
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

	/**
	 * Discover all inputs and cookies at a particular url
	 * @param webClient
	 * @param url
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public void discover(WebClient webClient, String url) throws IOException, MalformedURLException  {
		
		
		String link = url;//"http://127.0.0.1/dvwa/"+url;
		final HtmlPage page = webClient.getPage(link);
		System.out.println("Input Discovery for: "+ link);
		
		List<HtmlForm> forms = page.getForms(); //list of all forms on a page
		for (HtmlForm form : forms) {
			
			for (HtmlElement e : form.getHtmlElementsByTagName("input"))
				System.out.println("	Input discovered: " + e);

			for (HtmlElement e : form.getHtmlElementsByTagName("textarea"))
				System.out.println("	Input discovered " + e);
			
			for (HtmlElement e : form.getHtmlElementsByTagName("password"))
				System.out.println("	Input discovered " + e);
			
			for (HtmlElement e : form.getHtmlElementsByTagName("text"))
				System.out.println("	Input discovered " + e);
			
			/*
			//Or comment out the for statements above and try this
			final List<HtmlInput> input = (List<HtmlInput>) form.getByXPath("//input");
            for(HtmlInput formInput: input){
                    System.out.println(formInput);
            }
            */
		}
		
		//Find cookies
		for (Cookie cookie : webClient.getCookieManager().getCookies()) {
			System.out.println("	Cookie discovered: "  + cookie.toString());		
		}
	}
}
