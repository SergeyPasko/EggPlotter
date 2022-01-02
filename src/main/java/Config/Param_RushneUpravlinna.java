package Config;

import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Serhii.Pasko
 * Date: 03.07.15
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public class Param_RushneUpravlinna {
    private static Param_RushneUpravlinna instance;
    private ResourceBundle resourceBundle;
    //класс извлекает информацию из файла messages. properties
    private static final String BUNDLE_NAME = "RushneUpravlinna";
    public static final String VUSOTA_ELEMENTA = "VUSOTA_ELEMENTA";

    public static Param_RushneUpravlinna getInstance() {
        if (instance == null) {
            instance = new Param_RushneUpravlinna();
            instance.resourceBundle =
                    ResourceBundle.getBundle(BUNDLE_NAME);
        }
        return instance;
    }

    public int getProperty(String key) {
        return Integer.parseInt((String) resourceBundle.getObject(key));
    }
}
