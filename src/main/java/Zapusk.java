/**
 * Created with IntelliJ IDEA.
 * User: spasko
 * Date: 09.09.14
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class Zapusk {
    JFrame_SnatieRazmerov snatieRazmerov = new JFrame_SnatieRazmerov(this);
    JFrame_ObrobkaEgg obrobkaEgg = new JFrame_ObrobkaEgg(this);
    Dvijenie dvijenie = new Dvijenie(this);
    JFrame_RushneUpravlinna rushneUpravlinna = new JFrame_RushneUpravlinna(this);

    public Zapusk() {
        snatieRazmerov.setLocationRelativeTo(null);
        snatieRazmerov.setVisible(true);
        rushneUpravlinna.setLocationRelativeTo(null);
        rushneUpravlinna.setVisible(false);
        obrobkaEgg.setLocationRelativeTo(null);
        obrobkaEgg.setVisible(false);
    }

    public static void main(String[] args) {
        new Zapusk();
    }
}
