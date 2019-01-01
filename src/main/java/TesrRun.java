/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 18.02.17
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public class TesrRun {
    public static void main(String args[]) {
        Zapusk zapusk = new Zapusk();
        zapusk.dvijenie.goTo( 1580, 0,0, 1100, false);


        zapusk.dvijenie.notSignalXYZ(true, true, true);
        System.exit(0);
    }
}
