package upgradeImg;

import java.awt.Adjustable;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;

import drawPanels.Draw_ModifyImg;

public class LineToPoints extends UpgradeImg {
	private static final long serialVersionUID = 1L;
	private JCheckBox jCheckBox_simetria = new JCheckBox("Симетрія (відстань між точками по X та Y однакова)");
	private JScrollBar jScrollBar_shilnistX;
	private JScrollBar jScrollBar_shilnistY;
	private JLabel jLabel_shilnistX;
	private JLabel jLabel_shilnistY;
	private MyAdjusmentListener myAdjusmentListener = new MyAdjusmentListener();

	@Override
	public String getNameTabletPane() {
		return "Лінії точками";
	}

	@Override
	public Image getSummaImg() {
		int[] imgEnd = new int[imgStart.length];
		for (int i = 0; i < imgEnd.length; i++) {
			imgEnd[i] = (0Xff000000 | 255 << 16 | 255 << 8 | 255);
		}
		Iterator<Point> it = points.iterator();

		while (it.hasNext()) {
			Point tmpPoint = it.next();
			imgEnd[tmpPoint.y * imgShirina + tmpPoint.x] = 0Xff000000;
			if (tmpPoint.y + 1 <= imgVusota)
				imgEnd[(tmpPoint.y + 1) * imgShirina + tmpPoint.x] = 0Xff000000;
		}

		return createImage(new MemoryImageSource(imgShirina, imgVusota, imgEnd, 0, imgShirina));

	}

	@Override
	public void createPoints() {
		int[] imgTemp = new int[imgStart.length];
		imgTemp = imgStart.clone();
		int max = getMaxRazrez(razrez);
		Point tmpPoint = null;
		points = new ArrayList<Point>();

		for (int k = 0; k < imgVusota-1; k++) {
			for (int j = 0; j < imgShirina; j++) {
				if (imgTemp[k * imgShirina + j] == 0Xff000000) {
					tmpPoint = new Point(j, k);
					int xComp = jScrollBar_shilnistX.getValue() * max / razrez[tmpPoint.y];
					for (int q = -xComp / 2; q <= xComp / 2; q++) {
						for (int w = -jScrollBar_shilnistY.getValue() / 2; w <= jScrollBar_shilnistY.getValue()
								/ 2; w++) {
							int curPos = (k + w) * imgShirina + j + q;
							if (w * w + q * q > xComp * xComp || curPos >= imgStart.length || curPos < 0)
								continue;
							imgTemp[curPos] = 333;
						}
					}
					imgTemp[imgShirina * tmpPoint.y + tmpPoint.x] = 100;
					points.add(tmpPoint);
				}
			}
		}
	}

	public LineToPoints(int width, int height, int[] razrez, Draw_ModifyImg draw_modifyImg) {
		super(width, height, razrez, draw_modifyImg);
		jCheckBox_simetria.setSize(width, height / 7);
		jCheckBox_simetria.setLocation(0, 0);
		jCheckBox_simetria.setHorizontalAlignment(SwingConstants.CENTER);
		jCheckBox_simetria.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (((JCheckBox) e.getItem()).isSelected()) {
					jScrollBar_shilnistY.setValue(jScrollBar_shilnistX.getValue());
					jScrollBar_shilnistY.setEnabled(false);
				} else
					jScrollBar_shilnistY.setEnabled(true);
			}
		});
		add(jCheckBox_simetria);
		jScrollBar_shilnistX = new JScrollBar(Adjustable.HORIZONTAL, 10, 1, 5, 31);
		jScrollBar_shilnistX.addAdjustmentListener(myAdjusmentListener);
		jLabel_shilnistX = new JLabel();
		super.setScrollBar(new JLabel("Відстань між точками по X"), jScrollBar_shilnistX, jLabel_shilnistX, 1);
		jScrollBar_shilnistY = new JScrollBar(Adjustable.HORIZONTAL, 10, 1, 5, 31);
		jScrollBar_shilnistY.addAdjustmentListener(myAdjusmentListener);
		jLabel_shilnistY = new JLabel();
		super.setScrollBar(new JLabel("Відстань між точками по Y"), jScrollBar_shilnistY, jLabel_shilnistY, 2);

		jCheckBox_simetria.doClick();
		writeLabelText();
	}

	private void writeLabelText() {
		jLabel_shilnistX.setText((double) jScrollBar_shilnistX.getValue() / 5 + " мм");
		jLabel_shilnistY.setText((double) jScrollBar_shilnistY.getValue() / 5 + " мм");
	}

	private class MyAdjusmentListener implements AdjustmentListener {

		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
			if (jCheckBox_simetria.isSelected()) {
				jScrollBar_shilnistY.setValue(jScrollBar_shilnistX.getValue());
			}
			writeLabelText();
			createPoints();
			drawPoints();
		}
	}

}
