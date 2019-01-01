package drawPanels;

import javax.swing.*;
import java.awt.*;

public class Draw_ObrablayemaiyKartinka extends JPanel {
    Image img = null;
    int radiusCursor = 10;
    int xCenter = -radiusCursor;
    int yCenter = -radiusCursor;

    public void setImage(Image img,boolean repaint) {
        if (img!=null)
        this.img = img;
        if (repaint)repaint();
    }

    public void drawCursor(int xCenter, int yCenter) {
        this.xCenter = xCenter;
        this.yCenter = yCenter;
        repaint(xCenter - 2*radiusCursor, yCenter - 2*radiusCursor, 4*radiusCursor, 4*radiusCursor);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3f));
        g2.setColor(Color.MAGENTA);
        g2.drawLine(xCenter - radiusCursor, yCenter - radiusCursor, xCenter + radiusCursor, yCenter + radiusCursor);
        g2.drawLine(xCenter + radiusCursor, yCenter - radiusCursor, xCenter - radiusCursor, yCenter + radiusCursor);
        g.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(1f));

    }
}