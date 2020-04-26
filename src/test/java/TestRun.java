/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 18.02.17
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public class TestRun {
    public static void main(String args[]) throws InterruptedException {
        Zapusk zapusk = new Zapusk();
        int value = Integer.MAX_VALUE;
        zapusk.dvijenie.goTo( value, 0,0, 500, false);
        Thread.sleep(3000);

        zapusk.dvijenie.goTo( 0, 0,0, 500, false);

        zapusk.dvijenie.notSignalXYZ(true, true, true);
        System.exit(0);
    }
}
