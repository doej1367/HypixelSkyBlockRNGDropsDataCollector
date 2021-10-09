package util;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * 
 * @author doej1367
 */
public class GoogleFormApi {
	private String form_id;
	private StringBuilder postData = new StringBuilder();

	public GoogleFormApi(String form_id) {
		this.form_id = form_id;
	}

	public void put(int id, String value) {
		if (postData.length() > 0)
			postData.append("&");
		postData.append("entry.");
		postData.append(id);
		postData.append("=");
		String data = value.replaceAll(",", "%2C").replaceAll("\\+", "%2B").replaceAll("'", "%27").replaceAll(" ", "+");
		postData.append(data);
	}

	public boolean sendData() {
		try {
			URL url = new URL("https://docs.google.com/forms/d/e/" + form_id + "/formResponse?" + postData);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			return urlConnection.getResponseCode() == 200;
		} catch (IOException ignored) {
		}
		return false;
	}
}
