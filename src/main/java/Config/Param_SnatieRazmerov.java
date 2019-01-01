package Config;

import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Serhii.Pasko
 * Date: 03.07.15
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public class Param_SnatieRazmerov {
    private static Param_SnatieRazmerov instance;
    private ResourceBundle resourceBundle;
    //класс извлекает информацию из файла messages. properties
    private static final String BUNDLE_NAME = "SnatieRazmerov";
    public static final String POLOSA_OGRANISHENIA = "POLOSA_OGRANISHENIA";
    public static final String VUSOTA_ELEMENTA = "VUSOTA_ELEMENTA";
    public static final String VUSOTA_OKNA = "VUSOTA_OKNA";
    public static final String SHIRINA_OKNA = "SHIRINA_OKNA";

    public static Param_SnatieRazmerov getInstance() {
        if (instance == null) {
            instance = new Param_SnatieRazmerov();
            instance.resourceBundle =
                    ResourceBundle.getBundle(BUNDLE_NAME);
        }
        return instance;
    }

    public int getProperty(String key) {
        return Integer.parseInt((String) resourceBundle.getObject(key));
    }
}
