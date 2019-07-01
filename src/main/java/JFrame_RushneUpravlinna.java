import Config.Param_RushneUpravlinna;
import utils.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: spasko
 * Date: 10.09.14
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
class JFrame_RushneUpravlinna extends JFrame {
	private static final long serialVersionUID = 1L;
	//Параметри та елементи вікна ручного керування
    private Zapusk zapusk;
    JPanel mainPanel;
    JLabel jlabel_contact;
    int storonaElementa = Param_RushneUpravlinna.getInstance().getProperty(Param_RushneUpravlinna.VUSOTA_ELEMENTA);
    int vusotaOkna = 3 * storonaElementa + 30;
    int shirinaOkna = 4 * storonaElementa;
    Thread potokRushnogoUpravlinna;
    Napravlenie_dvigenia napravlenie_dvigenia = null;
    boolean nowPressed = false;


    public JFrame_RushneUpravlinna(Zapusk zapusk) {
        this.zapusk = zapusk;
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
            vupolnenie();
            potokRushnogoUpravlinna.start();
            potokRushnogoUpravlinna.suspend();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws IOException {
        //Основне вікно
        mainPanel = (JPanel) this.getContentPane();
        mainPanel.setLayout(null);
        this.setSize(new Dimension(shirinaOkna, vusotaOkna));
        mainPanel.setSize(new Dimension(shirinaOkna, vusotaOkna));
        this.setTitle("Ручне керування");
        this.setResizable(false);

        //Кнопки
        addButton("Icons/VverhVlevo.png", "-X,-Y", 1, 1, 0, 0);
        addButton("Icons/Vverh.png", "-Y", 1, 1, 1, 0);
        addButton("Icons/VverhVpravo.png", "+X,-Y", 1, 1, 2, 0);
        addButton("Icons/Vlevo.png", "-X", 1, 1, 0, 1);
        addButton("Icons/Vpravo.png", "+X", 1, 1, 2, 1);
        addButton("Icons/VnuzVlevo.png", "-X,+Y", 1, 1, 0, 2);
        addButton("Icons/Vnuz.png", "+Y", 1, 1, 1, 2);
        addButton("Icons/VnuzVpravo.png", "+X,+Y", 1, 1, 2, 2);
        addButton("Icons/VverhZ.png", "-Z", 1, 1.5, 3, 0);
        addButton("Icons/VnuzZ.png", "+Z", 1, 1.5, 3, 1.5);

        jlabel_contact = new JLabel();
        jlabel_contact.setSize(storonaElementa, storonaElementa);
        jlabel_contact.setLocation(storonaElementa, mainPanel.getHeight() - 30 -
                storonaElementa * 2);
        if (zapusk.dvijenie.nowKontact())
            jlabel_contact.setIcon(new ImageIcon(ImageIO.read(FileUtils.getFile("Icons/Kontact.png"))));
        else jlabel_contact.setIcon(null);
        mainPanel.add(jlabel_contact);
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            zapusk.snatieRazmerov.jbutton_rushneUpravlinna.doClick();
        }
    }

    private JButton addButton(final String iconFileName, final String msg, double razmerX,
                              double razmerY, final double pozitsiaX, final double pozitsiaY) {
        JButton jButton = new JButton();
        try {
        	File fpng = FileUtils.getFile(iconFileName);
			Image img = ImageIO.read(fpng);
			
			if (img.getWidth(null) >= img.getHeight(null))
                img = img.getScaledInstance(storonaElementa, storonaElementa * img.getHeight(null) / img.getWidth(null), 15);
            else
                img = img.getScaledInstance(storonaElementa * img.getWidth(null) / img.getHeight(null), storonaElementa, 15);
            jButton .setIcon(new ImageIcon(img));
        } catch (IOException ignored) {
        }
        jButton.setToolTipText(msg);
        jButton.setSize((int) (razmerX * storonaElementa), (int) (razmerY * storonaElementa));
        //Реація на нажими мишкою, реалізовано для реалізації довгого нажимання
        jButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                switch ((int) (pozitsiaX * 10 + pozitsiaY)) {
                    case 0:
                        napravlenie_dvigenia = Napravlenie_dvigenia.VVERH_VLEVO;
                        break;
                    case 10:
                        napravlenie_dvigenia = Napravlenie_dvigenia.VVERH;
                        break;
                    case 20:
                        napravlenie_dvigenia = Napravlenie_dvigenia.VVERH_VPRAVO;
                        break;
                    case 1:
                        napravlenie_dvigenia = Napravlenie_dvigenia.VLEVO;
                        break;
                    case 21:
                        napravlenie_dvigenia = Napravlenie_dvigenia.VPRAVO;
                        break;
                    case 2:
                        napravlenie_dvigenia = Napravlenie_dvigenia.VNUZ_VLEVO;
                        break;
                    case 12:
                        napravlenie_dvigenia = Napravlenie_dvigenia.VNUZ;
                        break;
                    case 22:
                        napravlenie_dvigenia = Napravlenie_dvigenia.VNUZ_VPRAVO;
                        break;
                    case 30:
                        napravlenie_dvigenia = Napravlenie_dvigenia.PODNAT_INSRUMENT;
                        break;
                    case 31:
                        napravlenie_dvigenia = Napravlenie_dvigenia.OPUSTIT_INSRUMENT;
                        break;
                    default:
                        napravlenie_dvigenia = null;
                        break;
                }
                nowPressed = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                nowPressed = false;
            }
        });

        jButton.setLocation((int) (pozitsiaX * storonaElementa), (int) (mainPanel.getHeight() - 30 -
                storonaElementa * (3 - pozitsiaY)));
        mainPanel.add(jButton);
        return jButton;
    }

    //Запуск потока який підхоплює кліки по клавішам
    private void vupolnenie() {
        potokRushnogoUpravlinna = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (nowPressed) {
                        zapusk.dvijenie.dvijenieNaOdinHag(napravlenie_dvigenia, false, zapusk.dvijenie.scorostPerehoda*2);
                        if (zapusk.dvijenie.nowKontact())
                            try {
                            	File fpng = FileUtils.getFile("Icons/Kontact.png");
                                jlabel_contact.setIcon(new ImageIcon(ImageIO.read(fpng)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        else jlabel_contact.setIcon(null);
                    } else java.util.concurrent.locks.LockSupport.parkNanos(10000);
                }
            }
        });
    }
}
