package drawPanels;

import javax.swing.*;
import java.awt.*;

public class Draw_VusotaInstrumenta extends JPanel {
    private int height = 0;
    private int vusota;

    public void setHeight(int height) {
        this.height = height;
    }

    public void drawVusota(int vusota) {
        this.vusota = vusota;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(45789));
        g.fillRect(0, 0, 20, height);
        g.setColor(Color.BLACK);
        g.drawLine(0, height - 40, 20, height - 40);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3f));
        g2.setColor(Color.MAGENTA);
        g2.fillPolygon(new int[]{0, 7, 14}, new int[]{vusota + height - 60, vusota + height - 38, vusota + height - 60}, 3);
    }
}