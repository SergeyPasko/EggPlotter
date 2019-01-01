package drawPanels;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Draw_ModifyImg extends JPanel {
    Image img = null;
    int radiusCursor = 2;

    ArrayList<Point>points=new ArrayList<Point>();

    public void setImage(Image img,boolean repaint) {
        if (img!=null)
        this.img = img;
        if (repaint)repaint();
    }

    public Image getImg() {
        return img;
    }

    public void setPoints(ArrayList<Point>points){
        this.points=points;
        repaint();
    }



    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.MAGENTA);
        Iterator it=points.iterator();

        while (it.hasNext()){
            Point ptemp=(Point)it.next();
            int xCenter = (int)ptemp.getX();
            int yCenter = (int)ptemp.getY();
        g2.drawLine(xCenter - radiusCursor, yCenter - radiusCursor, xCenter + radiusCursor, yCenter + radiusCursor);
        g2.drawLine(xCenter + radiusCursor, yCenter - radiusCursor, xCenter - radiusCursor, yCenter + radiusCursor);
        }

    }
}