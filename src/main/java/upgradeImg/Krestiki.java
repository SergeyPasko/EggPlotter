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

/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 20.07.15
 * Time: 20:00
 * To change this template use File | Settings | File Templates.
 */
public class Krestiki extends UpgradeImg {
    private JCheckBox jCheckBox_simetria = new JCheckBox("Симетрія (відстань між точками по X та Y однакова)");
    private JScrollBar jScrollBar_shilnistX;
    private JScrollBar jScrollBar_shilnistY;
    private JScrollBar jScrollBar_zmishennaX;
    private JScrollBar jScrollBar_zmishennaY;
    private JScrollBar jScrollBar_tolshina_linii;
    private JLabel jLabel_shilnistX;
    private JLabel jLabel_shilnistY;
    private JLabel jLabel_zmishennaX;
    private JLabel jLabel_zmishennaY;
    private JLabel jLabel_tolshina_linii;
    private MyAdjusmentListener myAdjusmentListener = new MyAdjusmentListener();

    @Override
    public String getNameTabletPane() {
        return "Хрестики";
    }

    @Override
    public Image getSummaImg() {
        return killColor(killColor(draw_modifyImg.getImg(),Color.BLACK,Color.WHITE),Color.MAGENTA,Color.BLACK);

    }

    @Override
    public void createPoints() {
        int[] imgTemp = imgStart.clone();
        int point;
        points = new ArrayList<Point>();
        for (int i = 0; i < imgShirina - jScrollBar_zmishennaX.getValue(); i += 1) {
            for (int j = 0; j < imgVusota - jScrollBar_zmishennaY.getValue(); j += 1) {
                point = (j + jScrollBar_zmishennaY.getValue()) * imgShirina + i + jScrollBar_zmishennaX.getValue();
                if (i % jScrollBar_shilnistX.getValue() == 0 && imgTemp[point] == Color.BLACK.getRGB())
                    switch ((i / jScrollBar_shilnistX.getValue()) % 5) {
                        case 0:
                            if ((j / (jScrollBar_shilnistY.getValue())) % 5 >= 0 &&
                                    (j / jScrollBar_shilnistY.getValue()) % 5 < 2 &&
                                    logikScaleY(j, 0, 2))
                                imgTemp[point] = Color.MAGENTA.getRGB();

                            break;

                        case 1:
                            if ((j / jScrollBar_shilnistY.getValue()) % 5 >= 3 &&
                                    (j / jScrollBar_shilnistY.getValue()) % 5 < 5
                                    && logikScaleY(j, 3, 5))
                                imgTemp[point] = Color.MAGENTA.getRGB();
                            break;
                        case 2:
                            if ((j / jScrollBar_shilnistY.getValue()) % 5 >= 1 &&
                                    (j / jScrollBar_shilnistY.getValue()) % 5 < 3
                                    && logikScaleY(j, 1, 3))
                                imgTemp[point] = Color.MAGENTA.getRGB();
                            break;

                        case 3:
                            if ((j / jScrollBar_shilnistY.getValue()) % 5 >= 0 &&
                                    (j / jScrollBar_shilnistY.getValue()) % 5 < 1
                                    && logikScaleY(j, -1, 1) ||
                                    (j / jScrollBar_shilnistY.getValue()) % 5 >= 4 &&
                                            (j / jScrollBar_shilnistY.getValue()) % 5 < 5
                                            && logikScaleY(j, 4, 6))
                                imgTemp[point] = Color.MAGENTA.getRGB();
                            break;

                        case 4:
                            if ((j / jScrollBar_shilnistY.getValue()) % 5 >= 2 &&
                                    (j / jScrollBar_shilnistY.getValue()) % 5 < 4
                                    && logikScaleY(j, 2, 4))
                                imgTemp[point] = Color.MAGENTA.getRGB();
                            break;

                    }
                if (j % jScrollBar_shilnistY.getValue() == 0 && imgTemp[point] == Color.BLACK.getRGB())
                    switch ((j / jScrollBar_shilnistY.getValue()) % 5) {
                        case 4:
                            if ((j / (jScrollBar_shilnistY.getValue())) % 5 >= 0 &&
                                    (i/ jScrollBar_shilnistX.getValue()) % 5 < 2 &&
                                    logikScaleX(i, 0, 2))
                                imgTemp[point] = Color.MAGENTA.getRGB();

                            break;

                       case 3:
                            if ((i/ jScrollBar_shilnistX.getValue()) % 5 >= 3 &&
                                    (i/ jScrollBar_shilnistX.getValue()) % 5 < 5
                                    && logikScaleX(i, 3, 5))
                                imgTemp[point] = Color.MAGENTA.getRGB();
                            break;
                       case 2:
                            if ((i/ jScrollBar_shilnistX.getValue()) % 5 >= 1 &&
                                    (i/ jScrollBar_shilnistX.getValue()) % 5 < 3
                                    && logikScaleX(i, 1, 3))
                                imgTemp[point] = Color.MAGENTA.getRGB();
                            break;

                         case 1:
                            if ((i/ jScrollBar_shilnistX.getValue()) % 5 >= 0 &&
                                    (i/ jScrollBar_shilnistX.getValue()) % 5 < 1
                                    && logikScaleX(i, -1, 1) ||
                                    (i/ jScrollBar_shilnistX.getValue()) % 5 >= 4 &&
                                            (i/ jScrollBar_shilnistX.getValue()) % 5 < 5
                                            && logikScaleX(i, 4, 6))
                                imgTemp[point] = Color.MAGENTA.getRGB();
                            break;

                        case 0:
                            if ((i/ jScrollBar_shilnistX.getValue()) % 5 >= 2 &&
                                    (i/ jScrollBar_shilnistX.getValue()) % 5 < 4
                                    && logikScaleX(i, 2, 4))
                                imgTemp[point] = Color.MAGENTA.getRGB();
                            break;

                    }
            }
        }
        draw_modifyImg.setImage(createImage(new MemoryImageSource(imgShirina, imgVusota, imgTemp, 0, imgShirina)), true);
    }

    private boolean logikScaleY(int target, int a, int b) {
        return target % (5 * jScrollBar_shilnistY.getValue()) > (a + 1) * jScrollBar_shilnistY.getValue() -
                jScrollBar_shilnistY.getValue() * jScrollBar_tolshina_linii.getValue() / 100
                && target % (5 * jScrollBar_shilnistY.getValue()) < (b - 1) * jScrollBar_shilnistY.getValue() +
                jScrollBar_shilnistY.getValue() * jScrollBar_tolshina_linii.getValue() / 100;
    }
    private boolean logikScaleX(int target, int a, int b) {
        return target % (5 * jScrollBar_shilnistX.getValue()) > (a + 1) * jScrollBar_shilnistX.getValue() -
                jScrollBar_shilnistX.getValue() * jScrollBar_tolshina_linii.getValue() / 100
                && target % (5 * jScrollBar_shilnistX.getValue()) < (b - 1) * jScrollBar_shilnistX.getValue() +
                jScrollBar_shilnistX.getValue() * jScrollBar_tolshina_linii.getValue() / 100;
    }


    public Krestiki(int width, int height, int[] razrez, Draw_ModifyImg draw_modifyImg) {
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
        jScrollBar_shilnistX = new JScrollBar(Adjustable.HORIZONTAL, 10, 1, 5, 21);
        jScrollBar_shilnistX.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistX = new JLabel();
        super.setScrollBar(new JLabel("Відстань між центрами по X"), jScrollBar_shilnistX, jLabel_shilnistX, 1);
        jScrollBar_shilnistY = new JScrollBar(Adjustable.HORIZONTAL, 10, 1, 5, 21);
        jScrollBar_shilnistY.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistY = new JLabel();
        super.setScrollBar(new JLabel("Відстань між центрами по Y"), jScrollBar_shilnistY, jLabel_shilnistY, 2);
        jScrollBar_zmishennaX = new JScrollBar(Adjustable.HORIZONTAL, 0, 1, 0, 41);
        jScrollBar_zmishennaX.addAdjustmentListener(myAdjusmentListener);
        jLabel_zmishennaX = new JLabel();
        super.setScrollBar(new JLabel("Початкове зміщення по X"), jScrollBar_zmishennaX, jLabel_zmishennaX, 3);
        jScrollBar_zmishennaY = new JScrollBar(Adjustable.HORIZONTAL, 0, 1, 0, 41);
        jScrollBar_zmishennaY.addAdjustmentListener(myAdjusmentListener);
        jLabel_zmishennaY = new JLabel();
        super.setScrollBar(new JLabel("Початкове зміщення по Y"), jScrollBar_zmishennaY, jLabel_zmishennaY, 4);
        jScrollBar_tolshina_linii = new JScrollBar(Adjustable.HORIZONTAL, 100, 1, 50, 101);
        jScrollBar_tolshina_linii.addAdjustmentListener(myAdjusmentListener);
        jLabel_tolshina_linii = new JLabel();
        super.setScrollBar(new JLabel("Розмір хрестика"), jScrollBar_tolshina_linii, jLabel_tolshina_linii, 5);
        jCheckBox_simetria.doClick();
        writeLabelText();
    }

    private void writeLabelText() {
        jLabel_shilnistX.setText((double) jScrollBar_shilnistX.getValue() * 2 / 5 + " мм");
        jLabel_shilnistY.setText((double) jScrollBar_shilnistY.getValue() * 2 / 5 + " мм");
        jLabel_zmishennaX.setText((double) jScrollBar_zmishennaX.getValue() / 5 + " мм");
        jLabel_zmishennaY.setText((double) jScrollBar_zmishennaY.getValue() / 5 + " мм");
        jLabel_tolshina_linii.setText((double) jScrollBar_tolshina_linii.getValue() + " %");
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
