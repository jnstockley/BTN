package com.github.jnstockley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;


/**
 *
 * Function that manages setting up the failover method and
 * building the failover message to be sent
 *
 * @author Jack Stockley
 *
 * @version 1.6
 *
 */
public class Failover {

	/**
	 *  Name of the Class used for Logging error
	 */
	private static final String CLASSNAME = Failover.class.getName();

	/**
	 *  The name of the person the failover notification is sent to
	 */
	private String toName;

	/**
	 *  The email, or text, the failover notification is sent to
	 */
	private String toEmail;

	/**
	 * The name of the person the failover notification is coming from
	 */
	private String fromName;

	/**
	 *  The email address the failover notification is coming from
	 */
	private String fromEmail;

	/**
	 *  The SMTP server used to send the failover notification
	 */
	private String smtpServer;

	/**
	 *  The SMTP password of the account to send the notification from
	 */
	private String smtpPassword;

	/**
	 *  The SMTP port for the SMTP server
	 */
	private int smtpPort;

	/**
	 * Get the name of the person to send the failover notification to
	 * @return The name of the person getting the failover notification
	 */
	public String getToName() {
		return toName;
	}

	/**
	 * Sets the name of the person to send the failover notification to
	 * @param toName The name of the person getting the failover notification
	 */
	public void setToName(String toName) {
		this.toName = toName;
	}

	/**
	 * Get the email of the person to send the failover notification to
	 * @return The email of the person getting the failover notification
	 */
	public String getToEmail() {
		return toEmail;
	}

	/**
	 * Sets the email of the person to send the failover notification to
	 * @param toEmail The email of the person getting the failover notification
	 */
	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	/**
	 * Get the name of the person sending the failover notification
	 * @return The name of the person sending the failover notification
	 */
	public String getFromName() {
		return fromName;
	}

	/**
	 * Sets the name of the person sending the failover notification
	 * @param fromName The name of the person sending the failover notification
	 */
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	/**
	 * Get the email used to send the failover notification
	 * @return The email address used to send the failover notification
	 */
	public String getFromEmail() {
		return fromEmail;
	}

	/**
	 * Sets the email used to send the failover notification
	 * @param fromEmail The email address used to send the failover notification
	 */
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	/**
	 * Get the SMTP server used to send the failover notification
	 * @return The SMTP server used to send the failover notification
	 */
	public String getSmtpServer() {
		return smtpServer;
	}

	/**
	 * Sets the SMTP server used to send the failover notification
	 * @param smtpServer The SMTP server used to send the failover notification
	 */
	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	/**
	 * Get the password of the email account used to send the failover notification
	 * @return The password of the email account used to send the failover notification
	 */
	public String getSmtpPassword() {
		return smtpPassword;
	}

	/**
	 * Set the password of the email account used to send the failover notification
	 * @param smtpPassword The password of the email account used to send the failover notification
	 */
	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	/**
	 * Get the port of the SMTP server used to send the failover notification
	 * @return The port of the SMTP server used to send the failover notification
	 */
	public int getSmtpPort() {
		return smtpPort;
	}

	/**
	 * Sets the port of the SMTP server used to send the failover notification
	 * @param smtpPort The port of the SMTP server used to send the failover notification
	 */
	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	/**
	 * Makes sure the data in the JSON config file is valid and
	 * build a Failover object to send failover notifications
	 * @param file The JSON config file which stores the configuration settings for BTTN
	 */
	public Failover(File file) {
		// Build the JSONObject and get data from config file
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(file));
		} catch (IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Makes sure JSONObjecthas failover settings
		if(json.containsKey("failover")) {
			JSONObject failover = (JSONObject) json.get("failover");
			toName = failover.get("toName").toString();
			toEmail = failover.get("toEmail").toString();
			fromName = failover.get("fromName").toString();
			fromEmail = failover.get("fromEmail").toString();
			smtpServer = failover.get("smtpServer").toString();
			smtpPassword = failover.get("smtpPassword").toString();
			smtpPort = Integer.parseInt(failover.get("smtpPort").toString());
		}
	}

	/**
	 * Builds the message to be sent and sends the failover notification
	 * @param subject The subject line for the failover notification
	 * @param message The body of the failover notification
	 * @param link A link to either the twitch stream(s) or to view the new update,
	 * otherwise link won't be added to message
	 */
	public void sendFailover(String subject, String message, String link) {
		// Checks if failover is enabled
		if(failoverEnabled()) {
			String HTMLmessage = "";
			// Link contains a twitch link and builds the appropriate message
			if(link.contains("twitch")) {
				HTMLmessage = "<p>" + message + "</p><br><a href=" + link + ">" + Bundle.getBundle("openStream") +"</a>";
				// Link contains a GitHub link and builds the appropriate message
			} else if(link.contains("github")) {
				HTMLmessage = "<p>" + message + "</p><br><a href=" + link + ">" + Bundle.getBundle("viewUpdate") +"</a>";
				// Link is not a valid link, so it's not included in the message
			} else {
				HTMLmessage = "<p>" + message + "</p>";
			}
			// Builds the mailer and email and sends the notification
			Mailer mailer = MailerBuilder
					.withSMTPServer(this.smtpServer, this.smtpPort, this.fromEmail, this.smtpPassword)
					.buildMailer();
			Email email = EmailBuilder.startingBlank()
					.from(this.fromEmail, this.fromEmail)
					.to(this.toName, this.toEmail)
					.withSubject(Bundle.getBundle("failoverSubject", message))
					.withPlainText(message)
					.withHTMLText(HTMLmessage)
					.buildEmail();
			mailer.sendMail(email);
			Logging.logWarn(CLASSNAME, Bundle.getBundle("failoverSent") + " <" + subject + "> " + message);
		} else {
			Logging.logWarn(CLASSNAME, Bundle.getBundle("failoverNotEnabled") + " <" + subject + "> " + message);
		}

	}

	/**
	 * Helper function to add the required failover data to the JSON config file
	 * @param reader BufferedReader used to get user input from the console
	 */
	@SuppressWarnings("unchecked")
	public static void addFailover(BufferedReader reader) {
		// Asks if the failover notification is sent to an email or a phone number
		System.out.print(Bundle.getBundle("failoverTextEmail"));
		String method = "";
		try {
			method = reader.readLine();
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		String failoverAddr = "";
		// Failover is sent to a phone number as a text
		if(method.equalsIgnoreCase(Bundle.getBundle("text"))) {
			// Gets the phone number to send the notification to
			System.out.print(Bundle.getBundle("enterPhoneNumber"));
			String number = "";
			try {
				number = reader.readLine();
			} catch (IOException e) {
				Logging.logError(CLASSNAME, e);
			}
			// Checks if phone number is valid
			if(!validPhoneNumber(number)) {
				Logging.logError(CLASSNAME, Bundle.getBundle("badPhoneNumber", number));
			}
			// List of pre-defined supported cell providers which makes it easier for the user to add
			HashMap<String, String> providers = new HashMap<String, String>();
			providers.put("AT&T", "@txt.att.net");
			providers.put("Boost Mobile", "@sms.myboostmobile.com");
			providers.put("Cricket Wireless", "@mms.cricketwireless.net");
			providers.put("Google Project Fi", "@msg.fi.google.com");
			providers.put("Republic Wireless", "@text.republicwireless.com");
			providers.put("Sprint", "@messaging.sprintpcs.com");
			providers.put("Straight Talk / Verizon", "@vtext.com");
			providers.put("T-Mobile", "@tmomail.net");
			providers.put("Ting", "@message.ting.com");
			providers.put("Tracfone", "@mmst5.tracfone.com");
			providers.put("U.S. Cellular", "@email.uscc.net");
			providers.put("Virgin Mobile", "@vmobl.com");
			// Adds an option for the user to add their own custom cell provider
			providers.put(Bundle.getBundle("customProvider"), null);
			// Asks the user which provider they want to use
			System.out.println(Bundle.getBundle("selectProvider"));
			List<String> providerNames = new ArrayList<String>();
			providerNames.addAll(providers.keySet());
			Collections.sort(providerNames);
			// Prints out all providers sorted alphabetically
			for(int i=0; i<providerNames.size(); i++) {
				int temp = i + 1;
				System.out.println(temp + ". " + providerNames.get(i));
			}
			// User enters their selected provider
			System.out.print(Bundle.getBundle("provider"));
			int option = -1;
			try {
				option = Integer.parseInt(reader.readLine()) - 1;
			} catch (NumberFormatException | IOException e) {
				Logging.logError(CLASSNAME, e);
			}
			String suffix = "";
			// Allows user to enter custom provider and checks to make sure it's valid
			if(providerNames.get(option).equalsIgnoreCase(Bundle.getBundle("customProvider"))) {
				// Asks user to enter their providers email extension
				System.out.print(Bundle.getBundle("cellProviderExtension"));
				Pattern pattern = Pattern.compile("@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
				try {
					suffix = reader.readLine();
				} catch (IOException e) {
					Logging.logError(CLASSNAME, e);
				}
				// Makes sure domain is a valid domain
				Matcher matcher = pattern.matcher(suffix);
				if(!matcher.matches()) {
					Logging.logError(CLASSNAME, Bundle.getBundle("badSuffix"));
				}
			} else {
				suffix = providers.get(providerNames.get(option));
			}
			failoverAddr = number + suffix;
			// Failover is sent to a phone number as a text
		} else if(method.equalsIgnoreCase(Bundle.getBundle("email"))) {
			// Asks user to enter their email address to send the notification to
			System.out.print(Bundle.getBundle("enterEmailAddress"));
			try {
				failoverAddr = reader.readLine();
				// Makes sure the entered email address is valid
				if(!validEmail(failoverAddr)) {
					Logging.logError(CLASSNAME, Bundle.getBundle("badEmail"));
				}
			} catch (IOException e) {
				Logging.logError(CLASSNAME, e);
			}
			// Invalid method selected
		} else {
			Logging.logError(CLASSNAME, Bundle.getBundle("invalidOption", method));
		}
		// Asks user to enter their name
		System.out.print(Bundle.getBundle("toName"));
		String toName = "";
		try {
			toName = reader.readLine();
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Asks user to enter the email address the notifications are sent from
		System.out.print(Bundle.getBundle("fromEmail"));
		String fromEmail = "";
		try {
			fromEmail = reader.readLine();
			// Makes sure the email address is valid
			if(!validEmail(fromEmail)) {
				Logging.logError(CLASSNAME, Bundle.getBundle("badEmail"));
			}
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Asks user to enter the password for the email address sending the notification
		System.out.print(Bundle.getBundle("smtpPassword"));
		String smtpPassword = "";
		try {
			smtpPassword = reader.readLine();
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Asks user to enter the SMTP server of the email account
		System.out.print(Bundle.getBundle("smtpServer"));
		String smtpServer = "";
		try {
			smtpServer = reader.readLine();
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Asks the user to enter the SMTP port of the email account
		System.out.print(Bundle.getBundle("smtpPort"));
		int smtpPort = -1;
		try {
			smtpPort = Integer.parseInt(reader.readLine());
		} catch (NumberFormatException | IOException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Builds the JSONObject that hold the failover configuration
		JSONObject failover = new JSONObject();
		failover.put("toEmail", failoverAddr);
		failover.put("toName", toName);
		failover.put("fromEmail", fromEmail);
		failover.put("fromName", "BTTN");
		failover.put("smtpPassword", smtpPassword);
		failover.put("smtpServer", smtpServer);
		failover.put("smtpPort", smtpPort);
		// Reads the current JSON config file
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(BTTN.configFile));
		} catch (IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Adds the failover config and overwrites the old one, if present and writes it to the file
		json.put("failover", failover);
		try {
			FileWriter writer = new FileWriter(BTTN.configFile);
			writer.write(json.toJSONString());
			writer.flush();
			writer.close();
			Logging.logInfo(CLASSNAME, Bundle.getBundle("failoverEnabled"));
		} catch (IOException e) {
			Logging.logError(CLASSNAME, e);
		}
	}

	/**
	 * Helper function to remove the failover settings
	 * @param reader BufferedReader used to get user input from the console
	 */
	public static void removeFailover(BufferedReader reader) {
		// Builds the JSONObject that holds the current failover settings
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		try {
			json = (JSONObject) parser.parse(new FileReader(BTTN.configFile));
		} catch (IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Makes sure the JSON config file contains failover settings and removes it then writes changes
		if(json.containsKey("failover")) {
			json.remove("failover");
			FileWriter writer;
			try {
				writer = new FileWriter(BTTN.configFile);
				writer.write(json.toJSONString());
				writer.flush();
				writer.close();
				Logging.logInfo(CLASSNAME, Bundle.getBundle("failoverRemoved"));
			} catch (IOException e) {
				Logging.logError(CLASSNAME, e);
			}
			// JSON config file does not contain failover settings
		} else {
			Logging.logWarn(CLASSNAME, Bundle.getBundle("failoverDisabled"));
		}
	}

	/**
	 * Helper function that uses regex to make sure an email address is valid
	 * @param email The email address to check if valid
	 * @return True if valid, otherwise false
	 */
	private static boolean validEmail(String email) {
		Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * Helper function that uses regex to make sure a phone number is valid
	 * @param phoneNumber The phone number to check if valid
	 * @return True if valid, otherwise false
	 */
	private static boolean validPhoneNumber(String phoneNumber) {
		Pattern pattern = Pattern.compile("^\\d{10}$");
		Matcher matcher = pattern.matcher(phoneNumber);
		return matcher.matches();
	}

	/**
	 * Checks if failover is enable and returns true otherwise returns false
	 * @return True if failover is enabled, otherwise false
	 */
	private static boolean failoverEnabled() {
		// Read the JSON data from the JSON config file
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			json = (JSONObject) parser.parse(new FileReader(BTTN.configFile));
		} catch (IOException | ParseException e) {
			Logging.logError(CLASSNAME, e);
		}
		// Check if failover is enabled and return True if enabled
		if(json.containsKey("failover")) {
			JSONObject failover = (JSONObject) json.get("failover");
			return failover.containsKey("toEmail");
		} else {
			return false;
		}
	}
}