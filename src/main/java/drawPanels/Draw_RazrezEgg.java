package drawPanels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class Draw_RazrezEgg extends JPanel {

    private int[] data;

    public void setData(int[] data) {
        this.data = data;
        repaint();
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.setColor(new Color(-8333707));
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        Graphics2D graphics2d = (Graphics2D) graphics;
        graphics2d.setStroke(new BasicStroke(3f));
        graphics2d.setColor(Color.MAGENTA);
        graphics.setColor(Color.BLUE);
        for (int i = 0; i < data.length - 1; i++) {
            graphics.drawLine(data[i] / 2, i, data[i + 1] / 2, i + 1);
        }

    }
}