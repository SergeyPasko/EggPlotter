package drawPanels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class Draw_SnatieRazmerov extends JPanel {
    private static final long serialVersionUID = 1L;
    public int[][] massivKoordinat = new int[0][5];

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

        drawEggCurve(graphics, 0, Color.BLUE);
        drawEggCurve(graphics, 1, Color.GREEN);
        drawEggCurve(graphics, 2, Color.MAGENTA);
        drawEggCurve(graphics, 3, Color.RED);

        Graphics2D graphics2d = (Graphics2D) graphics;
        graphics2d.setStroke(new BasicStroke(2f));
        drawEggCurve(graphics, 4, Color.BLACK);

    }

    private void drawEggCurve(Graphics g, int prohod, Color color) {
        g.setColor(color);
        for (int i = 0; i < massivKoordinat.length - 1; i++) {
            g.drawLine(i, massivKoordinat[i][prohod], i + 1, massivKoordinat[i + 1][prohod]);
        }
    }

    public void addPoint(int x, int y, int prohod) {
        massivKoordinat[x][prohod - 1] = y;
        repaint();
    }
}