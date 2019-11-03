package Config;

import java.util.ResourceBundle;

public class Param_Profile {
	private static Param_Profile instance;
	private static String profile;
	private static String fileAppender = "";

	static {
		if (instance == null) {
			instance = new Param_Profile();			
			profile = (String) ResourceBundle.getBundle("Profile").getObject(Constants.PROFILE_NAME);
			if (isSecondGeneration()) {
				fileAppender = "2";
			}
		}
	}

	public static boolean isSecondGeneration() {
		return Constants.SECOND_GENERATION.equalsIgnoreCase(profile);
	}

	public static String getFileAppender() {
		return fileAppender;
	}

}
