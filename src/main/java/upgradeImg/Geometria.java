package upgradeImg;

import drawPanels.Draw_ModifyImg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Serg
 * Date: 20.07.15
 * Time: 20:00
 * To change this template use File | Settings | File Templates.
 */
public class Geometria extends UpgradeImg {
    private JCheckBox jCheckBox_simetria = new JCheckBox("Симетрія (відстань між точками по X та Y однакова)");
    private JCheckBox jCheckBox_povorot = new JCheckBox("Випадкові повороти складових зображень");
    private JCheckBox jCheckBox_inversia = new JCheckBox("Інверсне застосування зображень");
    private JButton jButton_katalogImg = new JButton("Задати набір зображень");
    private JScrollBar jScrollBar_shilnistX;
    private JScrollBar jScrollBar_shilnistY;
    private JScrollBar jScrollBar_razmerImages;
    private JScrollBar jScrollBar_razbrosRazmera;
    private JLabel jLabel_pathKatalogImg = new JLabel("Вкажіть каталог з зображеннями");
    private JLabel jLabel_razmerImages;
    private JLabel jLabel_shilnistX;
    private JLabel jLabel_shilnistY;
    private JLabel jLabel_razbrosRazmera;
    private MyAdjusmentListener myAdjusmentListener = new MyAdjusmentListener();
    private Image[] clusterImages;

    @Override
    public String getNameTabletPane() {
        return "Геометрія";
    }

    @Override
    public Image getSummaImg() {
        if (!jCheckBox_inversia.isSelected()) return killColor(draw_modifyImg.getImg(), Color.MAGENTA, Color.WHITE);
        else return killColor(killColor(draw_modifyImg.getImg(), Color.BLACK, Color.WHITE), Color.MAGENTA, Color.BLACK);
    }

    @Override
    public void createPoints() {
        int[] imgTemp = imgStart.clone();
        int[] imgEnd = new int[imgStart.length];
        ArrayList<Point> tmpPoints;

        points = new ArrayList<Point>();
        int i = 0;
        int max = getMaxRazrez(razrez);
        Point tmpPoint;
        Random random = new Random();
        boolean original;
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

        points = new ArrayList<Point>();

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
        for (int povtor = 0; povtor < 10; povtor++) {
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
                outer:
                for (int delta = -2; delta < 2 * Math.max(jScrollBar_shilnistY.getValue(), jScrollBar_shilnistX.getValue()); delta += 2) {
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
        ArrayList<Point> tmpPoints2;
        Point pointA;
        Point pointB;
        Point pointC;
        Point pointD;
        for (int povtor = 0; povtor < 10; povtor++) {
            tmpPoints2 = new ArrayList<Point>();
            int logik;
            for (Point p : tmpPoints) {
                logik = 0;
                pointA = new Point(p.x + 1, p.y);
                pointB = new Point(p.x - 1, p.y);
                pointC = new Point(p.x, p.y + 1);
                pointD = new Point(p.x, p.y - 1);
                imgTemp[imgShirina * p.y + p.x] = 0;
                outer:
                for (int delta = -Math.min(jScrollBar_shilnistY.getMinimum(), jScrollBar_shilnistX.getMinimum());
                     delta < 2 * Math.max(jScrollBar_shilnistY.getValue(), jScrollBar_shilnistX.getValue()); delta += 2) {
                    for (int q = -(jScrollBar_shilnistX.getValue() + delta) * max / razrez[p.y] / 2;
                         q <= (jScrollBar_shilnistX.getValue() + delta) * max / razrez[p.y] / 2; q++) {

                        for (int w = -(jScrollBar_shilnistY.getValue() + delta) / 2;
                             w <= (jScrollBar_shilnistY.getValue() + delta) / 2; w++) {
                            if (sravnenieSveta(imgTemp, pointA.x, pointA.y, q, w, 100) &&
                                    sravnenieSveta(imgTemp, pointB.x, pointB.y, -1, 0, 0)) {
                                logik = 2;
                                break outer;
                            }
                            if (sravnenieSveta(imgTemp, pointB.x, pointB.y, q, w, 100) &&
                                    sravnenieSveta(imgTemp, pointA.x, pointA.y, 1, 0, 0)) {
                                logik = 1;
                                break outer;
                            }
                            if (sravnenieSveta(imgTemp, pointC.x, pointC.y, q, w, 100) &&
                                    sravnenieSveta(imgTemp, pointD.x, pointD.y, 0, -1, 0)) {
                                logik = 4;
                                break outer;
                            }
                            if (sravnenieSveta(imgTemp, pointD.x, pointD.y, q, w, 100) &&
                                    sravnenieSveta(imgTemp, pointC.x, pointC.y, 0, 1, 0)) {
                                logik = 3;
                                break outer;
                            }
                        }
                    }
                }
                try {
                    switch (logik) {
                        case 1:
                            imgTemp[imgShirina * pointA.y + pointA.x] = 100;
                            tmpPoints2.add(pointA);
                            break;
                        case 2:
                            imgTemp[imgShirina * pointB.y + pointB.x] = 100;
                            tmpPoints2.add(pointB);
                            break;
                        case 3:
                            imgTemp[imgShirina * pointC.y + pointC.x] = 100;
                            tmpPoints2.add(pointC);
                            break;
                        case 4:
                            imgTemp[imgShirina * pointD.y + pointD.x] = 100;
                            tmpPoints2.add(pointD);
                            break;
                        case 0:
                            imgTemp[imgShirina * p.y + p.x] = 100;
                            tmpPoints2.add(p);
                            break;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    imgTemp[imgShirina * p.y + p.x] = 100;
                    tmpPoints2.add(p);
                }
            }
            tmpPoints = new ArrayList<Point>(tmpPoints2);
        }
        for (Point p : tmpPoints) {
            points.add(p);
        }
        Point[] pointsArray = new Point[points.size()];
        points.toArray(pointsArray);


        points = new ArrayList<Point>();
        Image imageResult = createImage(imgShirina, imgVusota);
        int[] temp = new int[imgStart.length];
        Graphics2D g = (Graphics2D) imageResult.getGraphics();
        Random r = new Random();
        g.setColor(Color.WHITE);

        if (clusterImages != null && clusterImages.length > 0)
            for (Point aPointsArray : pointsArray) {
                g.fillRect(0, 0, imgShirina, imgVusota);
                double tetra = 0;
                int prosent =
                        jScrollBar_razbrosRazmera.getValue() / 2 - r.nextInt(jScrollBar_razbrosRazmera.getValue());
                Image tempImg = clusterImages[r.nextInt(clusterImages.length)];
                if (jCheckBox_povorot.isSelected()) {
                    tetra = r.nextInt(360);
                    tempImg = rotator(tempImg, tetra);
                    g.drawImage(tempImg,
                            aPointsArray.x - (2*jScrollBar_razmerImages.getValue() / 2 + jScrollBar_razmerImages.getValue() * prosent / 200) * max / razrez[aPointsArray.y],
                            aPointsArray.y - (2*jScrollBar_razmerImages.getValue() / 2 + jScrollBar_razmerImages.getValue() * prosent / 200),
                            (2*jScrollBar_razmerImages.getValue() + jScrollBar_razmerImages.getValue() * prosent / 100) * max / razrez[aPointsArray.y],
                            (2*jScrollBar_razmerImages.getValue() + jScrollBar_razmerImages.getValue() * prosent / 100),
                            null);

                } else
                    g.drawImage(tempImg,
                            aPointsArray.x - (jScrollBar_razmerImages.getValue() / 2 + jScrollBar_razmerImages.getValue() * prosent / 200) * max / razrez[aPointsArray.y],
                            aPointsArray.y - (jScrollBar_razmerImages.getValue() / 2 + jScrollBar_razmerImages.getValue() * prosent / 200),
                            (jScrollBar_razmerImages.getValue() + jScrollBar_razmerImages.getValue() * prosent / 100) * max / razrez[aPointsArray.y],
                            (jScrollBar_razmerImages.getValue() + jScrollBar_razmerImages.getValue() * prosent / 100),
                            null);

                try {
                    PixelGrabber pg = new PixelGrabber(imageResult, 0, 0, imgShirina, imgVusota, temp, 0, imgShirina);
                    pg.grabPixels();
                } catch (InterruptedException ignored) {
                }
                for (int z = 0; z < imgStart.length; z++) {
                    if (imgStart[z] == Color.BLACK.getRGB() && temp[z] == Color.BLACK.getRGB())
                        imgEnd[z] = Color.MAGENTA.getRGB();
                }

            }

        for (int z = 0; z < imgStart.length; z++) {
            if (imgStart[z] != Color.BLACK.getRGB()) imgEnd[z] = Color.WHITE.getRGB();
            else if (imgEnd[z] != Color.MAGENTA.getRGB())
                imgEnd[z] = Color.BLACK.getRGB();

        }
        draw_modifyImg.setImage(createImage(new MemoryImageSource(imgShirina, imgVusota, imgEnd, 0, imgShirina)), true);
    }


    public Geometria(int width, int height, int[] razrez, Draw_ModifyImg draw_modifyImg) {
        super(width, height, razrez, draw_modifyImg);
        jButton_katalogImg.setSize(width / 3, height / 7);
        jButton_katalogImg.setLocation(0, 0);
        jButton_katalogImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });
        add(jButton_katalogImg);
        jLabel_pathKatalogImg.setSize(2 * width / 3, height / 7);
        jLabel_pathKatalogImg.setLocation(width / 3, 0);
        add(jLabel_pathKatalogImg);
        jCheckBox_simetria.setSize(width / 3, height / 7);
        jCheckBox_simetria.setLocation(0, height / 7);
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
        jCheckBox_povorot.setSize(width / 3, height / 7);
        jCheckBox_povorot.setLocation(width / 3, height / 7);
        jCheckBox_povorot.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                writeLabelText();
                createPoints();
                drawPoints();
            }
        });
        add(jCheckBox_povorot);
        jCheckBox_inversia.setSize(width / 3, height / 7);
        jCheckBox_inversia.setLocation(2 * width / 3, height / 7);
        add(jCheckBox_inversia);
        jScrollBar_shilnistX = new JScrollBar(Adjustable.HORIZONTAL, 60, 1, 30, 81);
        jScrollBar_shilnistX.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistX = new JLabel();
        super.setScrollBar(new JLabel("Відстань між центрами по X"), jScrollBar_shilnistX, jLabel_shilnistX, 2);
        jScrollBar_shilnistY = new JScrollBar(Adjustable.HORIZONTAL, 60, 1, 30, 81);
        jScrollBar_shilnistY.addAdjustmentListener(myAdjusmentListener);
        jLabel_shilnistY = new JLabel();
        super.setScrollBar(new JLabel("Відстань між центрами по Y"), jScrollBar_shilnistY, jLabel_shilnistY, 3);
        jScrollBar_razmerImages = new JScrollBar(Adjustable.HORIZONTAL, 40, 1, 30, 81);
        jScrollBar_razmerImages.addAdjustmentListener(myAdjusmentListener);
        jLabel_razmerImages = new JLabel();
        super.setScrollBar(new JLabel("Розмір малюнків"), jScrollBar_razmerImages, jLabel_razmerImages, 4);
        jScrollBar_razbrosRazmera = new JScrollBar(Adjustable.HORIZONTAL, 1, 1, 1, 52);
        jScrollBar_razbrosRazmera.addAdjustmentListener(myAdjusmentListener);
        jLabel_razbrosRazmera = new JLabel();
        super.setScrollBar(new JLabel("Розкид розмірів"), jScrollBar_razbrosRazmera, jLabel_razbrosRazmera, 5);


        jCheckBox_simetria.doClick();
        writeLabelText();
    }

    private void writeLabelText() {
        jLabel_shilnistX.setText((double) jScrollBar_shilnistX.getValue() / 5 + " мм");
        jLabel_shilnistY.setText((double) jScrollBar_shilnistY.getValue() / 5 + " мм");
        jLabel_razmerImages.setText((double) jScrollBar_razmerImages.getValue() / 5 + " мм");
        jLabel_razbrosRazmera.setText(jScrollBar_razbrosRazmera.getValue() - 1 + " %");
    }

    private class MyAdjusmentListener implements AdjustmentListener {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if ((e.getSource().equals(jScrollBar_shilnistY) && !jCheckBox_simetria.isSelected())
                    || !e.getSource().equals(jScrollBar_shilnistY)) {
                if (jCheckBox_simetria.isSelected()) {
                    jScrollBar_shilnistY.setValue(jScrollBar_shilnistX.getValue());
                }
                writeLabelText();
                createPoints();
                drawPoints();
            }
        }
    }

    private void loadImage() {

        FileDialog fdlg;
        fdlg = new FileDialog(new JFrame(), "Виберіть необхідні зображення", FileDialog.LOAD);
        fdlg.setMultipleMode(true);
        fdlg.setLocationRelativeTo(null);
        fdlg.setDirectory("Kartinki");
        fdlg.setFile("*.bmp;*.jpg");
        fdlg.setVisible(true);
        Image imgTemp;
        BufferedImage scaledBI;
        String strTmp = "Вибрано: ";
        if (fdlg.getFiles() != null && fdlg.getFiles().length > 0) {
            clusterImages = new Image[fdlg.getFiles().length];
            for (int i = 0; i < fdlg.getFiles().length; i++) {

                try {
                    imgTemp = ImageIO.read(fdlg.getFiles()[i]);
                    strTmp += fdlg.getFiles()[i].getName().substring(0, fdlg.getFiles()[i].getName().length() - 4) + "; ";
                    scaledBI = new BufferedImage(300,
                            300, BufferedImage.TYPE_INT_ARGB);

                    Graphics2D g = scaledBI.createGraphics();
                    g.drawImage(imgTemp, 0, 0, scaledBI.getWidth(),
                            scaledBI.getHeight(), null);
                    g.dispose();
                    clusterImages[i] = scaledBI;
                    jLabel_pathKatalogImg.setText(strTmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            writeLabelText();
            createPoints();
            drawPoints();
        }
    }

    private Image rotator(Image bi, double theta) {
        int w = 2 * bi.getWidth(null);
        int h = 2 * bi.getHeight(null);

        Image imageResult = createImage(w, h);
        Graphics2D g = (Graphics2D) imageResult.getGraphics();

        AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(theta), w / 2, h / 2);
        g.setTransform(at);

        g.drawImage(bi,
                w / 4,
                h / 4,
                w / 2,
                h / 2,
                null);
        return imageResult;
    }
}
