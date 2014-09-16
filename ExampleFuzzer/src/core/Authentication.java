package core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Handle authentication into the two web applications
 * @author Anshul
 *
 */
public class Authentication {

	/**
	 * Use hardcoded user name and password to log into DVWA
	 * @param webClient
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public void submittingForm(WebClient webClient) throws IOException, MalformedURLException  {
	    
	    // Get the first page
	    final HtmlPage page1 = webClient.getPage("http://127.0.0.1/dvwa/login.php");

	    // Get the form that we are dealing with and within that form, 
	    // find the submit button and the field that we want to change.
	    
	    final List<HtmlForm> fList = page1.getForms();
	    
	    final HtmlForm form = fList.get(0);

	    final HtmlSubmitInput button = form.getInputByName("Login");
	    final HtmlTextInput userField = form.getInputByName("username");
	    final HtmlPasswordInput passwordField = form.getInputByName("password");
	    
	    // Change the value of the text field
	    userField.setValueAttribute("admin");
	    passwordField.setValueAttribute("password");

	    // Now submit the form by clicking the button and get back the second page.
	    final HtmlPage page2 = button.click();
	    System.out.println("************************Logged in*****************************");
		List<HtmlAnchor> links = page2.getAnchors();
		for (HtmlAnchor link : links) {
			System.out.println("Link discovered: " + link.asText() + " @URL=" + link.getHrefAttribute());
		}	
	}
	
}
