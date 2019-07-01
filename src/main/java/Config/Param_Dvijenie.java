package Config;

import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA. User: Serhii.Pasko Date: 03.07.15 Time: 11:49 To
 * change this template use File | Settings | File Templates.
 */
public class Param_Dvijenie {
	private static Param_Dvijenie instance;
	private ResourceBundle resourceBundle;
	// класс извлекает информацию из файла messages. properties
	private static final String BUNDLE_NAME = "Dvijenie" + Param_Profile.getFileAppender();

	public static final String VUSOTAPROHODANADPOVERHNOSTU = "VUSOTAPROHODANADPOVERHNOSTU";
	public static final String GLUBINAPROREZANIA = "GLUBINAPROREZANIA";
	public static final String SCOROSTPEREHODA = "SCOROSTPEREHODA";
	public static final String VUSOTAPODJOMANASHALNA = "VUSOTAPODJOMANASHALNA";

	public static final String HAGOVVOTSSHETEPOPRAVKAX = "HAGOVVOTSSHETEPOPRAVKAX";
	public static final String HAGOVVOTSSHETEPOPRAVKAY = "HAGOVVOTSSHETEPOPRAVKAY";
	public static final String HAGOVVOTSSHETEPOPRAVKAZ = "HAGOVVOTSSHETEPOPRAVKAZ";
	public static final String SMESHENIENASHALNE = "SMESHENIENASHALNE";

	public static final String HAGOVNAPOVOROT = "HAGOVNAPOVOROT";

	
	public static Param_Dvijenie getInstance() {
		if (instance == null) {
			instance = new Param_Dvijenie();
			instance.resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
		}
		return instance;
	}

	public int getProperty(String key) {
		return Integer.parseInt((String) resourceBundle.getObject(key));
	}
}
