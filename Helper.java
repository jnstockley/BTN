package com.github.jnstockley;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Helper {

	public static void setupFolder() {
		String userDir = System.getProperty("user.home");
		if (!Files.isDirectory(Paths.get(userDir + "/BTTN/"))) {
			File dir = new File(userDir + "/BTTN/");
			try {
				if (dir.mkdir()) {
					File config = new File(dir.getPath() + "/config.json");
					File youtube = new File(dir.getPath() + "/youtube.json");
					File twitch = new File(dir.getPath() + "/twitch.json");
					File youtubeLive = new File(dir.getPath() + "/youtubeLive.json");
					try {
						if (!config.createNewFile()) {
							System.err.println("Unable to create config file!");
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
					try {
						if (!youtube.createNewFile()) {
							System.err.println("Unable to create youtube data file!");
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
					try {
						if (!twitch.createNewFile()) {
							System.err.println("Unable to create twitch data file!");
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
					try {
						if (!youtubeLive.createNewFile()) {
							System.err.println("Unable to create youtube live data file!");
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
					System.out.println("Config and data files are stored here: " + dir.getPath() + "/");
					System.out.println("Before running BTTN in production state, please setup confing and all data files");
					System.exit(0);
				} else {
					System.err.println("Unable to create BTTN config folder!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

		}
	}

}
