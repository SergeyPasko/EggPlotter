import Config.Param_Dvijenie;
import Config.Param_ObrabotkaEgg;
import Config.Param_Profile;
import drawPanels.Draw_ObrablayemaiyKartinka;
import drawPanels.Draw_VusotaInstrumenta;
import utils.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static java.awt.Scrollbar.HORIZONTAL;

/**
 * Created with IntelliJ IDEA.
 * User: spasko
 * Date: 11.09.14
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */
class JFrame_ObrobkaEgg extends JFrame {

    //Параметри та елементи вікна обробки
    Zapusk zapusk;
    JPanel mainPanel;
    JPanel jpanelKnopki;
    int vusotaOkna = Param_ObrabotkaEgg.getInstance().getProperty(Param_ObrabotkaEgg.VUSOTA_OKNA);
    int shirinaOkna = Param_Dvijenie.getInstance().getProperty(Param_Dvijenie.HAGOVNAPOVOROT)/2;
    int vusotaElementa = Param_ObrabotkaEgg.getInstance().getProperty(Param_ObrabotkaEgg.VUSOTA_ELEMENTA);

    JLabel jlabel_podpisScorostObrabotki;
    JLabel jlabel_shisloScorostObrabotki;
    JLabel jlabel_podpisTonalnist;
    JLabel jlabel_shisloTonalnist;

    JButton jbutton_zavantajitiZobrajenna;
    JButton jbutton_zberegtuZobrajenna;
    JButton jbutton_pereituDoVumiruvanna;
    JButton jbutton_vurizatuKontyru;
    JButton jbutton_vugraivatuKontyru;
    JButton jbutton_vugraivatuMonochromneZobrajenna;
    JButton jbutton_prodovjituPoperednu;
    // JButton jbutton_vugraivatuTonalneZobrajenna;
    JButton jbutton_zminutuImg;
    JButton jbutton_pausaObrobka;
    JButton jbutton_stopObrobka;

    JToggleButton jbutton_rushneUpravlinna;
    JCheckBox jCheckBox_autoExit;

    // JRadioButton jradiobutton_originalneZobrajenna;
    // JRadioButton jradiobutton_monochromneZobrajenna;
    // JRadioButton jradiobutton_tonalneZobrajenna;
    // ButtonGroup buttongroup;

    JScrollBar jscrollbar_scorostObrabotki;
    JScrollBar jscrollbar_tonalnist;
    JProgressBar jprogressbar_obrabotka;


    Image imgOriginalneZobrajenna;
    Draw_ObrablayemaiyKartinka draw_obrablayemaiyKartinka;
    Draw_VusotaInstrumenta draw_vusotaInstrumenta;
    int[] massivToshekDlaKartinki;
    int kolishesvo_chernuh_toshek;
    int kolishesvo_krasnuh_toshek;
    int kolishesvo_narisovannuh_toshek;
    int procent_vupolnenia;
    int predelScorosti = Param_ObrabotkaEgg.getInstance().getProperty(Param_ObrabotkaEgg.PREDELSCOROSTI);
    int scorostObrabotki;
    int shirina;
    int vusota;
    Thread potokObrabotki;
    int glubinaProrezania;
    boolean nowDoing = false;
    boolean tolkoKonturu = false;
    boolean draw_height = false;
    private int selectedItem;

    public JFrame_ObrobkaEgg(Zapusk zapusk) {
        this.zapusk = zapusk;
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
            loadObrobkaFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() {

        //Основне вікно
        mainPanel = (JPanel) this.getContentPane();
        mainPanel.setLayout(null);
        this.setSize(new Dimension(shirinaOkna+40, vusotaOkna));
        mainPanel.setSize(new Dimension(shirinaOkna+40, vusotaOkna));
        this.setTitle("Сферичний плоттер V2, Обробка поверхні");
        this.setResizable(false);


        draw_obrablayemaiyKartinka = new Draw_ObrablayemaiyKartinka();
        draw_obrablayemaiyKartinka.setLayout(null);
        draw_obrablayemaiyKartinka.setSize(shirinaOkna - 20, vusotaOkna - vusotaElementa * 9 - 30);
        draw_obrablayemaiyKartinka.setLocation(0, 0);
        mainPanel.add(draw_obrablayemaiyKartinka);
        draw_obrablayemaiyKartinka.repaint();

        draw_vusotaInstrumenta = new Draw_VusotaInstrumenta();
        draw_vusotaInstrumenta.setLayout(null);
        draw_vusotaInstrumenta.setSize(20, vusotaOkna - vusotaElementa * 9 - 30);
        draw_vusotaInstrumenta.setLocation(shirinaOkna, 0);
        mainPanel.add(draw_vusotaInstrumenta);
        draw_vusotaInstrumenta.repaint();

        jpanelKnopki = new JPanel(null);
        jpanelKnopki.setSize(new Dimension(shirinaOkna+20, vusotaElementa * 9));
        jpanelKnopki.setLocation(0, draw_obrablayemaiyKartinka.getHeight());
        mainPanel.add(jpanelKnopki);

        //Скроли
        jscrollbar_scorostObrabotki = new JScrollBar(HORIZONTAL, 150, 1, 30, 10 * predelScorosti + 1);
        jscrollbar_scorostObrabotki.setSize(3 * jpanelKnopki.getWidth() / 5, vusotaElementa);
        jscrollbar_scorostObrabotki.setLocation(jpanelKnopki.getWidth() / 5, jpanelKnopki.getHeight() - 30 - 6 * vusotaElementa);
        jscrollbar_scorostObrabotki.addAdjustmentListener(new AdjustmentListener() {
            public final void adjustmentValueChanged(AdjustmentEvent e) {
                jlabel_shisloScorostObrabotki.setText(String.format("%1$.3f мм/сек",
                        (double) jscrollbar_scorostObrabotki.getValue() / 300));
               if (Param_Profile.isSecondGeneration()){
                   scorostObrabotki = 650000 / jscrollbar_scorostObrabotki.getValue();
               } else{
                   scorostObrabotki = 300000 / jscrollbar_scorostObrabotki.getValue();  
               }
            }
        });
        jpanelKnopki.add(jscrollbar_scorostObrabotki);
        if (Param_Profile.isSecondGeneration()){
            scorostObrabotki = 650000 / jscrollbar_scorostObrabotki.getValue();
        } else{
            scorostObrabotki = 300000 / jscrollbar_scorostObrabotki.getValue();  
        }

        jscrollbar_tonalnist = new JScrollBar(HORIZONTAL, 50, 1, 20, 81);
        jscrollbar_tonalnist.setSize(3 * jpanelKnopki.getWidth() / 5, vusotaElementa);
        jscrollbar_tonalnist.setLocation(jpanelKnopki.getWidth() / 5, jpanelKnopki.getHeight() - 30 - 5 * vusotaElementa);
        jscrollbar_tonalnist.addAdjustmentListener(new AdjustmentListener() {
            public final void adjustmentValueChanged(AdjustmentEvent e) {
                jlabel_shisloTonalnist.setText(jscrollbar_tonalnist.getValue() + " %");
                displayKartinka();
            }
        });
        jpanelKnopki.add(jscrollbar_tonalnist);

        //Підписи
        jlabel_podpisScorostObrabotki = new JLabel("Швидкість обробки", JLabel.RIGHT);
        jlabel_podpisScorostObrabotki.setSize(jpanelKnopki.getWidth() / 5, vusotaElementa);
        jlabel_podpisScorostObrabotki.setLocation(0, jpanelKnopki.getHeight() - 30 - 6 * vusotaElementa);
        jpanelKnopki.add(jlabel_podpisScorostObrabotki);

        jlabel_shisloScorostObrabotki = new JLabel(String.format("%1$.3f мм/сек",
                (double) jscrollbar_scorostObrabotki.getValue() / 300), JLabel.LEFT);
        jlabel_shisloScorostObrabotki.setSize(jpanelKnopki.getWidth() / 5, vusotaElementa);
        jlabel_shisloScorostObrabotki.setLocation(4 * jpanelKnopki.getWidth() / 5, jpanelKnopki.getHeight() - 30 - 6 * vusotaElementa);
        jpanelKnopki.add(jlabel_shisloScorostObrabotki);

        jlabel_podpisTonalnist = new JLabel("Глибина відтінків", JLabel.RIGHT);
        jlabel_podpisTonalnist.setSize(jpanelKnopki.getWidth() / 5, vusotaElementa);
        jlabel_podpisTonalnist.setLocation(0, jpanelKnopki.getHeight() - 30 - 5 * vusotaElementa);
        jpanelKnopki.add(jlabel_podpisTonalnist);

        jlabel_shisloTonalnist = new JLabel(jscrollbar_tonalnist.getValue() + " %", JLabel.LEFT);
        jlabel_shisloTonalnist.setSize(jpanelKnopki.getWidth() / 5, vusotaElementa);
        jlabel_shisloTonalnist.setLocation(4 * jpanelKnopki.getWidth() / 5, jpanelKnopki.getHeight() - 30 - 5 * vusotaElementa);
        jpanelKnopki.add(jlabel_shisloTonalnist);

        //Кнопки
        jbutton_zavantajitiZobrajenna = newAddButton("Завантажити зображення", 1, 1);
        jbutton_zavantajitiZobrajenna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImage(null);
            }
        });
        jbutton_prodovjituPoperednu = newAddButton("Продовжити попередню обробку", 1, 2);
        jbutton_prodovjituPoperednu.setEnabled(false);
        jbutton_prodovjituPoperednu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (selectedItem) {
                    case 1:
                        jbutton_vurizatuKontyru.setEnabled(true);
                        jbutton_vurizatuKontyru.doClick();
                        jbutton_vurizatuKontyru.setEnabled(false);
                        break;
                    case 2:
                        jbutton_vugraivatuMonochromneZobrajenna.setEnabled(true);
                        jbutton_vugraivatuMonochromneZobrajenna.doClick();
                        jbutton_vugraivatuMonochromneZobrajenna.setEnabled(false);
                        break;
                    case 3:
                        jbutton_vugraivatuKontyru.setEnabled(true);
                        jbutton_vugraivatuKontyru.doClick();
                        jbutton_vugraivatuKontyru.setEnabled(false);
                        break;
                }
                jbutton_prodovjituPoperednu.setEnabled(false);
            }
        });


        jbutton_zberegtuZobrajenna = newAddButton("Зберегти зображення", 2, 1);
        jbutton_zberegtuZobrajenna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImage();
            }
        });

        jbutton_pereituDoVumiruvanna = newAddButton("Вимірювання", 1, 5);
        jbutton_pereituDoVumiruvanna.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zapusk.obrobkaEgg.setVisible(false);
                zapusk.snatieRazmerov.setVisible(true);
                zapusk.snatieRazmerov.jbutton_pausaVumiru.setEnabled(false);
                zapusk.snatieRazmerov.jbutton_perehodDoObrabotki.setEnabled(true);
                zapusk.snatieRazmerov.jbutton_poshatuVumiru.setEnabled(true);
                zapusk.snatieRazmerov.jbutton_rushneUpravlinna.setEnabled(true);
                zapusk.snatieRazmerov.jbutton_stopVumiru.setEnabled(false);
                zapusk.snatieRazmerov.jscrollbar_shirinaZagotovki.setEnabled(true);
                zapusk.snatieRazmerov.jprogressbar_vumiruvanna.setEnabled(false);
            }
        });

        jbutton_rushneUpravlinna = new JToggleButton("Ручне управління");
        jbutton_rushneUpravlinna.setSize(jpanelKnopki.getWidth() / 2, vusotaElementa);
        jbutton_rushneUpravlinna.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zapusk.snatieRazmerov.jbutton_rushneUpravlinna.doClick();
            }
        });
        jbutton_rushneUpravlinna.setLocation(jpanelKnopki.getWidth() / 2, jpanelKnopki.getHeight() - 5 * vusotaElementa);
        jpanelKnopki.add(jbutton_rushneUpravlinna);

        jbutton_vurizatuKontyru = newAddButton("Вирізати контури", 1, 6);
        jbutton_vurizatuKontyru.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jprogressbar_obrabotka.setToolTipText("Триває вирізання контурів зображення");
                draw_obrablayemaiyKartinka.setToolTipText("Триває вирізання контурів зображення");
                //jradiobutton_monochromneZobrajenna.doClick();
                selectedItem = 1;
                blokirovkaKlavishPriObraabotki();
                obrabkaPoKrivum(true, true);
            }
        });
        jbutton_vugraivatuMonochromneZobrajenna = newAddButton("Намалювати монохромне забраження", 2, 6);
        jbutton_vugraivatuMonochromneZobrajenna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jprogressbar_obrabotka.setToolTipText("Триває малювання монохромного зображення");
                draw_obrablayemaiyKartinka.setToolTipText("Триває малювання монохромного зображення");
                // jradiobutton_monochromneZobrajenna.doClick();
                selectedItem = 2;
                blokirovkaKlavishPriObraabotki();
                obrabkaPoKrivum(false, false);
            }
        });
        jbutton_vugraivatuKontyru = newAddButton("Намалювати контури", 1, 7);
        jbutton_vugraivatuKontyru.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jprogressbar_obrabotka.setToolTipText("Триває малювання контурів зображення");
                draw_obrablayemaiyKartinka.setToolTipText("Триває малювання контурів зображення");
                // jradiobutton_monochromneZobrajenna.doClick();
                selectedItem = 3;
                blokirovkaKlavishPriObraabotki();
                obrabkaPoKrivum(true, false);
            }
        });
        /*jbutton_vugraivatuTonalneZobrajenna = newAddButton("Вигравіювати тональне зображення", 2, 7);
        jbutton_vugraivatuTonalneZobrajenna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jprogressbar_obrabotka.setToolTipText("Триває гравіювання тонального зображення");
                draw_obrablayemaiyKartinka.setToolTipText("Триває гравіювання тонального зображення");
                jradiobutton_tonalneZobrajenna.doClick();
                blokirovkaKlavishPriObraabotki();
                obrabkaTonalna();
            }
        });    */
        jbutton_zminutuImg = newAddButton("Обробити зображення під даний розріз", 2, 7);
        jbutton_zminutuImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zapusk.snatieRazmerov.setCanResiveImage(true);
                zapusk.snatieRazmerov.jbutton_zminutuImg.doClick();
                zapusk.snatieRazmerov.setCanResiveImage(false);
            }
        });

        jbutton_pausaObrobka = newAddButton("Пауза", 1, 9);
        jbutton_pausaObrobka.addActionListener(new ActionListener() {
            int z_perervane;

            @Override
            public void actionPerformed(ActionEvent e) {
                nowDoing = !nowDoing;
                if (nowDoing) {
                    jbutton_pausaObrobka.setText("Відновити");
                    potokObrabotki.suspend();
                    draw_height = false;
                    z_perervane = zapusk.dvijenie.pozitsia_z;
                    if (zapusk.dvijenie.pozitsia_x != 0 || zapusk.dvijenie.pozitsia_y != 0 || zapusk.dvijenie.pozitsia_z != 0)
                        zapusk.dvijenie.goTo(zapusk.dvijenie.pozitsia_x, zapusk.dvijenie.pozitsia_y, 0,
                                zapusk.dvijenie.scorostPerehoda, false);
                    jbutton_rushneUpravlinna.setEnabled(true);

                } else {
                    jbutton_pausaObrobka.setText("Пауза");
                    zapusk.dvijenie.goTo(zapusk.dvijenie.pozitsia_x, zapusk.dvijenie.pozitsia_y, z_perervane,
                            zapusk.dvijenie.scorostPerehoda, false);
                    if (jbutton_rushneUpravlinna.isSelected()) jbutton_rushneUpravlinna.doClick();
                    jbutton_rushneUpravlinna.setEnabled(false);
                    draw_height = true;
                    potokObrabotki.resume();

                }
            }
        });
        jbutton_stopObrobka = newAddButton("Стоп", 2, 9);
        jbutton_stopObrobka.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                perervatuObrobku(false, false);
            }
        });

        //Процент виконання (прогресбар)
        jprogressbar_obrabotka = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        jprogressbar_obrabotka.setStringPainted(true);
        jprogressbar_obrabotka.setSize(jpanelKnopki.getWidth(), vusotaElementa);
        jprogressbar_obrabotka.setLocation(0, jpanelKnopki.getHeight() - 2 * vusotaElementa);
        jpanelKnopki.add(jprogressbar_obrabotka);

        //Галочка про автовиключення
        jCheckBox_autoExit = new JCheckBox("Автоматичне вимикання компютера при завершенні обробки");
        jCheckBox_autoExit.setSize(jpanelKnopki.getWidth() / 2, vusotaElementa);
        jCheckBox_autoExit.setLocation(jpanelKnopki.getWidth() / 2, jpanelKnopki.getHeight() - 8 * vusotaElementa);
        jpanelKnopki.add(jCheckBox_autoExit);
        //Перемикачі
      /*  jradiobutton_originalneZobrajenna = new JRadioButton("Оригінал зображення");
        jradiobutton_originalneZobrajenna.setSize(jpanelKnopki.getWidth() / 3, vusotaElementa);
        jradiobutton_originalneZobrajenna.setLocation(0, jpanelKnopki.getHeight() - 8 * vusotaElementa);
        jpanelKnopki.add(jradiobutton_originalneZobrajenna);

        jradiobutton_monochromneZobrajenna = new JRadioButton("Монохромне зображення");
        jradiobutton_monochromneZobrajenna.setSize(jpanelKnopki.getWidth() / 3, vusotaElementa);
        jradiobutton_monochromneZobrajenna.setLocation(jpanelKnopki.getWidth() / 3, jpanelKnopki.getHeight() - 8 * vusotaElementa);
        jpanelKnopki.add(jradiobutton_monochromneZobrajenna);

        jradiobutton_tonalneZobrajenna = new JRadioButton("Тональне зображення");
        jradiobutton_tonalneZobrajenna.setSize(jpanelKnopki.getWidth() / 3, vusotaElementa);
        jradiobutton_tonalneZobrajenna.setLocation(2 * jpanelKnopki.getWidth() / 3, jpanelKnopki.getHeight() - 8 * vusotaElementa);
        jpanelKnopki.add(jradiobutton_tonalneZobrajenna);

        buttongroup = new ButtonGroup();
        buttongroup.add(jradiobutton_originalneZobrajenna);
        buttongroup.add(jradiobutton_monochromneZobrajenna);
        buttongroup.add(jradiobutton_tonalneZobrajenna);
        jradiobutton_monochromneZobrajenna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayKartinka();
            }
        });
        jradiobutton_originalneZobrajenna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayKartinka();
            }
        });
        jradiobutton_tonalneZobrajenna.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayKartinka();
            }
        });
        jradiobutton_originalneZobrajenna.setSelected(true);  */
        setSizeFrame();
    }

    public void setSizeFrame() {
        vusotaOkna = zapusk.snatieRazmerov.razmerZagotovki / 2 +
                jpanelKnopki.getHeight() + 30;
        this.setSize(new Dimension(shirinaOkna+20, vusotaOkna));
        this.setMinimumSize(new Dimension(shirinaOkna+20, vusotaOkna));
        mainPanel.setSize(new Dimension(shirinaOkna+20, vusotaOkna));
        draw_obrablayemaiyKartinka.setSize(shirinaOkna, zapusk.snatieRazmerov.razmerZagotovki / 10 * 5);
        draw_vusotaInstrumenta.setSize(20, zapusk.snatieRazmerov.razmerZagotovki / 10 * 5);
        draw_vusotaInstrumenta.setHeight(zapusk.snatieRazmerov.razmerZagotovki / 10 * 5);
        jpanelKnopki.setLocation(0, draw_obrablayemaiyKartinka.getHeight());
        this.setLocationRelativeTo(null);
    }

    private void blokirovkaKlavishPriObraabotki() {
        jbutton_zavantajitiZobrajenna.setEnabled(false);
        jbutton_zberegtuZobrajenna.setEnabled(false);
        jbutton_zminutuImg.setEnabled(false);
        //jradiobutton_originalneZobrajenna.setEnabled(false);
        //jradiobutton_monochromneZobrajenna.setEnabled(false);
        //jradiobutton_tonalneZobrajenna.setEnabled(false);
        jscrollbar_tonalnist.setEnabled(false);
        if (jbutton_rushneUpravlinna.isSelected()) zapusk.snatieRazmerov.jbutton_rushneUpravlinna.doClick();
        jbutton_rushneUpravlinna.setEnabled(false);
        jbutton_pereituDoVumiruvanna.setEnabled(false);
        jbutton_vurizatuKontyru.setEnabled(false);
        //jbutton_vugraivatuTonalneZobrajenna.setEnabled(false);
        jbutton_vugraivatuMonochromneZobrajenna.setEnabled(false);
        jbutton_vugraivatuKontyru.setEnabled(false);
        jprogressbar_obrabotka.setEnabled(true);
    }

    //Метод для додавання кнопки
    private JButton newAddButton(String title, int stolbech, int stroka) {
        JButton jButton = new JButton(title);
        jButton.setSize(jpanelKnopki.getWidth() / 2, vusotaElementa);
        jButton.setLocation(jpanelKnopki.getWidth() / 2 * (stolbech - 1), jpanelKnopki.getHeight() - (10 - stroka) * vusotaElementa);
        jpanelKnopki.add(jButton);
        return jButton;
    }

    //Реакція програми на закритття вікна
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            perervatuObrobku(true, false);
        }
    }

    //Завантажити малюнок
    public void loadImage(Image resivedImage) {
        FileDialog fdlg = null;
        if (resivedImage == null) {
            fdlg = new FileDialog(this, "Відкрити зображення", FileDialog.LOAD);
            fdlg.setLocationRelativeTo(null);
            fdlg.setDirectory("Kartinki");
            fdlg.setFile("*.bmp;*.jpg");
            fdlg.setVisible(true);
            String szCurrentFilename = (fdlg.getDirectory() + fdlg.getFile());
            try {
                imgOriginalneZobrajenna = ImageIO.read(new File(szCurrentFilename));

                {
                    BufferedImage scaledBI = new BufferedImage(draw_obrablayemaiyKartinka.getWidth(),
                            draw_obrablayemaiyKartinka.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = scaledBI.createGraphics();
                    g.drawImage(imgOriginalneZobrajenna, 0, 0, draw_obrablayemaiyKartinka.getWidth(),
                            draw_obrablayemaiyKartinka.getHeight(), null);
                    g.dispose();
                    imgOriginalneZobrajenna = scaledBI;
                }
            } catch (IOException ignored) {
            }
        } else {
            imgOriginalneZobrajenna = resivedImage;
        }
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(imgOriginalneZobrajenna, 0);
        try {
            mt.waitForAll();
        } catch (InterruptedException ignored) {
        }
        if (resivedImage != null || fdlg.getFile() != null) {

            displayKartinka();
            jbutton_zberegtuZobrajenna.setEnabled(true);
            //jradiobutton_originalneZobrajenna.setEnabled(true);
            //jradiobutton_monochromneZobrajenna.setEnabled(true);
            //jradiobutton_tonalneZobrajenna.setEnabled(true);
            jscrollbar_tonalnist.setEnabled(true);
            jbutton_vurizatuKontyru.setEnabled(true);
            //jbutton_vugraivatuTonalneZobrajenna.setEnabled(true);
            jbutton_vugraivatuMonochromneZobrajenna.setEnabled(true);
            jbutton_vugraivatuKontyru.setEnabled(true);
            jbutton_prodovjituPoperednu.setEnabled(false);
        }
    }

    //Створення та візуалізація малюнку для обробки
    private void displayKartinka() {
        kolishesvo_chernuh_toshek = 0;
        kolishesvo_krasnuh_toshek = 0;
        kolishesvo_narisovannuh_toshek = 0;
        Image imgDlaObrobki = null;
        shirina = draw_obrablayemaiyKartinka.getWidth();
        vusota = draw_obrablayemaiyKartinka.getHeight();
        {
            massivToshekDlaKartinki = new int[vusota * shirina];
            try {
                if (imgOriginalneZobrajenna != null) {
                    PixelGrabber pg = new PixelGrabber(imgOriginalneZobrajenna, 0, 0, shirina, vusota, massivToshekDlaKartinki, 0, shirina);
                    pg.grabPixels();
                }
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
                        kolishesvo_chernuh_toshek++;
                    } else r = g = b = 255;
                    massivToshekDlaKartinki[k * shirina + j] = (0Xff000000 | r << 16 | g << 8 | b);
                }
            }
            for (int j = 0; j < shirina; j++) {
                for (int k = 0; k < vusota; k++) {
                    int p = massivToshekDlaKartinki[k * shirina + j];
                    int r;
                    int g = 0xff & (p >> 8);
                    int b = 0xff & (p);
                    if (b == 0) if (sravnenieSveta(massivToshekDlaKartinki, j, k, 0, 1, 0, 210) &
                            sravnenieSveta(massivToshekDlaKartinki, j, k, 1, 0, 0, 210) &
                            sravnenieSveta(massivToshekDlaKartinki, j, k, 0, -1, 0, 210) &
                            sravnenieSveta(massivToshekDlaKartinki, j, k, -1, 0, 0, 210)) {
                        r = 210;
                        massivToshekDlaKartinki[k * shirina + j] = (0Xff000000 | r << 16 | g << 8 | b);
                        kolishesvo_krasnuh_toshek++;
                    }
                }
            }
            kolishesvo_chernuh_toshek -= kolishesvo_krasnuh_toshek;
            imgDlaObrobki = createImage(new MemoryImageSource(shirina, vusota, massivToshekDlaKartinki, 0, shirina));
        }

        draw_obrablayemaiyKartinka.setImage(imgDlaObrobki, true);
    }

    //Перевіряється колір точки з масива pix зміщеної на (delta_x,delta_y) відносно даної (xx,yy) з кольором[] sravnivaemoe_znashenie
    private boolean sravnenieSveta(int[] pix, int xx, int yy, int delta_x, int delta_y, int... sravnivaemoe_znashenie) {
        if (xx + delta_x < 0 || xx + delta_x > shirina - 1 || yy + delta_y > vusota - 1 || yy + delta_y < 0)
            return false;
        else {
            int summaColor = (0xff & (pix[(yy + delta_y) * shirina + xx + delta_x])) + (0xff & (pix[(yy + delta_y) * shirina + xx + delta_x] >> 16)) +
                    (0xff & pix[(yy + delta_y) * shirina + xx + delta_x] >> 8);

            for (int a : sravnivaemoe_znashenie) {
                if (summaColor == a) return true;
            }
            return false;
        }
    }

    //Збереження малюнку
    void saveImage() {
        FileDialog fdlg;
        fdlg = new FileDialog(this, "Збереження зображення", FileDialog.SAVE);
        fdlg.setLocationRelativeTo(null);
        fdlg.setDirectory("Кartinki-bmp save");
        fdlg.setFile("*(ширина-" + imgOriginalneZobrajenna.getHeight(null) + " тональність-" + jscrollbar_tonalnist.getValue() + ").bmp");
        fdlg.setVisible(true);
        if (fdlg.getFile() == null) return;
        try {
            BufferedImage bi = imageToBufferedImage(imgOriginalneZobrajenna);
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

    //Обробка з аналізом по кривим
    private void obrabkaPoKrivum(final boolean tolkoKonturu, boolean prorizat) {
        saveObrobkaToFile(false);
        if (prorizat) glubinaProrezania = zapusk.dvijenie.glubinaProrezania;
        else glubinaProrezania = 0;
        this.tolkoKonturu = tolkoKonturu;
        potokObrabotki = new Thread(new Runnable() {
            public void run() {

                zapusk.dvijenie.pozitsia_x = 0;
                zapusk.dvijenie.pozitsia_y = zapusk.dvijenie.cmechenieNashalneY - zapusk.snatieRazmerov.polosaOgranishenia * 10;
                zapusk.dvijenie.pozitsia_z = zapusk.dvijenie.vusotaPodjomaNashalna;
                zapusk.dvijenie.goTo(0, 0, 0, zapusk.dvijenie.scorostPerehoda, false);
                zapusk.dvijenie.goTo(0, 0, 0, zapusk.dvijenie.scorostPerehoda, false);
                //Nad poverhnostu v nashale
                zapusk.dvijenie.goTo(0, 0, zapusk.snatieRazmerov.pomeraniRazmeru[0][4] - zapusk.dvijenie.vusotaProhodaNadPoverhnostu, zapusk.dvijenie.scorostPerehoda, false);


                draw_height = true;
                jbutton_stopObrobka.setEnabled(true);
                jbutton_pausaObrobka.setEnabled(true);
                for (int k = 0; k < shirina; k++) {
                    if (k % 2 == 0)
                        for (int j = 0; j < vusota; j++) {
                            prorisovkaKruvoi(k, j, 0);
                            if (!tolkoKonturu) prorisovkaKruvoi(k, j, 210);
                        }
                    else {
                        for (int j = vusota - 1; j > -1; j--) {
                            prorisovkaKruvoi(k, j, 0);
                            if (!tolkoKonturu) prorisovkaKruvoi(k, j, 210);
                        }
                    }
                }
                perervatuObrobku(false, true);
            }
        });
        potokObrabotki.start();

    }

    //Малювання кривої без відривів
    private void prorisovkaKruvoi(int k, int j, int sravnivaemoe_znashenie) {
        boolean contact = false;
        Color color = Color.BLUE;
        if (sravnenieSveta(massivToshekDlaKartinki, k, j, 0, 0, sravnivaemoe_znashenie)) {

            if (sravnenieSveta(massivToshekDlaKartinki, k, j, 0, 1, sravnivaemoe_znashenie) ||
                    sravnenieSveta(massivToshekDlaKartinki, k, j, 0, -1, sravnivaemoe_znashenie) ||
                    sravnenieSveta(massivToshekDlaKartinki, k, j, 1, 0, sravnivaemoe_znashenie) ||
                    sravnenieSveta(massivToshekDlaKartinki, k, j, -1, 0, sravnivaemoe_znashenie) ||
                    sravnenieSveta(massivToshekDlaKartinki, k, j, 1, 1, sravnivaemoe_znashenie) ||
                    sravnenieSveta(massivToshekDlaKartinki, k, j, 1, -1, sravnivaemoe_znashenie) ||
                    sravnenieSveta(massivToshekDlaKartinki, k, j, -1, 1, sravnivaemoe_znashenie) ||
                    sravnenieSveta(massivToshekDlaKartinki, k, j, -1, -1, sravnivaemoe_znashenie)) {
                contact = true;
                zapusk.dvijenie.goTo(2 * k, 2 * j, zapusk.dvijenie.pozitsia_z, zapusk.dvijenie.scorostPerehoda, true);
                zapusk.dvijenie.goTo(2 * k, 2 * j, zapusk.snatieRazmerov.pomeraniRazmeru[2 * j][4] - zapusk.dvijenie.vusotaProhodaNadPoverhnostu, zapusk.dvijenie.scorostPerehoda, true);
                zapusk.dvijenie.goTo(2 * k, 2 * j, glubinaProrezania + zapusk.snatieRazmerov.pomeraniRazmeru[2 * j][4],
                        scorostObrabotki, true);
                //System.out.println("Start");
            }

            massivToshekDlaKartinki[j * shirina + k] = 0Xff000000 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
            kolishesvo_narisovannuh_toshek += 1;
            if (tolkoKonturu) procent_vupolnenia = (kolishesvo_narisovannuh_toshek * 100) / kolishesvo_chernuh_toshek;
            else
                procent_vupolnenia = (kolishesvo_narisovannuh_toshek * 100) / (kolishesvo_chernuh_toshek + kolishesvo_krasnuh_toshek);
            jprogressbar_obrabotka.setValue(procent_vupolnenia);

            while (contact) {
                if (dvijenie(0, 1, sravnivaemoe_znashenie)) continue;
                if (dvijenie(1, 1, sravnivaemoe_znashenie)) continue;
                if (dvijenie(1, 0, sravnivaemoe_znashenie)) continue;
                if (dvijenie(1, -1, sravnivaemoe_znashenie)) continue;
                if (dvijenie(0, -1, sravnivaemoe_znashenie)) continue;
                if (dvijenie(-1, -1, sravnivaemoe_znashenie)) continue;
                if (dvijenie(-1, 0, sravnivaemoe_znashenie)) continue;
                if (dvijenie(-1, 1, sravnivaemoe_znashenie)) continue;
                if (sravnivaemoe_znashenie == 210) {
                    if (dvijenie(0, 2, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(1, 2, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(2, 1, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(2, 0, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(2, -1, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(1, -2, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(0, -2, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(-1, -2, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(-2, -1, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(-2, 0, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(-2, 1, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(-1, 2, sravnivaemoe_znashenie)) continue;

                    if (dvijenie(0, 3, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(3, 0, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(0, -3, sravnivaemoe_znashenie)) continue;
                    if (dvijenie(-3, 0, sravnivaemoe_znashenie)) continue;
                }
                //System.out.println("End");
                contact = false;
                zapusk.dvijenie.goTo(zapusk.dvijenie.pozitsia_x, zapusk.dvijenie.pozitsia_y,
                        zapusk.dvijenie.pozitsia_z - glubinaProrezania -
                                zapusk.dvijenie.vusotaProhodaNadPoverhnostu, zapusk.dvijenie.scorostPerehoda, true);
            }
        }
    }

    //Аналіз з переміщенням на сусідній піксель
    private boolean dvijenie(int delta_x, int delta_y, int colorSravnivanui) {
        Color vuhodnoy = Color.BLUE;
        if (sravnenieSveta(massivToshekDlaKartinki, zapusk.dvijenie.pozitsia_x / 2, zapusk.dvijenie.pozitsia_y / 2,
                delta_x, delta_y, colorSravnivanui)) {

          /*  if (delta_x == -1 && delta_y == -1 && sravnenieSveta(massivToshekDlaKartinki, zapusk.dvijenie.pozitsia_x / 2, zapusk.dvijenie.pozitsia_y / 2,
                    -1, 0, colorSravnivanui)) {
                delta_y = 0;
            }
            if ((delta_x == 0 || delta_x == 1) && delta_y == -1 && sravnenieSveta(massivToshekDlaKartinki, zapusk.dvijenie.pozitsia_x / 2, zapusk.dvijenie.pozitsia_y / 2,
                    -1, 0, colorSravnivanui)) {
                delta_y = 0;
                delta_x = -1;
            }  */

            zapusk.dvijenie.goTo(zapusk.dvijenie.pozitsia_x + 2 * delta_x, zapusk.dvijenie.pozitsia_y + 2 * delta_y,
                    zapusk.dvijenie.pozitsia_z, scorostObrabotki, true);
            //System.out.println("x=" + zapusk.dvijenie.pozitsia_x + " y=" + zapusk.dvijenie.pozitsia_y);
            massivToshekDlaKartinki[zapusk.dvijenie.pozitsia_y / 2 * shirina + zapusk.dvijenie.pozitsia_x / 2] =
                    0Xff000000 | vuhodnoy.getRed() << 16 | vuhodnoy.getGreen() << 8 | vuhodnoy.getBlue();
            kolishesvo_narisovannuh_toshek += 1;
            if (tolkoKonturu) procent_vupolnenia = (kolishesvo_narisovannuh_toshek * 100) / kolishesvo_chernuh_toshek;
            else
                procent_vupolnenia = (kolishesvo_narisovannuh_toshek * 100) / (kolishesvo_chernuh_toshek + kolishesvo_krasnuh_toshek);
            jprogressbar_obrabotka.setValue(procent_vupolnenia);
            draw_obrablayemaiyKartinka.setImage(createImage(new MemoryImageSource(shirina, vusota,
                    massivToshekDlaKartinki, 0, shirina)), false);
            return true;
        } else return false;
    }

    //Реакція на закриття вікна або нажимання кнопки "Стоп"
    private void perervatuObrobku(final boolean exit, final boolean konethObrabotki) {
        //Вікно переривання/виходу
        JOptionPane joptionpane_vuhodIzProgramu = new JOptionPane();
        if (konethObrabotki) nowDoing = !nowDoing;
        if (!nowDoing) jbutton_pausaObrobka.doClick();
        int pressed;
        if (konethObrabotki) {
            pressed = 0;
        } else {
            pressed = joptionpane_vuhodIzProgramu.showOptionDialog(null, "Ви дійсно бажаєте перервати обробку?",
                    "Вихід/переривання обробки", JOptionPane.OK_CANCEL_OPTION, 0, null, new String[]{"Так", "Ні"}, "Ні");

        }
        if (pressed == 0) {
            if (!konethObrabotki) saveObrobkaToFile(true);
            jbutton_stopObrobka.setEnabled(false);
            jbutton_pausaObrobka.setEnabled(false);
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    draw_height = false;

                    if (zapusk.dvijenie.pozitsia_x > 1 ||
                            (zapusk.dvijenie.pozitsia_y != zapusk.dvijenie.cmechenieNashalneY - zapusk.snatieRazmerov.polosaOgranishenia * 10 && zapusk.dvijenie.pozitsia_y > 1) ||
                            (zapusk.dvijenie.pozitsia_z != zapusk.dvijenie.vusotaPodjomaNashalna && zapusk.dvijenie.pozitsia_z > 1)) {


                        zapusk.dvijenie.goTo(0, 0, 0, zapusk.dvijenie.scorostPerehoda, false);
                        zapusk.dvijenie.goTo(0, zapusk.dvijenie.cmechenieNashalneY - zapusk.snatieRazmerov.polosaOgranishenia * 10,
                                0, zapusk.dvijenie.scorostPerehoda, false);
                        zapusk.dvijenie.goTo(0, zapusk.dvijenie.pozitsia_y,
                                zapusk.dvijenie.vusotaPodjomaNashalna, zapusk.dvijenie.scorostPerehoda, false);
                        zapusk.dvijenie.pozitsia_x = 0;
                        zapusk.dvijenie.pozitsia_y = 0;
                        zapusk.dvijenie.pozitsia_z = 0;
                    }
                    jprogressbar_obrabotka.setValue(0);
                    jbutton_pausaObrobka.setText("Пауза");
                    jbutton_zavantajitiZobrajenna.setEnabled(true);
                    jbutton_rushneUpravlinna.setEnabled(true);
                    jbutton_pereituDoVumiruvanna.setEnabled(true);
                    jbutton_zminutuImg.setEnabled(true);
                    jprogressbar_obrabotka.setEnabled(false);
                    nowDoing = false;
                    draw_obrablayemaiyKartinka.setToolTipText(null);
                    jprogressbar_obrabotka.setToolTipText(null);
                    zapusk.dvijenie.notSignalXYZ(true, true, false);
                    if (!exit && konethObrabotki && jCheckBox_autoExit.isSelected()) {
                        String[] commands = {"shutdown", "-s"};
                        try {
                            Runtime.getRuntime().exec(commands);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    ;
                    if (potokObrabotki != null) potokObrabotki.stop();
                    if (!exit) Thread.currentThread().stop();
                    else {
                        zapusk.dvijenie.notSignalXYZ(true, true, true);
                        System.exit(1);
                    }
                }
            })).start();
        }
    }

    synchronized private void saveObrobkaToFile(boolean ProcessFinished) {
        try {
        	RandomAccessFile rf = FileUtils.getRandomAccersFile("tmpSave/ObrabkaSettings"+Param_Profile.getFileAppender()+".res");

            File imgFile = FileUtils.getFile("tmpSave\\imgFile"+Param_Profile.getFileAppender()+".bmp");
            if (ProcessFinished) {
                rf.writeInt(jscrollbar_scorostObrabotki.getValue());
                rf.writeInt(jscrollbar_tonalnist.getValue());
                rf.writeInt(kolishesvo_chernuh_toshek);
                rf.writeInt(kolishesvo_krasnuh_toshek);
                rf.writeInt(kolishesvo_narisovannuh_toshek);
                rf.writeInt(selectedItem);
                BufferedImage bi = imageToBufferedImage(createImage(new MemoryImageSource(shirina, vusota, massivToshekDlaKartinki, 0, shirina)));
                ImageIO.write(bi, "bmp", imgFile);
                rf.close();
            } else {
                rf.writeInt(0);
                rf.close();
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized private void loadObrobkaFromFile() {
        try {
        	RandomAccessFile rf = FileUtils.getRandomAccersFile("tmpSave/ObrabkaSettings"+Param_Profile.getFileAppender()+".res");
            File imgFile = FileUtils.getFile("tmpSave/imgFile"+Param_Profile.getFileAppender()+".bmp");
           
            rf.seek(0);
            if (rf.readInt() == 0) return;
            else {
                rf.seek(0);
                jscrollbar_scorostObrabotki.setValue(rf.readInt());
                jscrollbar_tonalnist.setValue(rf.readInt());
                kolishesvo_chernuh_toshek = rf.readInt();
                kolishesvo_krasnuh_toshek = rf.readInt();
                kolishesvo_narisovannuh_toshek = rf.readInt();
                selectedItem = rf.readInt();
                String temp = "";
                switch (selectedItem) {
                    case 1:
                        temp = jbutton_vurizatuKontyru.getText();
                        break;
                    case 2:
                        temp = jbutton_vugraivatuMonochromneZobrajenna.getText();
                        break;
                    case 3:
                        temp = jbutton_vugraivatuKontyru.getText();
                        break;
                }
                jbutton_prodovjituPoperednu.setText(jbutton_prodovjituPoperednu.getText() + " (" + temp + ")");
                procent_vupolnenia = (kolishesvo_narisovannuh_toshek * 100) / kolishesvo_chernuh_toshek;
                jprogressbar_obrabotka.setValue(procent_vupolnenia);
                Image imgDlaObrobki = ImageIO.read(imgFile);
                shirina = draw_obrablayemaiyKartinka.getWidth();
                vusota = draw_obrablayemaiyKartinka.getHeight();
                draw_obrablayemaiyKartinka.setImage(imgDlaObrobki, true);
                jbutton_prodovjituPoperednu.setEnabled(true);

                massivToshekDlaKartinki = new int[vusota * shirina];
                try {
                    PixelGrabber pg = new PixelGrabber(imgDlaObrobki, 0, 0, shirina, vusota, massivToshekDlaKartinki, 0, shirina);
                    pg.grabPixels();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
