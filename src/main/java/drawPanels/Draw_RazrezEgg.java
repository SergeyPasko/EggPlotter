package drawPanels;

import javax.swing.*;
import java.awt.*;

public class Draw_RazrezEgg extends JPanel {

    private int[] data;

    public void setData(int[] data) {
        this.data = data;
        repaint();
    }





    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(-8333707));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3f));
        g2.setColor(Color.MAGENTA);
        g.setColor(Color.BLUE);
        for (int i=0;i<data.length-1;i++){
            g.drawLine(data[i]/2,i,data[i+1]/2,i+1);
        }

    }
}