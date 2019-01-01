package upgradeImg;

import Config.Param_ObrabotkaImage;
import drawPanels.Draw_ModifyImg;

import javax.swing.*;
import java.awt.*;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 20.07.15
 * Time: 19:50
 * To change this template use File | Settings | File Templates.
 */
public abstract class UpgradeImg extends JPanel {
	private static final long serialVersionUID = 1L;
	protected int vusotaElementa = Param_ObrabotkaImage.getInstance().getProperty(Param_ObrabotkaImage.VUSOTA_ELEMENTA);
    protected int[] imgStart;
    protected int imgShirina;
    protected int imgVusota;
    protected int[] razrez;
    protected Draw_ModifyImg draw_modifyImg;
    protected ArrayList<Point> points=new ArrayList<Point>();

    //Перевіряється колір точки з масива pix зміщеної на (delta_x,delta_y) відносно даної (xx,yy) з кольором[] sravnivaemoe_znashenie
    protected boolean sravnenieSveta(int[] pix, int xx, int yy, int delta_x, int delta_y, int... sravnivaemoe_znashenie) {
        if (xx + delta_x < 0 || xx + delta_x > imgShirina - 1 || yy + delta_y > imgVusota - 1 || yy + delta_y < 0)
            return false;
        else {
            int summaColor = (0xff & (pix[(yy + delta_y) * imgShirina + xx + delta_x])) + (0xff & (pix[(yy + delta_y) * imgShirina + xx + delta_x] >> 16)) +
                    (0xff & pix[(yy + delta_y) * imgShirina + xx + delta_x] >> 8);

            for (int a : sravnivaemoe_znashenie) {
                if (summaColor == a) return true;
            }
            return false;
        }
    }


    public void setImage(Image img) {
        if (img != null) {
            imgShirina = img.getWidth(null);
            imgVusota = img.getHeight(null);

            imgStart = new int[imgShirina * imgVusota];
            try {
                PixelGrabber pg = new PixelGrabber(img, 0, 0, imgShirina, imgVusota, imgStart, 0, imgShirina);
                pg.grabPixels();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    public Image killColor(Image imageToModify,Color colorBegin,Color colorEnd){
        int[] imgEnd = new int[imgShirina * imgVusota];
        try {
            PixelGrabber pg = new PixelGrabber(imageToModify, 0, 0, imgShirina, imgVusota, imgEnd, 0, imgShirina);
            pg.grabPixels();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        for (int j = 0; j < imgShirina; j++) {
            for (int k = 0; k < imgVusota; k++) {
                if (imgEnd[k * imgShirina + j]==colorBegin.getRGB()) imgEnd[k * imgShirina + j]=colorEnd.getRGB();

            }
        }

        return createImage(new MemoryImageSource(imgShirina, imgVusota, imgEnd, 0, imgShirina));
    }


    public abstract String getNameTabletPane();

    public void drawPoints() {
        draw_modifyImg.setPoints(points);
    } 

    public abstract Image getSummaImg();

    public abstract void createPoints();

    protected UpgradeImg(int width, int heigth, int[] razrez, Draw_ModifyImg draw_modifyImg) {
        this.draw_modifyImg = draw_modifyImg;
        this.razrez = razrez;
        this.setLayout(null);
        this.setSize(width, heigth);
    }

    protected int getMaxRazrez(int[]razrez) {
        int max = 0;
        for (int i = 0; i < razrez.length; i++) {
            if (razrez[i] > max) max = razrez[i];
        }
        return max;
    }

    protected void setScrollBar(JLabel jLabel_left, JScrollBar jScrollBar, JLabel jLabel_right, int nomerStroki) {
        jLabel_left.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel_left.setSize(this.getWidth() / 5, this.getHeight() / 7);
        jLabel_left.setLocation(0, nomerStroki * vusotaElementa);
        jScrollBar.setSize(3 * this.getWidth() / 5, this.getHeight() / 7);
        jScrollBar.setLocation(this.getWidth() / 5, nomerStroki * vusotaElementa);
        jLabel_right.setHorizontalAlignment(SwingConstants.LEFT);
        jLabel_right.setSize(this.getWidth() / 5, this.getHeight() / 7);
        jLabel_right.setLocation(4 * this.getWidth() / 5, nomerStroki * vusotaElementa);
        this.add(jLabel_left);
        this.add(jScrollBar);
        this.add(jLabel_right);
    }

}
