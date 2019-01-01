package upgradeImg;

import drawPanels.Draw_ModifyImg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.MemoryImageSource;

/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 20.07.15
 * Time: 20:00
 * To change this template use File | Settings | File Templates.
 */
public class Setka extends UpgradeImg {
    private JCheckBox jCheckBox_simetria = new JCheckBox("Симетрія (відстань між точками по X та Y однакова)");
    private JScrollBar jScrollBar_shirinaPeremushkiX;
    private JScrollBar jScrollBar_shirinaPeremushkiY;
    private JScrollBar jScrollBar_kolishetstvoSectorovX;
    private JScrollBar jScrollBar_kolishetstvoSectorovY;
    private JLabel jLabel_shilnistX;
    private JLabel jLabel_shilnistY;
    private JLabel jLabel_zmishennaX;
    private JLabel jLabel_zmishennaY;
    private MyAdjusmentListener myAdjusmentListener = new MyAdjusmentListener();

    @Override
    public String getNameTabletPane() {
        return "Поєднуюча сітка";
    }

    @Override
    public Image getSummaImg() {
        return killColor(draw_modifyImg.getImg(),Color.MAGENTA,Color.WHITE);
    }

    @Override
    public void createPoints() {
        int[] imgTemp = imgStart.clone();
        int y;
        int x;
        int xSec = (imgShirina - jScrollBar_shirinaPeremushkiX.getValue() * jScrollBar_kolishetstvoSectorovX.getValue())
                / jScrollBar_kolishetstvoSectorovX.getValue();
        int ySec = (imgVusota - jScrollBar_shirinaPeremushkiY.getValue() * (jScrollBar_kolishetstvoSectorovY.getValue() - 1))
                / jScrollBar_kolishetstvoSectorovY.getValue();
        int maxRazrez = getMaxRazrez(razrez);
        outer:
        for (int i = 0; i < imgStart.length; i++) {
            x = i % imgShirina;
            y = (i - x) / imgShirina;
            for (int k = 1; k <= jScrollBar_kolishetstvoSectorovX.getValue(); k++) {
                if (imgStart[i] == Color.WHITE.getRGB()) continue outer;

                int ySecDelta = jScrollBar_shirinaPeremushkiX.getValue() * maxRazrez / razrez[y] / 2;
                if (x < (2 * k - 1) * jScrollBar_shirinaPeremushkiX.getValue() / 2 +
                        ySecDelta +
                        (k - 1) * xSec &&
                        x > ((2 * k - 1) * jScrollBar_shirinaPeremushkiX.getValue() / 2 -
                                ySecDelta +
                                (k-1) * xSec)) {
                    imgTemp[i] = Color.MAGENTA.getRGB();
                    continue outer;

                }
            }
            for (int z = 1; z <= jScrollBar_kolishetstvoSectorovY.getValue(); z++) {

                if (y > (z - 2) * jScrollBar_shirinaPeremushkiY.getValue() + (z - 1) * ySec &&
                        y < ((z - 1) * jScrollBar_shirinaPeremushkiY.getValue() +
                                (z-1) * ySec)) {
                    imgTemp[i] = Color.MAGENTA.getRGB();
                    continue outer;
                }
            }
        }
        draw_modifyImg.setImage(createImage(new MemoryImageSource(imgShirina, imgVusota, imgTemp, 0, imgShirina)), true);
    }


    public Setka(int width, int height, int[] razrez, Draw_ModifyImg draw_modifyImg) {
        super(width, height, razrez, draw_modifyImg);
        jCheckBox_simetria.setSize(width, height / 7);
        jCheckBox_simetria.setLocation(0, 0);
        jCheckBox_simetria.setHorizontalAlignment(SwingConstants.CENTER);
        jCheckBox_simetria.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (((JCheckBox) e.getItem()).isSelected()) {
                    jScrollBar_shirinaPeremushkiY.setValue(jScrollBar_shirinaPeremushkiX.getValue());
                    jScrollBar_shirinaPeremushkiY.setEnabled(false);
                } else jScrollBar_shirinaPeremushkiY.setEnabled(true);
            }
        });
        add(jCheckBox_simetria);
        jScrollBar_shirinaPeremushkiX = new JScrollBar(Adjustable.HORIZONTAL, 10, 1, 5, 101);
        jScrollBar_shirinaPeremushkiX.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistX = new JLabel();
        super.setScrollBar(new JLabel("Ширина перемички по X"), jScrollBar_shirinaPeremushkiX, jLabel_shilnistX, 1);
        jScrollBar_shirinaPeremushkiY = new JScrollBar(Adjustable.HORIZONTAL, 10, 1, 5, 101);
        jScrollBar_shirinaPeremushkiY.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistY = new JLabel();
        super.setScrollBar(new JLabel("Ширина переички по Y"), jScrollBar_shirinaPeremushkiY, jLabel_shilnistY, 2);
        jScrollBar_kolishetstvoSectorovX = new JScrollBar(Adjustable.HORIZONTAL, 12, 1, 4, 17);
        jScrollBar_kolishetstvoSectorovX.addAdjustmentListener(myAdjusmentListener);
        jLabel_zmishennaX = new JLabel();
        super.setScrollBar(new JLabel("Кількість секторів по X (на оберт)"), jScrollBar_kolishetstvoSectorovX, jLabel_zmishennaX, 3);
        jScrollBar_kolishetstvoSectorovY = new JScrollBar(Adjustable.HORIZONTAL, 4, 1, 1, 11);
        jScrollBar_kolishetstvoSectorovY.addAdjustmentListener(myAdjusmentListener);
        jLabel_zmishennaY = new JLabel();
        super.setScrollBar(new JLabel("Кількість секторів по Y"), jScrollBar_kolishetstvoSectorovY, jLabel_zmishennaY, 4);
        jCheckBox_simetria.doClick();
        writeLabelText();
    }

    private void writeLabelText() {
        jLabel_shilnistX.setText((double) (jScrollBar_shirinaPeremushkiX.getValue()) / 5 + " мм");
        jLabel_shilnistY.setText((double) (jScrollBar_shirinaPeremushkiY.getValue()) / 5 + " мм");
        jLabel_zmishennaX.setText((double) (jScrollBar_kolishetstvoSectorovX.getValue()) + " сектoрів");
        jLabel_zmishennaY.setText((double) (jScrollBar_kolishetstvoSectorovY.getValue()) + " сектoрів");
    }

    private class MyAdjusmentListener implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (jCheckBox_simetria.isSelected()) {
                jScrollBar_shirinaPeremushkiY.setValue(jScrollBar_shirinaPeremushkiX.getValue());
            }
            writeLabelText();
            createPoints();
        }
    }
}
