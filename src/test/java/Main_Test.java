/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 13.07.15
 * Time: 22:19
 * To change this template use File | Settings | File Templates.
 */
public class Main_Test {
    public static void main(String[] args) {
        int[]testRazres=new int[499];
        for (int i=0;i<testRazres.length;i++){
            testRazres[i]=250-(int)(Math.sin(((double)500-i)/500*Math.PI)*250);
        }
        JFrame_ObrobkaImage obrobkaEgg =new JFrame_ObrobkaImage(testRazres);
        obrobkaEgg.setLocationRelativeTo(null);
        obrobkaEgg.setVisible(true);

    }
}
