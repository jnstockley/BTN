import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

public class BTN {
	
	@SuppressWarnings("unchecked")
	private static void addChannels(String filepath, BufferedReader reader) throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONObject channels = (JSONObject) parser.parse(new FileReader(filepath));
		int oldSize = channels.size();
		System.out.print("Enter a channel to get notifications for, seperate each channel by a comma: ");
		List<String> newChannels = Arrays.asList(reader.readLine().split("\\s*,\\s*"));
		for(String channel: newChannels) {
			if(!channels.containsKey(channel)) {
				channels.put(channel, false);
			}
		}
		if(channels.size() != oldSize) {
			FileWriter writer = new FileWriter(filepath);
			writer.write(channels.toJSONString());
			writer.flush();
			writer.close();
			System.exit(0);
		} else {
			System.out.println("No new channels added, file remains the same!");
			System.exit(0);
		}
	}
	
	private static void removeChannels(String filepath, BufferedReader reader) throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONObject channels = (JSONObject) parser.parse(new FileReader(filepath));
		int oldSize = channels.size();
		System.out.println("Enter the number(s) of the channels you want to no longer recieve notifications for, sperate each index by a comma:");
		int index = 1;
		Set<String> channelSet = channels.keySet();
		List<String> channelNames = new ArrayList<String>();
		for(Object channel: channelSet) {
			channelNames.add(channel.toString());
		}
		Collections.sort(channelNames);
		for(String channel: channelNames) {
			System.out.println(index + ": " + channel);
			index++;
		}
		System.out.print("Channels: ");
		List<String> indexs = Arrays.asList((reader.readLine().split("\\s*,\\s*")));
		for(String channelIndex: indexs) {
			channels.remove(channelNames.get(Integer.parseInt(channelIndex)-1));
		}
		if(channels.size() != oldSize) {
			FileWriter writer = new FileWriter(filepath);
			writer.write(channels.toJSONString());
			writer.flush();
			writer.close();
			System.exit(0);
		} else {
			System.out.println("No channels selected to be removed, file remains the same!");
			System.exit(0);
		}
	}
	
	private static void setupChannels(String filepath) throws NumberFormatException, IOException, ParseException {
		System.out.println("Please select an option: ");
		System.out.println("1. Add Channel");
		System.out.println("2. Remove Channel");
		System.out.print("Option: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int option = Integer.parseInt(reader.readLine());
		switch(option) {
		case 1:
			addChannels(filepath, reader);
			break;
		case 2:
			removeChannels(filepath, reader);
			break;
		default:
			System.out.println("Not a valid option inputted!");
			System.exit(1);
		}
	}

	private static HashMap<String, Boolean> getOldStatus(String filepath) throws FileNotFoundException, IOException, ParseException{
		HashMap<String, Boolean> oldStatus = new HashMap<String, Boolean>();
		JSONParser parser = new JSONParser();
		JSONObject statuses = (JSONObject) parser.parse(new FileReader(filepath));
		for(Object channel: statuses.keySet()) {
			oldStatus.put(channel.toString(), (Boolean) statuses.get(channel.toString()));
		}
		return oldStatus;
	}

	@SuppressWarnings("deprecation")
	private static HashMap<String, Boolean> getStatus(Set<String> channels) throws InterruptedException {
		HashMap<String, Boolean> currStatus = new HashMap<String, Boolean>();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		options.addArguments("--mute-audio");
		options.addArguments("--log-level=3");
		options.addArguments("--disable-logging");
		WebDriver browser = new ChromeDriver(options);
		WebDriverWait wait = new WebDriverWait(browser, 20);
		for(String channel: channels) {
			browser.get("https://twitch.tv/" + channel + "/");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("tw-upcase")));
			List<WebElement> liveStatus = browser.findElements(By.xpath("/html/body/div[1]/div/div[2]/div/main/div[2]/div[3]/div/div/div[1]/div[1]/div[2]/div/div[1]/div/div/div/div[1]/div/div/a"));
			if(liveStatus.size()>0) {
				currStatus.put(channel, true);
			} else {
				currStatus.put(channel, false);
			}
		}
		browser.close();
		return currStatus;
	}
	
	private static void sendLiveNotification(List<String> nowLive) {
		Entity<String> payload = null;
		String streamer = "";
		for(int i=0; i<nowLive.size()-1; i++) {
			streamer += nowLive.get(i) + ", ";
		}
		streamer += nowLive.get(nowLive.size()-1);
		if(nowLive.size() == 1) {
			streamer += " is live!";
			payload = Entity.json("{\"pushTitle\": \"" + streamer + "\", \"link\": \"https://twitch.tv/" + nowLive.get(0) + "\", \"content\": \"Check them out on twitch.tv/" + nowLive.get(0) + "!\"}");
		} else {
			streamer += " are live!";
			payload = Entity.json("{\"pushTitle\": \"" + streamer + "\", \"content\": \"Check them out on Twitch!\"}");
		}
		Client client = ClientBuilder.newClient();
		Response response = client.target("https://api.spontit.com/v3/push")
		  .request(MediaType.APPLICATION_JSON_TYPE)
		  .header("X-Authorization", "V5ZK5PSRUOL3PORJBXF2YJWJESH1BEOM79EC0NCW4DD73GU3M8FE0MRCPRN51E8N7C0SR9PFR02NUJ5XUDBHBRSAB106L6PQH2I7")
		  .header("X-UserId", "user59319487026")
		  .post(payload);
		if(response.getStatus() == 200) {
			System.out.println("Notif Sent!");
		} else {
			System.out.println("Notif not Sent!");
		}
	}
	
	private static void updateStatusFile(HashMap<String, Boolean> currStatus, String filepath) throws IOException {
		JSONObject json = new JSONObject(currStatus);
		FileWriter writer = new FileWriter(filepath);
		writer.write(json.toJSONString());
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException, ParseException {
		HashMap<String, Boolean> oldStatus = new HashMap<String, Boolean>();
		HashMap<String, Boolean> currStatus = new HashMap<String, Boolean>();
		List<String> nowLive = new ArrayList<String>();
		Set<String> channels = new HashSet<String>();
		if(args.length == 1) {
			setupChannels(args[0]);
		} else if(args.length == 2) {
			System.setProperty("webdriver.chrome.driver", args[1]);
			oldStatus = getOldStatus(args[0]);
			channels = oldStatus.keySet();
			currStatus = getStatus(channels);
			for(String channel: currStatus.keySet()) {
				if(currStatus.get(channel) && !oldStatus.get(channel)) {
					nowLive.add(channel);
				}
			}
			if(!nowLive.isEmpty()) {
				Collections.sort(nowLive);
				sendLiveNotification(nowLive);
			}
			updateStatusFile(currStatus, args[0]);
		} else {
			System.err.println("Invalid arguments, make sure you are only providing the chanels json file and the chrome driver!");
			System.exit(1);
		}
		//TODO 
		/**
		 * Send notifications
		 * Add way to remeber state of channel and send notification only when state changes from offline to online
		 * Switch to jsoup over selenium
		 * webui or something to easliy change who to get notifications from etc.
		 * If no args allow to add channels
		 */

	}
}
