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
import java.util.Iterator;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 20.07.15
 * Time: 20:00
 * To change this template use File | Settings | File Templates.
 */
public class Kontur extends UpgradeImg {
    private JCheckBox jCheckBox_simetria = new JCheckBox("Симетрія (відстань між точками по X та Y однакова)");
    private JScrollBar jScrollBar_shilnistX;
    private JScrollBar jScrollBar_shilnistY;
    private JLabel jLabel_shilnistX;
    private JLabel jLabel_shilnistY;
    private MyAdjusmentListener myAdjusmentListener = new MyAdjusmentListener();


    @Override
    public String getNameTabletPane() {
        return "Точки по контуру";
    }

    @Override
    public Image getSummaImg() {
        int[] imgEnd = new int[imgStart.length];
        for (int i = 0; i < imgEnd.length; i++) {
            imgEnd[i] = (0Xff000000 | 255 << 16 | 255 << 8 | 255);
        }
        Iterator<Point> it = points.iterator();

        while (it.hasNext()) {
            Point tmpPoint = it.next();
            imgEnd[tmpPoint.y * imgShirina + tmpPoint.x] = 0Xff000000;
            if (tmpPoint.y + 1 <= imgVusota) imgEnd[(tmpPoint.y + 1) * imgShirina + tmpPoint.x] = 0Xff000000;
        }


        return createImage(new MemoryImageSource(imgShirina, imgVusota, imgEnd, 0, imgShirina));

    }

    @Override
    public void createPoints() {
        int[] imgTemp = new int[imgStart.length];
        int[] imgTempPopravka = new int[imgStart.length];
        ArrayList<Point> tmpPoints;
        imgTempPopravka = imgStart;
        for (int j = 0; j < imgShirina; j++) {
            for (int k = 0; k < imgVusota; k++) {
                int p = imgStart[k * imgShirina + j];
                int r;
                int g = 0xff & (p >> 8);
                int b = 0xff & (p);
                if (b == 0) if (sravnenieSveta(imgStart, j, k, 0, 1, 0, 210) &
                        sravnenieSveta(imgStart, j, k, 1, 0, 0, 210) &
                        sravnenieSveta(imgStart, j, k, 0, -1, 0, 210) &
                        sravnenieSveta(imgStart, j, k, -1, 0, 0, 210)) {
                    r = 210;
                    imgTempPopravka[k * imgShirina + j] = (0Xff000000 | r << 16 | g << 8 | b);
                }
            }
        }

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
        for (int povtor = 0; povtor <  Math.max(jScrollBar_shilnistY.getValue(), jScrollBar_shilnistX.getValue()); povtor++) {
            Point pointA = new Point();
            Point pointB = new Point();

            tmpPoints = new ArrayList<Point>();
            boolean flag;
            for (Point p : points) {
                flag = false;
                for (int j = -1; j < 2; j++) {
                    for (int k = -1; k < 2; k++) {
                        if (sravnenieSveta(imgTempPopravka, p.x, p.y, j, k, 0) && (!(j == k && k == 0))) {
                            if (!flag) {
                                pointA = new Point(p.x + j, p.y + k);
                                flag = true;
                            } else {
                                pointB = new Point(p.x + j, p.y + k);
                                break;
                            }
                        }
                    }
                }
                imgTemp[imgShirina * p.y + p.x] = 0;
                outer:for (int delta = -2; delta < 2 * Math.max(jScrollBar_shilnistY.getValue(), jScrollBar_shilnistX.getValue()); delta += 2) {
                    for (int q = -(jScrollBar_shilnistX.getValue() + delta) * max / razrez[pointA.y] / 2;
                         q <= (jScrollBar_shilnistX.getValue() + delta) * max / razrez[pointA.y] / 2; q++) {
                        for (int w = -(jScrollBar_shilnistY.getValue() + delta) / 2;
                             w <= (jScrollBar_shilnistY.getValue() + delta) / 2; w++) {
                            if (sravnenieSveta(imgTemp, pointA.x, pointA.y, q, w, 100)) {
                                flag = false;
                                break outer;
                            }
                            if (sravnenieSveta(imgTemp, pointB.x, pointB.y, q, w, 100)) {
                                flag = true;
                                break outer;
                            }
                        }
                    }
                }
                if (flag) {
                    imgTemp[imgShirina * pointA.y + pointA.x] = 100;
                    tmpPoints.add(pointA);
                } else {
                    {
                        imgTemp[imgShirina * pointB.y + pointB.x] = 100;
                        tmpPoints.add(pointB);
                    }
                }
            }
            points = new ArrayList<Point>(tmpPoints);
        }
    }


    public Kontur(int width, int height, int[] razrez, Draw_ModifyImg draw_modifyImg) {
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
        jScrollBar_shilnistX = new JScrollBar(Adjustable.HORIZONTAL, 15, 1, 7, 31);
        jScrollBar_shilnistX.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistX = new JLabel();
        super.setScrollBar(new JLabel("Відстань між точками по X"), jScrollBar_shilnistX, jLabel_shilnistX, 1);
        jScrollBar_shilnistY = new JScrollBar(Adjustable.HORIZONTAL, 15, 1, 7, 31);
        jScrollBar_shilnistY.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistY = new JLabel();
        super.setScrollBar(new JLabel("Відстань між точками по Y"), jScrollBar_shilnistY, jLabel_shilnistY, 2);

        jCheckBox_simetria.doClick();
        writeLabelText();
    }

    private void writeLabelText() {
        jLabel_shilnistX.setText((double) jScrollBar_shilnistX.getValue() / 5 + " мм");
        jLabel_shilnistY.setText((double) jScrollBar_shilnistY.getValue() / 5 + " мм");
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
