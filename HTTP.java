//HTTP.java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

/**
 * 
 * @author Jack Stockley
 * 
 * @version 0.9-beta
 *
 */
public class HTTP {

	/**
	 * 
	 * @param url
	 * @return
	 */
	protected static HashMap<String, String> get(String url) {
		URL obj = null;
		try {
			obj = new URL(url);
		} catch (MalformedURLException e) {
			System.err.println("HTTP.java - " + url + " is not a valid URL!");
			System.exit(1);
		}
		HttpURLConnection httpURLConnection = null;
		int responseCode = -1;
		try {
			httpURLConnection = (HttpURLConnection) obj.openConnection();
			httpURLConnection.setRequestMethod("GET");
			responseCode = httpURLConnection.getResponseCode();
		} catch (IOException e) {
			System.err.println("HTTP.java - error opening HTTP request!");
			System.exit(1);
		}
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("statusCode", Integer.toString(responseCode));
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			} catch (IOException e) {
				System.err.println("HTTP.java - error reading response from HTTP request!");
				System.exit(1);
			}
			String inputLine;
			StringBuffer strResponse = new StringBuffer();
			try {
				while ((inputLine = in.readLine()) != null) {
					strResponse.append(inputLine);
				}
				in.close();
			} catch (IOException e) {
				System.err.println("HTTP.java - error reading response from HTTP request!");
				System.exit(1);
			}
			response.put("data", strResponse.toString());
		}
		return response;
	}

	/**
	 * 
	 * @param url
	 * @param headers
	 * @return
	 */
	protected static HashMap<String, String> get(String url, HashMap<String, String> headers) {
		URL obj = null;
		try {
			obj = new URL(url);
		} catch (MalformedURLException e) {
			System.err.println("HTTP.java - " + url + " is not a valid URL!");
			System.exit(1);
		}
		HttpURLConnection httpURLConnection = null;
		int responseCode = -1;
		try {
			httpURLConnection = (HttpURLConnection) obj.openConnection();
			httpURLConnection.setRequestMethod("GET");
			Set<String> headerKeys = headers.keySet();
			for(String header: headerKeys) {
				httpURLConnection.setRequestProperty(header, headers.get(header));
			}
			responseCode = httpURLConnection.getResponseCode();
		} catch (IOException e) {
			System.err.println("HTTP.java - error opening HTTP request!");
			System.exit(1);
		}
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("statusCode", Integer.toString(responseCode));
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			} catch (IOException e) {
				System.err.println("HTTP.java - error reading response from HTTP request!");
				System.exit(1);
			}
			String inputLine;
			StringBuffer strResponse = new StringBuffer();
			try {
				while ((inputLine = in.readLine()) != null) {
					strResponse.append(inputLine);
				}
				in.close();
			} catch (IOException e) {
				System.err.println("HTTP.java - error reading response from HTTP request!");
				System.exit(1);
			} 
			response.put("data", strResponse.toString());
		}
		return response;
	}

	/**
	 * 
	 * @param url
	 * @param data
	 * @return
	 */
	protected static HashMap<String, String> post(String url, String data) {
		URL obj = null;
		try {
			obj = new URL(url);
		} catch (MalformedURLException e) {
			System.err.println("HTTP.java - " + url + " is not a valid URL!");
			System.exit(1);
		}
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) obj.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
		} catch (IOException e) {
			System.err.println("HTTP.java - error opening HTTP request!");
			System.exit(1);
		}
		OutputStream os = null;
		int responseCode = -1;
		try{
			os = httpURLConnection.getOutputStream();
			byte[] input = data.getBytes("utf-8");
			os.write(input, 0, input.length);
			os.flush();
			os.close();
			responseCode = httpURLConnection.getResponseCode();
		} catch (IOException e) {
			System.err.println("HTTP.java - error adding data to HTTP request!");
			System.exit(1);
		}
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("statusCode", Integer.toString(responseCode));
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			} catch (IOException e) {
				System.err.println("HTTP.java - error reading response from HTTP request!");
				System.exit(1);
			}
			String inputLine;
			StringBuffer strResponse = new StringBuffer();
			try {
				while ((inputLine = in.readLine()) != null) {
					strResponse.append(inputLine);
				}
				in.close();
			} catch (IOException e) {
				System.err.println("HTTP.java - error reading response from HTTP request!");
				System.exit(1);
			} 
			response.put("data", response.toString());
		}
		return response;
	}

	/**
	 * 
	 * @param url
	 * @param data
	 * @param headers
	 * @return
	 */
	protected static HashMap<String, String> post(String url, String data, HashMap<String, String> headers) {
		URL obj = null;
		try {
			obj = new URL(url);
		} catch (MalformedURLException e) {
			System.err.println("HTTP.java - " + url + " is not a valid URL!");
			System.exit(1);
		}
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) obj.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
		} catch (IOException e) {
			System.err.println("HTTP.java - error opening HTTP request!");
			System.exit(1);
		}
		Set<String> headerKeys = headers.keySet();
		for(String header: headerKeys) {
			httpURLConnection.setRequestProperty(header, headers.get(header));
		}
		OutputStream os = null;
		int responseCode = -1;
		try {
			os = httpURLConnection.getOutputStream();
			byte[] input = data.getBytes("utf-8");
			os.write(input, 0, input.length);
			os.flush();
			os.close();
			responseCode = httpURLConnection.getResponseCode();

		}catch (IOException e) {
			System.err.println("HTTP.java - error adding data to HTTP request!");
			System.exit(1);
		}
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("statusCode", Integer.toString(responseCode));
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			} catch (IOException e) {
				System.err.println("HTTP.java - error reading response from HTTP request!");
				System.exit(1);
			}
			String inputLine;
			StringBuffer strResponse = new StringBuffer();
			try {
				while ((inputLine = in.readLine()) != null) {
					strResponse.append(inputLine);
				}
				in.close();
			} catch (IOException e) {
				System.err.println("HTTP.java - error reading response from HTTP request!");
				System.exit(1);
			} 
			response.put("data", response.toString());
		}
		return response;
	}
}
