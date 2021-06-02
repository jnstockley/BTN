//HTTP.java
package com.github.jnstockley;
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
 * Helper class to help with making HTTP get and post requests
 * 
 * @author Jack Stockley
 * 
 * @version 1.0
 *
 */
public class HTTP {

	/**
	 * Makes a simple HTTP get request with no headers
	 * @param url URL to send the HTTP get request
	 * @return HashMap with HTTP status code and HTTP response data
	 */
	protected static HashMap<String, String> get(String url) {
		// Builds the URL and makes sure its valid
		URL obj = null;
		try {
			obj = new URL(url);
		} catch (MalformedURLException e) {
			Logger.logError(Bundle.getString("badURL", url));
		}
		// Builds an HTTP get request with no headers, and sends the request
		HttpURLConnection httpURLConnection = null;
		int responseCode = -1;
		try {
			httpURLConnection = (HttpURLConnection) obj.openConnection();
			httpURLConnection.setRequestMethod("GET");
			responseCode = httpURLConnection.getResponseCode();
		} catch (IOException e) {
			Logger.logError(Bundle.getString("errOpen"));
		}
		// Creates a HashMap to store response code and response data
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("statusCode", Integer.toString(responseCode));
		// Reads HTTP response data, parses it and saves it to HashMap
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			} catch (IOException e) {
			}
			String inputLine;
			StringBuffer strResponse = new StringBuffer();
			try {
				while ((inputLine = in.readLine()) != null) {
					strResponse.append(inputLine);
				}
				in.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("errRead"));
			}
			response.put("data", strResponse.toString());
		}
		return response;
	}

	/**
	 * Makes a simple HTTP get request with headers
	 * @param url URL to send the HTTP get request
	 * @param headers HashMap of headers to be sent with the HTTP get request
	 * @return HashMap with HTTP status code and HTTP response data
	 */
	protected static HashMap<String, String> get(String url, HashMap<String, String> headers) {
		// Builds the URL and makes sure its valid
		URL obj = null;
		try {
			obj = new URL(url);
		} catch (MalformedURLException e) {
			Logger.logError(Bundle.getString("badURL", url));
		}
		// Builds an HTTP get request and adds headers, and sends the request
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
			Logger.logError(Bundle.getString("errOpen"));
		}
		// Creates a HashMap to store response code and response data
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("statusCode", Integer.toString(responseCode));
		// Reads HTTP response data, parses it and saves it to HashMap
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			} catch (IOException e) {
				Logger.logError(Bundle.getString("errRead"));
			}
			String inputLine;
			StringBuffer strResponse = new StringBuffer();
			try {
				while ((inputLine = in.readLine()) != null) {
					strResponse.append(inputLine);
				}
				in.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("errRead"));
			} 
			response.put("data", strResponse.toString());
		}
		return response;
	}

	/**
	 * Makes a simple HTTP post request without headers and JSON data
	 * @param url URL to send the HTTP post request
	 * @param data JSON data to be sent with the post request
	 * @return HashMap with HTTP status code and HTTP response data
	 */
	protected static HashMap<String, String> post(String url, String data) {
		// Builds the URL and makes sure its valid
		URL obj = null;
		try {
			obj = new URL(url);
		} catch (MalformedURLException e) {
			Logger.logError(Bundle.getString("badURL", url));
		}
		// Builds an HTTP post request without headers
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) obj.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
		} catch (IOException e) {
			Logger.logError(Bundle.getString("errOpen"));
		}
		// Ads the data to be sent and send the post request
		OutputStream os = null;
		int responseCode = -1;
		// Checks if no data was added and sends a POST rrequest with no data
		if(data != null) {
			try{
				os = httpURLConnection.getOutputStream();
				byte[] input = data.getBytes("utf-8");
				os.write(input, 0, input.length);
				os.flush();
				os.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("badData"));
			}
		}
		try {
			responseCode = httpURLConnection.getResponseCode();

		} catch(IOException e) {
			Logger.logError(Bundle.getString("badData"));
		}
		// Creates a HashMap to store response code and response data
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("statusCode", Integer.toString(responseCode));
		// Reads HTTP response data, parses it and saves it to HashMap
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			} catch (IOException e) {
				Logger.logError(Bundle.getString("errRead"));
			}
			String inputLine;
			StringBuffer strResponse = new StringBuffer();
			try {
				while ((inputLine = in.readLine()) != null) {
					strResponse.append(inputLine);
				}
				in.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("errRead"));
			} 
			response.put("data", strResponse.toString());
		}
		return response;
	}

	/**
	 * Makes a simple HTTP post request with headers and JSON data
	 * @param url URL to send the HTTP post request
	 * @param data JSON data to be sent with the post request
	 * @param headers HashMap of headers to be sent with the HTTP post request
	 * @return HashMap with HTTP status code and HTTP response data
	 */
	protected static HashMap<String, String> post(String url, String data, HashMap<String, String> headers) {
		// Builds the URL and makes sure its valid
		URL obj = null;
		try {
			obj = new URL(url);
		} catch (MalformedURLException e) {
			Logger.logError(Bundle.getString("badURL", url));
		}
		// Builds an HTTP post request with headers
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) obj.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
		} catch (IOException e) {
			Logger.logError(Bundle.getString("errOpen"));
		}
		Set<String> headerKeys = headers.keySet();
		for(String header: headerKeys) {
			httpURLConnection.setRequestProperty(header, headers.get(header));
		}
		// Ads the data to be sent and send the post request
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
			Logger.logError(Bundle.getString("badData"));
		}
		// Creates a HashMap to store response code and response data
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("statusCode", Integer.toString(responseCode));
		// Reads HTTP response data, parses it and saves it to HashMap
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			} catch (IOException e) {
				Logger.logError(Bundle.getString("errRead"));
			}
			String inputLine;
			StringBuffer strResponse = new StringBuffer();
			try {
				while ((inputLine = in.readLine()) != null) {
					strResponse.append(inputLine);
				}
				in.close();
			} catch (IOException e) {
				Logger.logError(Bundle.getString("errRead"));
			} 
			response.put("data", strResponse.toString());
		}
		return response;
	}
}
