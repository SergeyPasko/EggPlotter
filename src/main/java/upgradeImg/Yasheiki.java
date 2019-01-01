package upgradeImg;

import drawPanels.Draw_ModifyImg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 20.07.15
 * Time: 20:00
 * To change this template use File | Settings | File Templates.
 */
public class Yasheiki extends UpgradeImg {
    private JCheckBox jCheckBox_simetria = new JCheckBox("Симетрія (відстань між точками по X та Y однакова)");
    private JScrollBar jScrollBar_shilnistX;
    private JScrollBar jScrollBar_shilnistY;
    private JScrollBar jScrollBar_tovshinaPeremusok;
    private JLabel jLabel_tovshinaPeremusok;
    private JLabel jLabel_shilnistX;
    private JLabel jLabel_shilnistY;
    private MyAdjusmentListener myAdjusmentListener = new MyAdjusmentListener();


    @Override
    public String getNameTabletPane() {
        return "Гепардова сітка";
    }

    @Override
    public Image getSummaImg() {
        return killColor(draw_modifyImg.getImg(), Color.MAGENTA, Color.WHITE);
    }

    @Override
    public void createPoints() {
        int[] imgTemp = imgStart.clone();
        int[] imgTempPopravka = imgStart.clone();
        int[] imgEnd = imgStart.clone();
        ArrayList<Point> tmpPoints;

        points = new ArrayList<Point>();
        int i = 0;
        int max = getMaxRazrez(razrez);
        Point tmpPoint;
        Random random = new Random();
        boolean original;
        while (i < imgVusota * imgShirina) {
            i++;
            tmpPoint = new Point(random.nextInt(imgShirina), random.nextInt(imgVusota));

            if ((imgStart[imgShirina * tmpPoint.y + tmpPoint.x] == 0Xff000000) &&
                    (sravnenieSveta(imgStart, tmpPoint.x, tmpPoint.y, 0, 1, 255 * 3) ||
                            sravnenieSveta(imgStart, tmpPoint.x, tmpPoint.y, 1, 0, 255 * 3) ||
                            sravnenieSveta(imgStart, tmpPoint.x, tmpPoint.y, 0, -1, 255 * 3) ||
                            sravnenieSveta(imgStart, tmpPoint.x, tmpPoint.y, -1, 0, 255 * 3))) {
                original = true;
                for (int q = -jScrollBar_shilnistX.getValue() * max / razrez[tmpPoint.y] / 2;
                     q <= jScrollBar_shilnistX.getValue() * max / razrez[tmpPoint.y] / 2; q++) {
                    for (int w = -jScrollBar_shilnistY.getValue() / 2;
                         w <= jScrollBar_shilnistY.getValue() / 2; w++) {
                        if (sravnenieSveta(imgTemp, tmpPoint.x, tmpPoint.y, q, w, 100)) {
                            original = false;
                            break;
                        }
                    }
                }

                if (original) {
                    imgTemp[imgShirina * tmpPoint.y + tmpPoint.x] = 100;
                    points.add(tmpPoint);
                }
            }
        }


        i = 0;
        tmpPoints = new ArrayList<Point>();
        while (i < imgVusota * imgShirina) {
            i++;
            tmpPoint = new Point(random.nextInt(imgShirina), random.nextInt(imgVusota));


            if (imgStart[imgShirina * tmpPoint.y + tmpPoint.x] == 0Xff000000 && (i % jScrollBar_shilnistX.getValue() == 0)) {
                original = true;

                for (int q = -jScrollBar_shilnistX.getValue() * max / razrez[tmpPoint.y] / 2;
                     q <= jScrollBar_shilnistX.getValue() * max / razrez[tmpPoint.y] / 2; q++) {
                    for (int w = -jScrollBar_shilnistY.getValue() / 2;
                         w <= jScrollBar_shilnistY.getValue() / 2; w++) {
                        if (sravnenieSveta(imgTemp, tmpPoint.x, tmpPoint.y, q, w, 100)) {
                            original = false;
                        }
                    }
                }

                if (original) {
                    imgTemp[imgShirina * tmpPoint.y + tmpPoint.x] = 100;
                    tmpPoints.add(tmpPoint);
                }
            }
        }

        for (Point p : tmpPoints) {
            points.add(p);
        }
        Point[] pointsArray = new Point[points.size()];
        points.toArray(pointsArray);
        Point first = new Point();
        Point second = new Point(1000, 1000);
        for (int j = 0; j < imgVusota; j++) {
            for (int k = 0; k < imgShirina; k++) {
                if (sravnenieSveta(imgStart, k, j, 0, 0, 255 * 3)) continue;
                first.x = 1000;
                first.y = 1000;
                second.x = 1000;
                second.y = 1000;
                for (Point aPointsArray : pointsArray) {
                    if (Point.distance(aPointsArray.x, aPointsArray.y, k, j) <
                            Point.distance(first.x, first.y, k, j)) {
                        first.x = aPointsArray.x;
                        first.y = aPointsArray.y;
                    }
                }
                for (Point aPointsArray : pointsArray) {
                    if (Point.distance(aPointsArray.x, aPointsArray.y, k, j) <
                            Point.distance(second.x, second.y, k, j) && !aPointsArray.equals(first)) {
                        second.x = aPointsArray.x;
                        second.y = aPointsArray.y;
                    }
                }

                if (Point.distance(second.x, second.y, k, j) - Point.distance(first.x, first.y, k, j) < 1)//jScrollBar_tovshinaPeremusok.getValue())
                {
                    imgEnd[imgShirina * j + k] = Color.MAGENTA.getRGB();
                    imgTempPopravka[imgShirina * j + k] = Color.YELLOW.getRGB();
                }
            }
        }
        int sizeRadius=jScrollBar_tovshinaPeremusok.getValue()/2-1;
        for (int j = 0; j < imgTempPopravka.length; j++) {
            if (imgTempPopravka[j] != Color.YELLOW.getRGB()) continue;
            int x = j % imgShirina;
            int y = (j - x) / imgShirina;
            for (int k = -sizeRadius; k < sizeRadius; k++) {
                for (int m = -sizeRadius*max / razrez[y]; m < sizeRadius*max / razrez[y]; m++) {
                    if (Point.distance(0,0,m* razrez[y]/max,k )>sizeRadius)continue;
                    if (((y + k) * imgShirina + (x + m)>=0)&&((y + k) * imgShirina + (x + m)<imgStart.length)&&imgTempPopravka[(y + k) * imgShirina + (x + m)] == Color.BLACK.getRGB())
                        imgEnd[(y + k) * imgShirina + (x + m)] = Color.MAGENTA.getRGB();
                }
            }
        }
        points=new ArrayList<Point>();
        draw_modifyImg.setImage(createImage(new MemoryImageSource(imgShirina, imgVusota, imgEnd, 0, imgShirina)), true);
    }


    public Yasheiki(int width, int height, int[] razrez, Draw_ModifyImg draw_modifyImg) {
        super(width, height, razrez, draw_modifyImg);
        jCheckBox_simetria.setSize(width, height / 7);
        jCheckBox_simetria.setLocation(0, 0);
        jCheckBox_simetria.setHorizontalAlignment(SwingConstants.CENTER);
        jCheckBox_simetria.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (((JCheckBox) e.getItem()).isSelected()) {
                    jScrollBar_shilnistY.setValue(jScrollBar_shilnistX.getValue());
                    jScrollBar_shilnistY.setEnabled(false);
                } else jScrollBar_shilnistY.setEnabled(true);
            }
        });
        add(jCheckBox_simetria);
        jScrollBar_shilnistX = new JScrollBar(Adjustable.HORIZONTAL, 40, 1, 30, 61);
        jScrollBar_shilnistX.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistX = new JLabel();
        super.setScrollBar(new JLabel("Відстань між центрами по X"), jScrollBar_shilnistX, jLabel_shilnistX, 1);
        jScrollBar_shilnistY = new JScrollBar(Adjustable.HORIZONTAL, 40, 1, 30, 61);
        jScrollBar_shilnistY.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistY = new JLabel();
        super.setScrollBar(new JLabel("Відстань між центрами по Y"), jScrollBar_shilnistY, jLabel_shilnistY, 2);
        jScrollBar_tovshinaPeremusok = new JScrollBar(Adjustable.HORIZONTAL, 10, 1, 6, 31);
        jScrollBar_tovshinaPeremusok.addAdjustmentListener(myAdjusmentListener);
        jLabel_tovshinaPeremusok = new JLabel();
        super.setScrollBar(new JLabel("Товщина перемичок"), jScrollBar_tovshinaPeremusok, jLabel_tovshinaPeremusok, 3);

        jCheckBox_simetria.doClick();
        writeLabelText();
    }

    private void writeLabelText() {
        jLabel_shilnistX.setText((double) jScrollBar_shilnistX.getValue() / 5 + " мм");
        jLabel_shilnistY.setText((double) jScrollBar_shilnistY.getValue() / 5 + " мм");
        jLabel_tovshinaPeremusok.setText((double) jScrollBar_tovshinaPeremusok.getValue() / 5 + " мм");
    }

    private class MyAdjusmentListener implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (jCheckBox_simetria.isSelected()) {
                jScrollBar_shilnistY.setValue(jScrollBar_shilnistX.getValue());
            }
            writeLabelText();
            createPoints();
            drawPoints();
        }
    }

}
