package upgradeImg;

import static utils.ImageProportionsUtil.*;

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

public class UmenshenieLinii extends UpgradeImg {
    private JCheckBox jCheckBox_simetria = new JCheckBox("Симетрія (відстань між точками по X та Y однакова)");
    private JScrollBar jScrollBar_smugaX;
    private JScrollBar jScrollBar_smugaY;

    private JLabel jLabel_smugaX;
    private JLabel jLabel_smugaY;

    private MyAdjusmentListener myAdjusmentListener = new MyAdjusmentListener();

    @Override
    public String getNameTabletPane() {
        return "Ширина ліній";
    }

    @Override
    public Image getSummaImg() {
        Image result=draw_modifyImg.getImg();
        int xLine = getMashtabedValueX(jScrollBar_smugaX.getValue())/10 - getMashtabedValueX(jScrollBar_smugaX.getMaximum()) / 20;
        int yLine = getMashtabedValueY(jScrollBar_smugaY.getValue())/10 - getMashtabedValueY(jScrollBar_smugaY.getMaximum()) / 20;
        if (xLine>0&&yLine>0)
            result=killColor(result,Color.MAGENTA,Color.WHITE);
        if (xLine<0&&yLine<0)
            result=killColor(result,Color.MAGENTA,Color.BLACK);
        return result;
    }

    @Override
    public void createPoints() {
        int[] imgTemp = imgStart.clone();
        int y;
        int x;
        int xLine = getMashtabedValueX(jScrollBar_smugaX.getValue())/10 - getMashtabedValueX(jScrollBar_smugaX.getMaximum()) / 20;
        int yLine = getMashtabedValueY(jScrollBar_smugaY.getValue())/10 - getMashtabedValueY(jScrollBar_smugaY.getMaximum()) / 20;
        int[] imgTempPopravka = imgStart.clone();
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


        for (int j = 0; j < imgTempPopravka.length; j++) {
            if (imgTempPopravka[j] != Color.BLACK.getRGB()) continue;
            x = j % imgShirina;
            y = (j - x) / imgShirina;
            for (int k = -Math.abs(yLine); k < Math.abs(yLine); k++) {
                for (int m = -Math.abs(xLine); m < Math.abs(xLine); m++) {
                    if (Point.distance(0, 0, m, k * Math.abs(xLine) / Math.abs(yLine == 0 ? 1 : xLine)) > Math.abs(xLine) &&
                            Point.distance(0, 0, m * Math.abs(yLine) / Math.abs(xLine == 0 ? 1 : yLine), k) > Math.abs(yLine)
                            ) continue;
                    if (((y + k) * imgShirina + (x + m) >= 0) && ((y + k) * imgShirina + (x + m) < imgStart.length)) {
                        if (imgTemp[(y + k) * imgShirina + (x + m)] == Color.BLACK.getRGB()&&xLine>0&&yLine>0)
                            imgTemp[(y + k) * imgShirina + (x + m)] = Color.MAGENTA.getRGB();
                        if (imgTemp[(y + k) * imgShirina + (x + m)] == Color.WHITE.getRGB()&&xLine<0&&yLine<0)
                            imgTemp[(y + k) * imgShirina + (x + m)] = Color.MAGENTA.getRGB();
                    }

                }
            }
        }
        draw_modifyImg.setImage(createImage(new MemoryImageSource(imgShirina, imgVusota, imgTemp, 0, imgShirina)), true);
    }


    public UmenshenieLinii(int width, int height, int[] razrez, Draw_ModifyImg draw_modifyImg) {
        super(width, height, razrez, draw_modifyImg);
        jCheckBox_simetria.setSize(width, height / 7);
        jCheckBox_simetria.setLocation(0, 0);
        jCheckBox_simetria.setHorizontalAlignment(SwingConstants.CENTER);
        jCheckBox_simetria.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (((JCheckBox) e.getItem()).isSelected()) {
                    jScrollBar_smugaY.setValue(jScrollBar_smugaX.getValue());
                    jScrollBar_smugaY.setEnabled(false);
                } else jScrollBar_smugaY.setEnabled(true);
            }
        });
        add(jCheckBox_simetria);
        jScrollBar_smugaX = new JScrollBar(Adjustable.HORIZONTAL, 100, 10, 0, 210);
        jScrollBar_smugaX.addAdjustmentListener(myAdjusmentListener);
        jLabel_smugaX = new JLabel();
        super.setScrollBar(new JLabel("Ширина перемички по X"), jScrollBar_smugaX, jLabel_smugaX, 1);
        jScrollBar_smugaY = new JScrollBar(Adjustable.HORIZONTAL, 100, 10, 0, 210);
        jScrollBar_smugaY.addAdjustmentListener(myAdjusmentListener);
        jLabel_smugaY = new JLabel();
        super.setScrollBar(new JLabel("Ширина переички по Y"), jScrollBar_smugaY, jLabel_smugaY, 2);
        jCheckBox_simetria.doClick();
        writeLabelText();
    }

    private void writeLabelText() {
        jLabel_smugaX.setText((double) (jScrollBar_smugaX.getValue() - jScrollBar_smugaX.getMaximum() / 2) / 50 + " мм");
        jLabel_smugaY.setText((double) (jScrollBar_smugaY.getValue() - jScrollBar_smugaY.getMaximum() / 2) / 50 + " мм");

    }

    private class MyAdjusmentListener implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (jCheckBox_simetria.isSelected()) {
                jScrollBar_smugaY.setValue(jScrollBar_smugaX.getValue());
            }
            writeLabelText();
            createPoints();
        }
    }
}
