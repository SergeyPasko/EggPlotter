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

/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 20.07.15
 * Time: 20:00
 * To change this template use File | Settings | File Templates.
 */
public class Ravnomerno extends UpgradeImg {
    private JCheckBox jCheckBox_simetria = new JCheckBox("Симетрія (відстань між точками по X та Y однакова)");
    private JScrollBar jScrollBar_shilnistX;
    private JScrollBar jScrollBar_shilnistY;
    private JScrollBar jScrollBar_zmishennaX;
    private JScrollBar jScrollBar_zmishennaY;
    private JLabel jLabel_shilnistX;
    private JLabel jLabel_shilnistY;
    private JLabel jLabel_zmishennaX;
    private JLabel jLabel_zmishennaY;
    private MyAdjusmentListener myAdjusmentListener = new MyAdjusmentListener();

    @Override
    public String getNameTabletPane() {
        return "Рівномірно точки";
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
        points = new ArrayList<Point>();
     /*   int i=0;
        while (i<imgVusota*imgShirina/10){
            i++;
            points.add(new Point((new Random()).nextInt(imgShirina),(new Random()).nextInt(imgVusota)));
        }  */
        for (int i = (int)(jScrollBar_zmishennaX.getValue()* (double)imgShirina / (Math.PI * getMaxRazrez(razrez))); i < imgShirina; i += (int)(jScrollBar_shilnistX.getValue() * (double)imgShirina / (Math.PI * getMaxRazrez(razrez)))) {
            for (int j = jScrollBar_zmishennaY.getValue(); j < imgVusota; j += jScrollBar_shilnistY.getValue()) {
                Point tmpPoint = new Point(i, j);
                if (imgStart[imgShirina * tmpPoint.y + tmpPoint.x] == 0Xff000000)
                    points.add(tmpPoint);

            }
        }
    }


    public Ravnomerno(int width, int height, int[] razrez, Draw_ModifyImg draw_modifyImg) {
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
        jScrollBar_shilnistX = new JScrollBar(Adjustable.HORIZONTAL, 20, 1, 15, 41);
        jScrollBar_shilnistX.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistX = new JLabel();
        super.setScrollBar(new JLabel("Відстань між точками по X"), jScrollBar_shilnistX, jLabel_shilnistX, 1);
        jScrollBar_shilnistY = new JScrollBar(Adjustable.HORIZONTAL, 20, 1, 15, 41);
        jScrollBar_shilnistY.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistY = new JLabel();
        super.setScrollBar(new JLabel("Відстань між точками по Y"), jScrollBar_shilnistY, jLabel_shilnistY, 2);
        jScrollBar_zmishennaX = new JScrollBar(Adjustable.HORIZONTAL, 0, 1, 0, 41);
        jScrollBar_zmishennaX.addAdjustmentListener(myAdjusmentListener);
        jLabel_zmishennaX = new JLabel();
        super.setScrollBar(new JLabel("Початкове зміщення по X"), jScrollBar_zmishennaX, jLabel_zmishennaX, 3);
        jScrollBar_zmishennaY = new JScrollBar(Adjustable.HORIZONTAL, 0, 1, 0, 41);
        jScrollBar_zmishennaY.addAdjustmentListener(myAdjusmentListener);
        jLabel_zmishennaY = new JLabel();
        super.setScrollBar(new JLabel("Початкове зміщення по Y"), jScrollBar_zmishennaY, jLabel_zmishennaY, 4);
        jCheckBox_simetria.doClick();
        writeLabelText();
    }

    private void writeLabelText() {
        jLabel_shilnistX.setText((double) jScrollBar_shilnistX.getValue() / 5 + " мм");
        jLabel_shilnistY.setText((double) jScrollBar_shilnistY.getValue() / 5 + " мм");
        jLabel_zmishennaX.setText((double) jScrollBar_zmishennaX.getValue() / 5 + " мм");
        jLabel_zmishennaY.setText((double) jScrollBar_zmishennaY.getValue() / 5 + " мм");
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
