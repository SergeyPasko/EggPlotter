package drawPanels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class Draw_SnatieRazmerov extends JPanel
{
    public int[][] massivKoordinat=new int[0][5];
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.white);
        g.fillRect(0,0,this.getWidth(),this.getHeight());
        g.setColor(Color.BLUE);
        for (int i=0;i<massivKoordinat.length-1;i++){
            g.drawLine(i,massivKoordinat[i][0],i+1,massivKoordinat[i+1][0]);
        }
        g.setColor(Color.GREEN);
        for (int i=0;i<massivKoordinat.length-1;i++){
            g.drawLine(i,massivKoordinat[i][1],i+1,massivKoordinat[i+1][1]);
        }
        g.setColor(Color.magenta);
        for (int i=0;i<massivKoordinat.length-1;i++){
            g.drawLine(i,massivKoordinat[i][2],i+1,massivKoordinat[i+1][2]);
        }
        g.setColor(Color.RED);
        for (int i=0;i<massivKoordinat.length-1;i++){
            g.drawLine(i,massivKoordinat[i][3],i+1,massivKoordinat[i+1][3]);
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(Color.BLACK);
        for (int i=0;i<massivKoordinat.length-1;i++){
            g2.drawLine(i,massivKoordinat[i][4],i+1,massivKoordinat[i+1][4]);
        }
    }
    public void addPoint(int x,int y,int prohod){
        massivKoordinat[x][prohod-1]=y;
        repaint();
    }
}