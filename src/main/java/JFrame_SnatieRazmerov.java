import static java.awt.Scrollbar.HORIZONTAL;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JToggleButton;

import Config.Param_Profile;
import Config.Param_SnatieRazmerov;
import drawPanels.Draw_SnatieRazmerov;
import utils.FileUtils;

/**
 * Created with IntelliJ IDEA.
 * User: spasko
 * Date: 09.09.14
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
class JFrame_SnatieRazmerov extends JFrame {

	private static final long serialVersionUID = 1L;
	//Параметри та елементи выкна вимірів
    JPanel mainPanel;
    int polosaOgranishenia = Param_SnatieRazmerov.getInstance().getProperty(Param_SnatieRazmerov.POLOSA_OGRANISHENIA);
    int vusotaElementa = Param_SnatieRazmerov.getInstance().getProperty(Param_SnatieRazmerov.VUSOTA_ELEMENTA);
    int vusotaOkna = Param_SnatieRazmerov.getInstance().getProperty(Param_SnatieRazmerov.VUSOTA_OKNA) + vusotaElementa * 5;
    int shirinaOkna = Param_SnatieRazmerov.getInstance().getProperty(Param_SnatieRazmerov.SHIRINA_OKNA) - 2 * polosaOgranishenia * 10;

    int razmerZagotovki;
    int procent;
    int[][] pomeraniRazmeru;
    JLabel jlabel_podpisShirina;
    JLabel jlabel_shisloShirina;
    JLabel jlabel_podpisChustvitelnost;
    JLabel jlabel_shisloChustvitelnost;
    JButton jbutton_poshatuVumiru;
    JButton jbutton_pausaVumiru;
    JButton jbutton_stopVumiru;
    JButton jbutton_perehodDoObrabotki;
    JButton jbutton_zminutuImg;
    JToggleButton jbutton_rushneUpravlinna;
    JScrollBar jscrollbar_shirinaZagotovki;
    JScrollBar jscrollBar_chustvitelnostVumiruvanna;
    JProgressBar jprogressbar_vumiruvanna;
    Zapusk zapusk;
    Draw_SnatieRazmerov drawPanel;
    Thread potokVumiruvanna;
    boolean nowDoing = false;
    boolean canResiveImage = false;
    int razrez[];
    
    static int koefValue;
    
    static{
    	if (Param_Profile.isSecondGeneration()){
    		koefValue=12;
    	} else{
    		koefValue=10;
    	}
    }

    public JFrame_SnatieRazmerov(Zapusk zapusk) {
        this.zapusk = zapusk;
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
            loadRazrezFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCanResiveImage(boolean canResiveImage) {
        this.canResiveImage = canResiveImage;
    }

    private void jbInit() {

        //Основне вікно
        mainPanel = (JPanel) this.getContentPane();
        mainPanel.setLayout(null);
        this.setSize(new Dimension(shirinaOkna, vusotaOkna));
        mainPanel.setSize(new Dimension(shirinaOkna, vusotaOkna));
        this.setTitle("Сферичний плоттер V2, зняття розмірів");
        this.setResizable(false);


        //Скрол
        jscrollbar_shirinaZagotovki = new JScrollBar(HORIZONTAL, 50, 1, 20, 101);
        jscrollbar_shirinaZagotovki.setSize(3 * mainPanel.getWidth() / 5, vusotaElementa);
        jscrollbar_shirinaZagotovki.setLocation(mainPanel.getWidth() / 5, mainPanel.getHeight() - 30 - 6 * vusotaElementa);
        jscrollbar_shirinaZagotovki.addAdjustmentListener(new AdjustmentListener() {
            public final void adjustmentValueChanged(AdjustmentEvent e) {
                jlabel_shisloShirina.setText(jscrollbar_shirinaZagotovki.getValue() +(Param_Profile.isSecondGeneration()?-8:0)+ " мм");
                drawPanel.setLocation((shirinaOkna - (jscrollbar_shirinaZagotovki.getValue() * koefValue - 2 * polosaOgranishenia * koefValue)) / 2 - 4, 0);
                razmerZagotovki = jscrollbar_shirinaZagotovki.getValue() * koefValue - 2 * polosaOgranishenia * koefValue + 1;
                drawPanel.setSize(razmerZagotovki, vusotaOkna - 6 * vusotaElementa - 30);
                pomeraniRazmeru = new int[razmerZagotovki][6];
                drawPanel.massivKoordinat = new int[razmerZagotovki][5];
                jbutton_perehodDoObrabotki.setEnabled(false);
                jbutton_zminutuImg.setEnabled(false);
            }
        });
        razmerZagotovki = koefValue * jscrollbar_shirinaZagotovki.getValue() - 2 * polosaOgranishenia * koefValue + 1;
        mainPanel.add(jscrollbar_shirinaZagotovki);

        jscrollBar_chustvitelnostVumiruvanna = new JScrollBar(HORIZONTAL, 3, 1, 3, 11);
        jscrollBar_chustvitelnostVumiruvanna.setSize(3 * mainPanel.getWidth() / 5, vusotaElementa);
        jscrollBar_chustvitelnostVumiruvanna.setLocation(mainPanel.getWidth() / 5, mainPanel.getHeight() - 30 - 5 * vusotaElementa);
        jscrollBar_chustvitelnostVumiruvanna.addAdjustmentListener(new AdjustmentListener() {
            public final void adjustmentValueChanged(AdjustmentEvent e) {
                jlabel_shisloChustvitelnost.setText((double) jscrollBar_chustvitelnostVumiruvanna.getValue() / 10 + " мм по Z");
                zapusk.dvijenie.shustvitelnostVumiruvanna = jscrollBar_chustvitelnostVumiruvanna.getValue();
            }
        });
        mainPanel.add(jscrollBar_chustvitelnostVumiruvanna);

        //Підписи
        jlabel_podpisShirina = new JLabel("Ширина", JLabel.CENTER);
        jlabel_podpisShirina.setSize(mainPanel.getWidth() / 5, vusotaElementa);
        jlabel_podpisShirina.setLocation(0, mainPanel.getHeight() - 30 - 6 * vusotaElementa);
        mainPanel.add(jlabel_podpisShirina);

        jlabel_shisloShirina = new JLabel(jscrollbar_shirinaZagotovki.getValue() +(Param_Profile.isSecondGeneration()?-8:0) + " мм", JLabel.CENTER);
        jlabel_shisloShirina.setSize(mainPanel.getWidth() / 5, vusotaElementa);
        jlabel_shisloShirina.setLocation(4 * mainPanel.getWidth() / 5, mainPanel.getHeight() - 30 - 6 * vusotaElementa);
        mainPanel.add(jlabel_shisloShirina);

        jlabel_podpisChustvitelnost = new JLabel("Перепад на 0.1мм по Y", JLabel.CENTER);
        jlabel_podpisChustvitelnost.setSize(mainPanel.getWidth() / 5, vusotaElementa);
        jlabel_podpisChustvitelnost.setLocation(0, mainPanel.getHeight() - 30 - 5 * vusotaElementa);
        mainPanel.add(jlabel_podpisChustvitelnost);

        jlabel_shisloChustvitelnost = new JLabel((double) jscrollBar_chustvitelnostVumiruvanna.getValue() / 10 + " мм по Z", JLabel.CENTER);
        jlabel_shisloChustvitelnost.setSize(mainPanel.getWidth() / 5, vusotaElementa);
        jlabel_shisloChustvitelnost.setLocation(4 * mainPanel.getWidth() / 5, mainPanel.getHeight() - 30 - 5 * vusotaElementa);
        mainPanel.add(jlabel_shisloChustvitelnost);

        //Кнопки
        jbutton_poshatuVumiru = new JButton("Почати виміри");
        jbutton_poshatuVumiru.setSize(mainPanel.getWidth() / 2, vusotaElementa);
        jbutton_poshatuVumiru.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vumiruvanna();
                zapusk.obrobkaEgg.draw_obrablayemaiyKartinka.setImage(new BufferedImage(1, 1, Image.SCALE_FAST), true);
            }
        });
        jbutton_poshatuVumiru.setLocation(0, mainPanel.getHeight() - 30 - 4 * vusotaElementa);
        mainPanel.add(jbutton_poshatuVumiru);

        jbutton_rushneUpravlinna = new JToggleButton("Ручне управління");
        jbutton_rushneUpravlinna.setSize(mainPanel.getWidth() / 2, vusotaElementa);
        jbutton_rushneUpravlinna.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zapusk.obrobkaEgg.jbutton_rushneUpravlinna.setSelected(jbutton_rushneUpravlinna.isSelected());
                if (jbutton_rushneUpravlinna.isSelected()) {
                    zapusk.rushneUpravlinna.setVisible(true);
                    zapusk.rushneUpravlinna.potokRushnogoUpravlinna.resume();
                } else {
                    zapusk.rushneUpravlinna.setVisible(false);
                    zapusk.rushneUpravlinna.potokRushnogoUpravlinna.suspend();
                }

            }
        });
        jbutton_rushneUpravlinna.setLocation(mainPanel.getWidth() / 2, mainPanel.getHeight() - 30 - 4 * vusotaElementa);
        mainPanel.add(jbutton_rushneUpravlinna);

        jbutton_pausaVumiru = new JButton("Пауза");
        jbutton_pausaVumiru.setSize(mainPanel.getWidth() / 2, vusotaElementa);
        jbutton_pausaVumiru.addActionListener(new ActionListener() {
            int z_perervane;

            @Override
            public void actionPerformed(ActionEvent e) {
                nowDoing = !nowDoing;
                if (nowDoing) {
                    jbutton_pausaVumiru.setText("Відновити");
                    potokVumiruvanna.suspend();
                    z_perervane = zapusk.dvijenie.pozitsia_z;
                    if (zapusk.dvijenie.pozitsia_x != 0 || zapusk.dvijenie.pozitsia_y != 0 || zapusk.dvijenie.pozitsia_z != 0)
                        zapusk.dvijenie.goTo(zapusk.dvijenie.pozitsia_x, zapusk.dvijenie.pozitsia_y, -zapusk.dvijenie.vusotaPodjomaNashalna,
                                zapusk.dvijenie.scorostPerehoda, false);
                    jbutton_rushneUpravlinna.setEnabled(true);
                } else {
                    jbutton_pausaVumiru.setText("Пауза");
                    zapusk.dvijenie.goTo(zapusk.dvijenie.pozitsia_x, zapusk.dvijenie.pozitsia_y, z_perervane,
                            zapusk.dvijenie.scorostPerehoda, false);
                    if (jbutton_rushneUpravlinna.isSelected()) jbutton_rushneUpravlinna.doClick();
                    jbutton_rushneUpravlinna.setEnabled(false);
                    potokVumiruvanna.resume();
                }
            }
        });
        jbutton_pausaVumiru.setLocation(0, mainPanel.getHeight() - 30 - 2 * vusotaElementa);
        mainPanel.add(jbutton_pausaVumiru);

        jbutton_stopVumiru = new JButton("Стоп");
        jbutton_stopVumiru.setSize(mainPanel.getWidth() / 2, vusotaElementa);
        jbutton_stopVumiru.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                perervatuObrobku(false, false);
            }
        });
        jbutton_stopVumiru.setLocation(mainPanel.getWidth() / 2, mainPanel.getHeight() - 30 - 2 * vusotaElementa);
        mainPanel.add(jbutton_stopVumiru);

        jbutton_perehodDoObrabotki = new JButton("Перейти до обробки");
        jbutton_perehodDoObrabotki.setSize(mainPanel.getWidth() / 2, vusotaElementa);
        jbutton_perehodDoObrabotki.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zapusk.obrobkaEgg.setSizeFrame();
                zapusk.obrobkaEgg.setVisible(true);
                setVisible(false);
                zapusk.obrobkaEgg.jbutton_zavantajitiZobrajenna.setEnabled(true);
                zapusk.obrobkaEgg.jbutton_zberegtuZobrajenna.setEnabled(false);
                zapusk.obrobkaEgg.jscrollbar_tonalnist.setEnabled(false);
                zapusk.obrobkaEgg.jbutton_vurizatuKontyru.setEnabled(false);
                zapusk.obrobkaEgg.jbutton_vugraivatuKontyru.setEnabled(false);
                zapusk.obrobkaEgg.jprogressbar_obrabotka.setEnabled(false);
                zapusk.obrobkaEgg.jbutton_stopObrobka.setEnabled(false);
                zapusk.obrobkaEgg.jbutton_pausaObrobka.setEnabled(false);
                zapusk.obrobkaEgg.draw_obrablayemaiyKartinka.setImage(null, true);

            }
        });
        jbutton_perehodDoObrabotki.setLocation(0, mainPanel.getHeight() - 30 - vusotaElementa);
        mainPanel.add(jbutton_perehodDoObrabotki);

        jbutton_zminutuImg = new JButton("Обробити зображення під даний розріз");
        jbutton_zminutuImg.setSize(mainPanel.getWidth() / 2, vusotaElementa);
        jbutton_zminutuImg.setLocation(mainPanel.getWidth() / 2, mainPanel.getHeight() - 30 - vusotaElementa);
        jbutton_zminutuImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame_ObrobkaImage jFrame_obrobkaImage;
                if (canResiveImage) {
                    jFrame_obrobkaImage = new JFrame_ObrobkaImage(razrez,zapusk.obrobkaEgg);
                } else {
                    jFrame_obrobkaImage = new JFrame_ObrobkaImage(razrez);
                }
                jFrame_obrobkaImage.setLocationRelativeTo(null);
                jFrame_obrobkaImage.setVisible(true);

            }
        });
        mainPanel.add(jbutton_zminutuImg);

        //Процент виконання (прогресбар)
        jprogressbar_vumiruvanna = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        jprogressbar_vumiruvanna.setStringPainted(true);
        jprogressbar_vumiruvanna.setSize(mainPanel.getWidth(), vusotaElementa);
        jprogressbar_vumiruvanna.setLocation(0, mainPanel.getHeight() - 30 - 3 * vusotaElementa);
        mainPanel.add(jprogressbar_vumiruvanna);

        jbutton_pausaVumiru.setEnabled(false);
        jbutton_perehodDoObrabotki.setEnabled(false);
        jbutton_zminutuImg.setEnabled(false);
        jbutton_stopVumiru.setEnabled(false);
        jprogressbar_vumiruvanna.setEnabled(false);
        pomeraniRazmeru = new int[razmerZagotovki][6];

        drawPanel = new Draw_SnatieRazmerov();
        drawPanel.setLayout(null);
        drawPanel.setSize(jscrollbar_shirinaZagotovki.getValue() * koefValue - 2 * polosaOgranishenia * koefValue,
                vusotaOkna - 6 * vusotaElementa - 30);
        drawPanel.setLocation((shirinaOkna - (jscrollbar_shirinaZagotovki.getValue() * koefValue - 2 * polosaOgranishenia * koefValue)) / 2 - 4, 0);
        mainPanel.add(drawPanel);
        drawPanel.repaint();
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            perervatuObrobku(true, false);
        }
    }

    //Проводяться виміри, через недосконалість вимірюючого пристрою спецефічним чином
    private void vumiruvanna() {
        jbutton_perehodDoObrabotki.setEnabled(false);
        saveRazrezToFile(false);
        potokVumiruvanna = new Thread(new Runnable() {
            public void run() {
                i = 0;
                jprogressbar_vumiruvanna.setValue(0);
                drawPanel.massivKoordinat = new int[razmerZagotovki][5];
                jbutton_perehodDoObrabotki.setEnabled(false);
                jbutton_poshatuVumiru.setEnabled(false);
                jbutton_zminutuImg.setEnabled(false);
                if (jbutton_rushneUpravlinna.isSelected()) jbutton_rushneUpravlinna.doClick();
                jbutton_rushneUpravlinna.setEnabled(false);
                jscrollbar_shirinaZagotovki.setEnabled(false);
                jscrollBar_chustvitelnostVumiruvanna.setEnabled(false);
                jprogressbar_vumiruvanna.setEnabled(true);

                zapusk.dvijenie.goTo(zapusk.dvijenie.pozitsia_x, (zapusk.dvijenie.pozitsia_y - zapusk.dvijenie.cmechenieNashalneY
                        - polosaOgranishenia * koefValue + jscrollbar_shirinaZagotovki.getValue() * koefValue),
                        (zapusk.dvijenie.pozitsia_z - zapusk.dvijenie.vusotaPodjomaNashalna), zapusk.dvijenie.scorostPerehoda, false);
                jbutton_pausaVumiru.setEnabled(true);
                jbutton_stopVumiru.setEnabled(true);

                int hag = razmerZagotovki - 1;
                for (int razmer[] : pomeraniRazmeru) {
                    razmer[0] = pomeratSusidnuToshku(false);
                    drawPanel.addPoint(hag, razmer[0], 1);
                    hag--;
                }

                zapusk.dvijenie.goTo((zapusk.dvijenie.pozitsia_x + zapusk.dvijenie.komandNaPovorot / 4), (zapusk.dvijenie.pozitsia_y + 1),
                        -zapusk.dvijenie.vusotaPodjomaNashalna, zapusk.dvijenie.scorostPerehoda, false);
                hag = 0;
                for (int razmer[] : pomeraniRazmeru) {
                    razmer[1] = pomeratSusidnuToshku(true);
                    drawPanel.addPoint(hag, razmer[1], 2);
                    hag++;
                }

                zapusk.dvijenie.goTo((zapusk.dvijenie.pozitsia_x + zapusk.dvijenie.komandNaPovorot / 4), (zapusk.dvijenie.pozitsia_y - 1),
                        -zapusk.dvijenie.vusotaPodjomaNashalna, zapusk.dvijenie.scorostPerehoda, false);
                hag = razmerZagotovki - 1;
                for (int razmer[] : pomeraniRazmeru) {
                    razmer[2] = pomeratSusidnuToshku(false);
                    drawPanel.addPoint(hag, razmer[2], 3);
                    hag--;
                }

                zapusk.dvijenie.goTo((zapusk.dvijenie.pozitsia_x + zapusk.dvijenie.komandNaPovorot / 4), (zapusk.dvijenie.pozitsia_y + 1),
                        -zapusk.dvijenie.vusotaPodjomaNashalna, zapusk.dvijenie.scorostPerehoda, false);
                hag = 0;
                for (int razmer[] : pomeraniRazmeru) {
                    razmer[3] = pomeratSusidnuToshku(true);
                    drawPanel.addPoint(hag, razmer[3], 4);
                    hag++;
                }
                aprocsimation();
                zapusk.dvijenie.goTo(0, 0, -zapusk.dvijenie.vusotaPodjomaNashalna, zapusk.dvijenie.scorostPerehoda, false);
                zapusk.dvijenie.goTo(0, 0, 0, zapusk.dvijenie.scorostPerehoda, false);
                saveRazrezToFile(true);


                zapusk.obrobkaEgg.draw_obrablayemaiyKartinka.drawCursor(-100, -100);

                jbutton_pausaVumiru.setEnabled(false);
                jbutton_perehodDoObrabotki.setEnabled(true);
                jbutton_poshatuVumiru.setEnabled(true);
                jbutton_rushneUpravlinna.setEnabled(true);
                jbutton_stopVumiru.setEnabled(false);
                jscrollbar_shirinaZagotovki.setEnabled(true);
                jscrollBar_chustvitelnostVumiruvanna.setEnabled(true);
                jprogressbar_vumiruvanna.setEnabled(false);
                jbutton_perehodDoObrabotki.setEnabled(true);
                jbutton_zminutuImg.setEnabled(true);
                Thread.currentThread().stop();
            }
        });
        potokVumiruvanna.start();
    }

    //Промірювання точки і здвиг в сторону
    int i = 0;

    private int pomeratSusidnuToshku(boolean smeshenieVnuz) {

        while (!zapusk.dvijenie.nowKontact() && (zapusk.dvijenie.pozitsia_z < 150)) {
            zapusk.dvijenie.goTo(zapusk.dvijenie.pozitsia_x, zapusk.dvijenie.pozitsia_y,
                    (zapusk.dvijenie.pozitsia_z + 1), zapusk.dvijenie.scorostPerehoda, false);
        }
        int result = zapusk.dvijenie.pozitsia_z;
        zapusk.dvijenie.goTo(zapusk.dvijenie.pozitsia_x, zapusk.dvijenie.pozitsia_y,
                (zapusk.dvijenie.pozitsia_z - zapusk.dvijenie.shustvitelnostVumiruvanna), zapusk.dvijenie.scorostPerehoda, false);
        if (smeshenieVnuz)
            zapusk.dvijenie.goTo(zapusk.dvijenie.pozitsia_x, (zapusk.dvijenie.pozitsia_y + 1),
                    zapusk.dvijenie.pozitsia_z, 2 * zapusk.dvijenie.scorostPerehoda, false);
        else
            zapusk.dvijenie.goTo(zapusk.dvijenie.pozitsia_x, (zapusk.dvijenie.pozitsia_y - 1),
                    zapusk.dvijenie.pozitsia_z, 2 * zapusk.dvijenie.scorostPerehoda, false);
        i++;
        procent = i * 100 / (4 * razmerZagotovki);
        jprogressbar_vumiruvanna.setValue(procent);
        return result + zapusk.dvijenie.vusotaPodjomaNashalna;
    }

    //Реакція на закриття вікна або нажимання кнопки "Стоп"
    private void perervatuObrobku(final boolean exit, boolean konethObrabotki) {
        //Вікно переривання/виходу

        if (konethObrabotki) nowDoing = !nowDoing;
        if (!nowDoing) jbutton_pausaVumiru.doClick();
        int pressed;
        if (konethObrabotki) pressed = 0;
        else pressed = JOptionPane.showOptionDialog(null, "Ви дійсно бажаєте перервати обробку?",
                "Вихід/переривання обробки", JOptionPane.OK_CANCEL_OPTION, 0, null, new String[]{"Так", "Ні"}, "Ні");
        if (pressed == 0) {
            jbutton_stopVumiru.setEnabled(false);
            jbutton_pausaVumiru.setEnabled(false);
            (new Thread(new Runnable() {
                @Override
                public void run() {

                    if (zapusk.dvijenie.pozitsia_x != 0 || zapusk.dvijenie.pozitsia_y != 0 || zapusk.dvijenie.pozitsia_z != 0)
                        zapusk.dvijenie.goTo(0, 0, -zapusk.dvijenie.vusotaPodjomaNashalna, zapusk.dvijenie.scorostPerehoda, false);
                    zapusk.dvijenie.goTo(0, 0, 0, zapusk.dvijenie.scorostPerehoda, false);


                    jprogressbar_vumiruvanna.setValue(0);
                    jbutton_pausaVumiru.setText("Пауза");
                    jbutton_perehodDoObrabotki.setEnabled(false);
                    jbutton_poshatuVumiru.setEnabled(true);
                    jbutton_rushneUpravlinna.setEnabled(true);
                    jscrollbar_shirinaZagotovki.setEnabled(true);
                    jscrollBar_chustvitelnostVumiruvanna.setEnabled(true);
                    jprogressbar_vumiruvanna.setEnabled(false);
                    nowDoing = false;
                    zapusk.dvijenie.notSignalXYZ(true, true, true);
                    if (potokVumiruvanna != null) potokVumiruvanna.stop();
                    if (!exit) Thread.currentThread().stop();
                    else System.exit(1);
                }
            })).start();
        }
    }

    //Обчислений збалансований радіус
    private void aprocsimation() {
        int[][] aprocsimationResult = new int[pomeraniRazmeru.length][4];
        razrez = new int[pomeraniRazmeru.length];
        for (int j = 0; j < 4; j++) {
            aprocsimationResult[0][j] = pomeraniRazmeru[0][j];
            aprocsimationResult[pomeraniRazmeru.length - 1][j] = pomeraniRazmeru[pomeraniRazmeru.length - 1][j];
            aprocsimationResult[1][j] = (pomeraniRazmeru[0][j] + pomeraniRazmeru[1][j] + pomeraniRazmeru[2][j]) / 3;
            aprocsimationResult[pomeraniRazmeru.length - 2][j] = (pomeraniRazmeru[pomeraniRazmeru.length - 1][j] +
                    pomeraniRazmeru[pomeraniRazmeru.length - 2][j] + pomeraniRazmeru[pomeraniRazmeru.length - 3][j]) / 3;
            for (int i = 2; i < (pomeraniRazmeru.length - 2); i++) {
                aprocsimationResult[i][j] = (pomeraniRazmeru[i - 2][j] + pomeraniRazmeru[i - 1][j] + pomeraniRazmeru[i][j] +
                        pomeraniRazmeru[i + 1][j] + pomeraniRazmeru[i + 2][j]) / 5;
            }
        }
        for (int i = 0; i < pomeraniRazmeru.length; i++) {
            drawPanel.massivKoordinat[pomeraniRazmeru.length - 1 - i][0] = aprocsimationResult[i][0];
            drawPanel.massivKoordinat[i][1] = aprocsimationResult[i][1];
            drawPanel.massivKoordinat[pomeraniRazmeru.length - 1 - i][2] = aprocsimationResult[i][2];
            drawPanel.massivKoordinat[i][3] = aprocsimationResult[i][3];
            drawPanel.massivKoordinat[i][4] = (aprocsimationResult[pomeraniRazmeru.length - 1 - i][0] +
                    aprocsimationResult[i][1] +
                    aprocsimationResult[pomeraniRazmeru.length - 1 - i][2] + aprocsimationResult[i][3]) / 4;
            pomeraniRazmeru[i][4] = drawPanel.massivKoordinat[i][4];
            razrez[i] = pomeraniRazmeru[i][4];
        }
        drawPanel.repaint();
    }

    synchronized private void saveRazrezToFile(boolean ProcessFinished) {
        try {
        	RandomAccessFile rf = FileUtils.getRandomAccersFile("tmpSave/Razrez"+Param_Profile.getFileAppender()+".res");
            if (ProcessFinished) {
                rf.writeInt(jscrollbar_shirinaZagotovki.getValue());
            } else {
                rf.writeInt(0);
                rf.close();
                return;
            }
            for (int j = 0; j < 5; j++)
                for (int i = 0; i < drawPanel.massivKoordinat.length; i++) {
                    rf.writeInt(drawPanel.massivKoordinat[i][j]);
                }

            rf =  FileUtils.getRandomAccersFile("tmpSave/ObrabkaSettings"+Param_Profile.getFileAppender()+".res");
            rf.writeInt(0);
            rf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized private void loadRazrezFromFile() {
        try {
        	RandomAccessFile rf = FileUtils.getRandomAccersFile("tmpSave/Razrez"+Param_Profile.getFileAppender()+".res");
            rf.seek(0);
            if (rf.readInt() == 0) return;
            else {
                rf.seek(0);
                jscrollbar_shirinaZagotovki.setValue(rf.readInt());
            }
            razmerZagotovki = koefValue * jscrollbar_shirinaZagotovki.getValue() - 2 * polosaOgranishenia * koefValue + 1;
            razrez = new int[razmerZagotovki];
            drawPanel.massivKoordinat = new int[razmerZagotovki][5];
            for (int j = 0; j < 5; j++)
                for (int i = 0; i < razmerZagotovki; i++) {
                    drawPanel.massivKoordinat[i][j] = rf.readInt();
                    pomeraniRazmeru[i][j] = drawPanel.massivKoordinat[i][j];
                    if (j == 4) razrez[i] = pomeraniRazmeru[i][4];
                }

            rf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jbutton_perehodDoObrabotki.setEnabled(true);
        jbutton_zminutuImg.setEnabled(true);
    }

}
