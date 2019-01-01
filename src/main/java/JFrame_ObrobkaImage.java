import Config.Param_ObrabotkaImage;
import drawPanels.Draw_ModifyImg;
import drawPanels.Draw_RazrezEgg;
import upgradeImg.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.awt.Scrollbar.HORIZONTAL;


/**
 * Created with IntelliJ IDEA.
 * User: spasko
 * Date: 11.09.14
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
public class JFrame_ObrobkaImage extends JFrame {
	private static final long serialVersionUID = 1L;
    //Параметри та елементи вікна обробки

	JFrame_ObrobkaEgg jFrame_obrobkaEgg;

    JPanel mainPanel;
    JPanel jpanelKnopki;
    int vusotaOkna = Param_ObrabotkaImage.getInstance().getProperty(Param_ObrabotkaImage.VUSOTA_OKNA);
    int shirinaOkna = Param_ObrabotkaImage.getInstance().getProperty(Param_ObrabotkaImage.SHIRINA_OKNA_GRAFIC) +
            Param_ObrabotkaImage.getInstance().getProperty(Param_ObrabotkaImage.SHIRINA_OKNA_RAZREZ);
    int vusotaElementa = Param_ObrabotkaImage.getInstance().getProperty(Param_ObrabotkaImage.VUSOTA_ELEMENTA);
    JLabel jlabel_podpisTonalnist;
    JLabel jlabel_shisloTonalnist;

    JButton jbutton_zavantajitiZobrajenna;
    JButton jbutton_zberegtuZobrajenna;
    JButton jbutton_peredatuNaObrobku;

    JScrollBar jscrollbar_tonalnist;

    JTabbedPane jTabbedPane_variantuImg;

    Image imgOriginalneZobrajenna;
    Draw_ModifyImg draw_modifyImg;
    Draw_RazrezEgg draw_razrezEgg;
    private int[] razrez;
    int[] massivToshekDlaKartinki;
    int shirina;
    int vusota;
    UpgradeImg[] upgradeImg;
    String Filename = "";


    public JFrame_ObrobkaImage(int[] razrez) {
        this.razrez = new int[(razrez.length) / 2];
        for (int i = 0; i < razrez.length - 1; i += 2) {
            this.razrez[i / 2] = 350 - (razrez[i] + razrez[i + 1]) / 2;

        }
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
            setSizeFrame(this.razrez);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JFrame_ObrobkaImage(int[] razrez, JFrame_ObrobkaEgg jFrame_obrobkaEgg) {
        this(razrez);
        this.jFrame_obrobkaEgg = jFrame_obrobkaEgg;
        jFrame_obrobkaEgg.setEnabled(false);
    }

    private void jbInit() {

        //Основне вікно
        mainPanel = (JPanel) this.getContentPane();
        mainPanel.setLayout(null);
        this.setSize(new Dimension(shirinaOkna, vusotaOkna));
        mainPanel.setSize(new Dimension(shirinaOkna, vusotaOkna));
        this.setTitle("Сферичний плоттер V2, Обробка зображення");
        this.setResizable(false);


        draw_modifyImg = new Draw_ModifyImg();
        draw_modifyImg.setLayout(null);
        draw_modifyImg.setSize(shirinaOkna - 20, vusotaOkna - vusotaElementa * 9 - 30);
        draw_modifyImg.setLocation(0, 0);
        mainPanel.add(draw_modifyImg);
        draw_modifyImg.repaint();

        draw_razrezEgg = new Draw_RazrezEgg();
        draw_razrezEgg.setData(razrez);
        draw_razrezEgg.setLayout(null);
        draw_razrezEgg.setSize(20, vusotaOkna - vusotaElementa * 9 - 30);
        draw_razrezEgg.setLocation(shirinaOkna - Param_ObrabotkaImage.getInstance().getProperty(Param_ObrabotkaImage.SHIRINA_OKNA_RAZREZ), 0);
        mainPanel.add(draw_razrezEgg);
        draw_razrezEgg.repaint();

        jpanelKnopki = new JPanel(null);
        jpanelKnopki.setSize(new Dimension(shirinaOkna, vusotaElementa * 9));
        jpanelKnopki.setLocation(0, draw_modifyImg.getHeight());
        mainPanel.add(jpanelKnopki);

        //Скроли
        jscrollbar_tonalnist = new JScrollBar(HORIZONTAL, 50, 1, 20, 81);
        jscrollbar_tonalnist.setSize(3 * jpanelKnopki.getWidth() / 5, vusotaElementa);
        jscrollbar_tonalnist.setLocation(jpanelKnopki.getWidth() / 5, jpanelKnopki.getHeight() - 30 - 7 * vusotaElementa);
        jscrollbar_tonalnist.addAdjustmentListener(new AdjustmentListener() {
            public final void adjustmentValueChanged(AdjustmentEvent e) {
                jlabel_shisloTonalnist.setText(jscrollbar_tonalnist.getValue() + " %");
                displayKartinka();
            }
        });
        jpanelKnopki.add(jscrollbar_tonalnist);

        //Підписи
        jlabel_podpisTonalnist = new JLabel("Глибина відтінків", JLabel.RIGHT);
        jlabel_podpisTonalnist.setSize(jpanelKnopki.getWidth() / 5, vusotaElementa);
        jlabel_podpisTonalnist.setLocation(0, jpanelKnopki.getHeight() - 30 - 7 * vusotaElementa);
        jpanelKnopki.add(jlabel_podpisTonalnist);

        jlabel_shisloTonalnist = new JLabel(jscrollbar_tonalnist.getValue() + " %", JLabel.LEFT);
        jlabel_shisloTonalnist.setSize(jpanelKnopki.getWidth() / 5, vusotaElementa);
        jlabel_shisloTonalnist.setLocation(4 * jpanelKnopki.getWidth() / 5, jpanelKnopki.getHeight() - 30 - 7 * vusotaElementa);
        jpanelKnopki.add(jlabel_shisloTonalnist);

        //Кнопки
        jbutton_zavantajitiZobrajenna = new JButton("Завантажити зображення");
        jbutton_zavantajitiZobrajenna.setSize(jpanelKnopki.getWidth() / 3, vusotaElementa);
        jbutton_zavantajitiZobrajenna.setLocation(0, jpanelKnopki.getHeight() - 9 * vusotaElementa);
        jpanelKnopki.add(jbutton_zavantajitiZobrajenna);
        jbutton_zavantajitiZobrajenna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImage();
            }
        });

        jbutton_zberegtuZobrajenna = new JButton("Зберегти зображення");
        jbutton_zberegtuZobrajenna.setSize(jpanelKnopki.getWidth() / 3, vusotaElementa);
        jbutton_zberegtuZobrajenna.setLocation(jpanelKnopki.getWidth() / 3, jpanelKnopki.getHeight() - 9 * vusotaElementa);
        jpanelKnopki.add(jbutton_zberegtuZobrajenna);
        jbutton_zberegtuZobrajenna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImage();
            }
        });

        jbutton_peredatuNaObrobku = new JButton("Передати на обробку");
        jbutton_peredatuNaObrobku.setSize(jpanelKnopki.getWidth() / 3, vusotaElementa);
        jbutton_peredatuNaObrobku.setLocation(2 * jpanelKnopki.getWidth() / 3, jpanelKnopki.getHeight() - 9 * vusotaElementa);
        jpanelKnopki.add(jbutton_peredatuNaObrobku);
        jbutton_peredatuNaObrobku.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                peradatNaObrobku();
            }
        });

        //Панель закладок
        jTabbedPane_variantuImg = new JTabbedPane();
        upgradeImg = new UpgradeImg[]{
                new ScaleImg(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
                new Ravnomerno(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
                new Kontur(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
                new Napolnenie(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
                new Globys(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
                new Setka(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
                new UmenshenieLinii(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
                new Yasheiki(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
                new LineToPoints(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
                new Krestiki(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
                new GeometriaRavnomerno(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
                new Geometria(jpanelKnopki.getWidth(), jpanelKnopki.getHeight() - 2 * vusotaElementa, razrez, draw_modifyImg),
        };
        for (UpgradeImg ui : upgradeImg) {
            jTabbedPane_variantuImg.add(ui.getNameTabletPane(), ui);
        }
        jTabbedPane_variantuImg.setSize(jpanelKnopki.getWidth(), jpanelKnopki.getHeight());
        jTabbedPane_variantuImg.setLocation(0, 2 * vusotaElementa);
        jpanelKnopki.add(jTabbedPane_variantuImg);
        jTabbedPane_variantuImg.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                displayKartinka();

            }
        });

        jTabbedPane_variantuImg.setEnabled(false);
        jbutton_zberegtuZobrajenna.setEnabled(false);
        jscrollbar_tonalnist.setEnabled(false);
        jbutton_peredatuNaObrobku.setEnabled(false);
    }

    private void peradatNaObrobku() {
        jFrame_obrobkaEgg.loadImage(upgradeImg[jTabbedPane_variantuImg.getSelectedIndex()].getSummaImg());
        jFrame_obrobkaEgg.setEnabled(true);
        dispose();
    }

    public void setSizeFrame(int[] razrez) {
        vusotaOkna = razrez.length +
                jpanelKnopki.getHeight() + 45;
        this.setSize(new Dimension(shirinaOkna, vusotaOkna));
        mainPanel.setSize(new Dimension(shirinaOkna, vusotaOkna));
        draw_modifyImg.setSize(shirinaOkna - Param_ObrabotkaImage.getInstance().getProperty(Param_ObrabotkaImage.SHIRINA_OKNA_RAZREZ),
                razrez.length);
        draw_razrezEgg.setSize(Param_ObrabotkaImage.getInstance().getProperty(Param_ObrabotkaImage.SHIRINA_OKNA_RAZREZ),
                razrez.length);
        jpanelKnopki.setSize(new Dimension(shirinaOkna, vusotaElementa * 10));
        jpanelKnopki.setLocation(0, draw_modifyImg.getHeight());
        draw_razrezEgg.setData(razrez);
        this.setLocationRelativeTo(null);

    }


    //Реакція програми на закритття вікна
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (jFrame_obrobkaEgg != null) {
                jFrame_obrobkaEgg.setEnabled(true);
            }
            this.dispose();
        }
    }
    //Завантажити малюнок
    private void loadImage() {
        String szCurrentFilename;
        FileDialog fdlg;
        fdlg = new FileDialog(this, "Відкрити зображення", FileDialog.LOAD);
        fdlg.setLocationRelativeTo(null);
        fdlg.setDirectory("Kartinki");
        fdlg.setFile("*.bmp;*.jpg");
        fdlg.setVisible(true);
        szCurrentFilename = (fdlg.getDirectory() + fdlg.getFile());

        if (fdlg.getFile() != null) {
            Filename = fdlg.getFile();
            try {
                imgOriginalneZobrajenna = ImageIO.read(new File(szCurrentFilename));
                {
                    BufferedImage scaledBI = new BufferedImage(draw_modifyImg.getWidth(),
                            draw_modifyImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = scaledBI.createGraphics();
                    g.drawImage(imgOriginalneZobrajenna, 0, 0, draw_modifyImg.getWidth(),
                            draw_modifyImg.getHeight(), null);
                    g.dispose();
                    imgOriginalneZobrajenna = scaledBI;
                }
            } catch (IOException ignored) {
            }
            MediaTracker mt = new MediaTracker(this);
            mt.addImage(imgOriginalneZobrajenna, 0);
            try {
                mt.waitForAll();
            } catch (InterruptedException ignored) {
            }
            displayKartinka();
            jbutton_zberegtuZobrajenna.setEnabled(true);
            jTabbedPane_variantuImg.setEnabled(true);
            jscrollbar_tonalnist.setEnabled(true);
            if (jFrame_obrobkaEgg != null) {
                jbutton_peredatuNaObrobku.setEnabled(true);
            }
        }
    }

    //Створення та візуалізація малюнку для обробки
    private void displayKartinka() {
        Image imgDlaObrobki = null;
        shirina = draw_modifyImg.getWidth();
        vusota = draw_modifyImg.getHeight();


        massivToshekDlaKartinki = new int[vusota * shirina];
        try {
            PixelGrabber pg = new PixelGrabber(imgOriginalneZobrajenna, 0, 0, shirina, vusota, massivToshekDlaKartinki, 0, shirina);
            pg.grabPixels();
        } catch (InterruptedException ignored) {
        }

        for (int j = 0; j < shirina; j++) {
            for (int k = 0; k < vusota; k++) {
                int p = massivToshekDlaKartinki[k * shirina + j];
                int r = 0xff & (p >> 16);
                int g = 0xff & (p >> 8);
                int b = 0xff & (p);
                if (r + b + g < 255 * 3 * jscrollbar_tonalnist.getValue() / 100) {
                    r = b = g = 0;
                } else r = g = b = 255;
                massivToshekDlaKartinki[k * shirina + j] = (0Xff000000 | r << 16 | g << 8 | b);
            }
        }
        imgDlaObrobki = createImage(new MemoryImageSource(shirina, vusota, massivToshekDlaKartinki, 0, shirina));
        for (UpgradeImg ui : upgradeImg) {
            ui.setImage(imgDlaObrobki);
        }
        // if (upgradeImg[jTabbedPane_variantuImg.getSelectedIndex()].getClass()!=ScaleImg.class)
        draw_modifyImg.setImage(upgradeImg[0].getSummaImg(), true);
        // else draw_modifyImg.setImage(imgDlaObrobki, true);
        if (upgradeImg[jTabbedPane_variantuImg.getSelectedIndex()].getClass() != ScaleImg.class)
            upgradeImg[jTabbedPane_variantuImg.getSelectedIndex()].setImage(upgradeImg[0].getSummaImg());
        else upgradeImg[jTabbedPane_variantuImg.getSelectedIndex()].setImage(imgDlaObrobki);

        upgradeImg[jTabbedPane_variantuImg.getSelectedIndex()].createPoints();
        upgradeImg[jTabbedPane_variantuImg.getSelectedIndex()].drawPoints();
    }


    //Збереження малюнку
    private void saveImage() {
        FileDialog fdlg;
        fdlg = new FileDialog(this, "Збереження зображення", FileDialog.SAVE);
        fdlg.setLocationRelativeTo(null);
        fdlg.setDirectory("Кartinki-bmp save");
        fdlg.setFile(Filename.replace(Filename.substring(Filename.length() - 4), "")
                + "(ширина-" + imgOriginalneZobrajenna.getHeight(null) + "_" +
                (new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(Calendar.getInstance().getTime())) + ").bmp");
        fdlg.setVisible(true);
        if (fdlg.getFile() == null) return;
        try {
            BufferedImage bi = imageToBufferedImage(upgradeImg[jTabbedPane_variantuImg.getSelectedIndex()].getSummaImg());
            File outputfile = new File(fdlg.getDirectory() + "/" + fdlg.getFile());
            ImageIO.write(bi, "bmp", outputfile);
        } catch (IOException ignored) {
        }
    }


    private BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bi = new BufferedImage
                (im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }


}
