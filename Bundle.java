import java.util.Locale;
import java.util.ResourceBundle;

public class Bundle {
	
	private static final Locale LOCALE = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
	private static final ResourceBundle bundle = getBundle("bundle");
	
	private static ResourceBundle getBundle(String bundle) {
		try {
			return ResourceBundle.getBundle("resources/" + bundle, LOCALE);
		} catch(Exception e) {
			return ResourceBundle.getBundle(bundle, LOCALE);
		}
	}
	
	protected static String getString(String key) {
		return bundle.getString(key);
	}
	
	protected static String getString(String key, String filename) {
		return bundle.getString(key).replace("%f%", filename);
	}

}
