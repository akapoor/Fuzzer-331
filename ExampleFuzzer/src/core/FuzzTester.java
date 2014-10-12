package core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FuzzTester {
	
	private URL url;

	public FuzzTester() {
	}
	
	/**
	 * Gets the response code from the given url
	 * @param urlParam
	 * @return
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
			code = connection.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return code;
	}

}
