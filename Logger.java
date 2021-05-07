//Logger.java
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

/**
 * 
 * @author Jack Stockley
 * 
 * @version 0.9-beta
 *
 */
public class Logger {

	/**
	 * 
	 * @param message
	 */
	protected static void logInfo(String message) {
		Date curDate = new Date();
		try {
			Path log = Paths.get("BTTN.log");
			BasicFileAttributes attr = Files.readAttributes(log, BasicFileAttributes.class);
			Date creation = new Date(attr.creationTime().toMillis());
			long diffTime = curDate.getTime() - creation.getTime();
			long diffDays = diffTime / (1000 * 60 * 60 * 24);
			if(diffDays >= 1) {
				clearLog();
			}
		} catch (IOException e1) {
			System.err.println(Bundle.getString("logClear"));
		}
		String fullMsg = curDate.toString() + ": " + Bundle.getString("info") +" - " + message + '\n';
		try {
			FileWriter writer = new FileWriter("BTTN.log", true);
			writer.write(fullMsg);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println(Bundle.getString("logWrite"));
			System.exit(1);
		}
		System.out.println(message);
		System.exit(0);
	}

	/**
	 * 
	 * @param message
	 */
	protected static void logWarn(String message) {
		Date curDate = new Date();
		String fullMsg = curDate.toString() + ": "+ Bundle.getString("warn") + " - " + message + '\n';
		try {
			FileWriter writer = new FileWriter("BTTN.log", true);
			writer.write(fullMsg);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println(Bundle.getString("logWrite"));
			System.exit(1);
		}
		System.err.println(message);
		System.exit(0);
	}

	/**
	 * 
	 * @param message
	 */
	protected static void logError(String message) {
		Date curDate = new Date();
		String fullMsg = curDate.toString() + ": " + Bundle.getString("error")  + " - " + message + '\n';
		try {
			FileWriter writer = new FileWriter("BTTN.log", true);
			writer.write(fullMsg);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println(Bundle.getString("logWrite"));
			System.exit(1);
		}
		System.err.println(message);
		System.exit(1);
	}

	private static void clearLog() {
		File log = new File("BTTN.log");
		log.delete();
	}

}
