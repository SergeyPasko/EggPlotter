package upgradeImg;

import drawPanels.Draw_ModifyImg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 20.07.15
 * Time: 20:00
 * To change this template use File | Settings | File Templates.
 */
public class ScaleImg extends UpgradeImg {
    private JCheckBox jCheckBox_simetria = new JCheckBox("Симетрія (відстань між точками по X та Y однакова)");
    private JScrollBar jScrollBar_mashtabX;
    private JScrollBar jScrollBar_mashtabY;
    private JScrollBar jScrollBar_zmishennaX;
    private JScrollBar jScrollBar_zmishennaY;
    private JLabel jLabel_shilnistX;
    private JLabel jLabel_shilnistY;
    private JLabel jLabel_zmishennaX;
    private JLabel jLabel_zmishennaY;
    private MyAdjusmentListener myAdjusmentListener = new MyAdjusmentListener();

    @Override
    public String getNameTabletPane() {
        return "Зміна розмірів/пропорцій";
    }

    @Override
    public Image getSummaImg() {

        Image imgOriginalneZobrajenna = createImage(new MemoryImageSource(imgShirina, imgVusota, imgStart, 0, imgShirina));
        {
            BufferedImage scaledBI = new BufferedImage(draw_modifyImg.getWidth(),
                    draw_modifyImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaledBI.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, imgShirina, imgVusota);
            g.drawImage(imgOriginalneZobrajenna, (jScrollBar_zmishennaX.getValue() - jScrollBar_zmishennaX.getMaximum() / 2),
                    jScrollBar_zmishennaY.getValue() - jScrollBar_zmishennaY.getMaximum() / 2, draw_modifyImg.getWidth() * jScrollBar_mashtabX.getValue() / 100,
                    draw_modifyImg.getHeight() * jScrollBar_mashtabY.getValue() / 100, null);
            g.dispose();
            imgOriginalneZobrajenna = scaledBI;
        }


        return imgOriginalneZobrajenna;

    }

    @Override
    public void createPoints() {
        draw_modifyImg.setImage(getSummaImg(), true);
    }


    public ScaleImg(int width, int height, int[] razrez, Draw_ModifyImg draw_modifyImg) {
        super(width, height, razrez, draw_modifyImg);
        jCheckBox_simetria.setSize(width, height / 7);
        jCheckBox_simetria.setLocation(0, 0);
        jCheckBox_simetria.setHorizontalAlignment(SwingConstants.CENTER);
        jCheckBox_simetria.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (((JCheckBox) e.getItem()).isSelected()) {
                    jScrollBar_mashtabY.setValue(jScrollBar_mashtabX.getValue());
                    jScrollBar_mashtabY.setEnabled(false);
                } else jScrollBar_mashtabY.setEnabled(true);
            }
        });
        add(jCheckBox_simetria);
        jScrollBar_mashtabX = new JScrollBar(Adjustable.HORIZONTAL, 100, 1, 50, 150);
        jScrollBar_mashtabX.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistX = new JLabel();
        super.setScrollBar(new JLabel("Масштабування по X"), jScrollBar_mashtabX, jLabel_shilnistX, 1);
        jScrollBar_mashtabY = new JScrollBar(Adjustable.HORIZONTAL, 100, 1, 50, 150);
        jScrollBar_mashtabY.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistY = new JLabel();
        super.setScrollBar(new JLabel("Масштабування по Y"), jScrollBar_mashtabY, jLabel_shilnistY, 2);
        jScrollBar_zmishennaX = new JScrollBar(Adjustable.HORIZONTAL, 250, 1, 0, 501);
        jScrollBar_zmishennaX.addAdjustmentListener(myAdjusmentListener);
        jLabel_zmishennaX = new JLabel();
        super.setScrollBar(new JLabel("Початкове зміщення по X"), jScrollBar_zmishennaX, jLabel_zmishennaX, 3);
        jScrollBar_zmishennaY = new JScrollBar(Adjustable.HORIZONTAL, 150, 1, 0, 301);
        jScrollBar_zmishennaY.addAdjustmentListener(myAdjusmentListener);
        jLabel_zmishennaY = new JLabel();
        super.setScrollBar(new JLabel("Початкове зміщення по Y"), jScrollBar_zmishennaY, jLabel_zmishennaY, 4);
        jCheckBox_simetria.doClick();
        writeLabelText();
    }

    private void writeLabelText() {
        jLabel_shilnistX.setText((double) jScrollBar_mashtabX.getValue() + " %");
        jLabel_shilnistY.setText((double) jScrollBar_mashtabY.getValue() + " %");
        jLabel_zmishennaX.setText((double) (jScrollBar_zmishennaX.getValue() - jScrollBar_zmishennaX.getMaximum() / 2) / 5 + " мм");
        jLabel_zmishennaY.setText((double) (jScrollBar_zmishennaY.getValue() - jScrollBar_zmishennaY.getMaximum() / 2) / 5 + " мм");
    }

    private class MyAdjusmentListener implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (jCheckBox_simetria.isSelected()) {
                jScrollBar_mashtabY.setValue(jScrollBar_mashtabX.getValue());
            }
            writeLabelText();
            createPoints();
            drawPoints();
        }
    }

}
