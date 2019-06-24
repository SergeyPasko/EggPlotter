import Config.Param_Dvijenie;
import Config.Param_Profile;
import jnpout32.pPort;

/**
 * Created with IntelliJ IDEA. User: spasko Date: 16.09.14 Time: 15:55 To change
 * this template use File | Settings | File Templates.
 */
class Dvijenie {
	private Zapusk zapusk;
	volatile int pozitsia_x = 0;
	volatile int pozitsia_y = 0;
	volatile int pozitsia_z = 0;
	int popravka_x = 40000;
	int popravka_y = 40000;
	int popravka_z = 40000;
	int vusotaProhodaNadPoverhnostu = Param_Dvijenie.getInstance()
			.getProperty(Param_Dvijenie.VUSOTAPROHODANADPOVERHNOSTU);
	int glubinaProrezania = Param_Dvijenie.getInstance().getProperty(Param_Dvijenie.GLUBINAPROREZANIA);
	int shustvitelnostVumiruvanna;
	int scorostPerehoda = Param_Dvijenie.getInstance().getProperty(Param_Dvijenie.SCOROSTPEREHODA);
	int vusotaPodjomaNashalna = Param_Dvijenie.getInstance().getProperty(Param_Dvijenie.VUSOTAPODJOMANASHALNA);
	int cmechenieNashalneY = Param_Dvijenie.getInstance().getProperty(Param_Dvijenie.SMESHENIENASHALNE);
	int komandNaPovorot = 1600;
	private int vverhVnuzY[] = { 1, 4, 2, 8 };
	private int vlevoVpravoX[] = { 128, 32, 64, 16 };
	private int vverhVnuzZ[] = { 15, 14, 10, 8, 9, 1, 3, 7 };
	/*
	 * Перший Зміщення вверх/вниз на 100мм 1000команд поворот вліво/вправо на
	 * 360град 1580команд зміщення вверх/вниз по Z на 34мм 400команд 2000команд
	 */

	/*
	 * Другий Зміщення вверх/вниз на 166мм 2000команд поворот вліво/вправо на
	 * 360град 2420команд зміщення вверх/вниз по Z на 166мм 2000команд
	 * 2000команд
	 */

	private LPT_work lptWork = new LPT_work();
	private boolean izmenenieNapravleniaDvigeniaX = false;
	private int hagovVOtsshetePopravkaX = Param_Dvijenie.getInstance()
			.getProperty(Param_Dvijenie.HAGOVVOTSSHETEPOPRAVKAX);
	private boolean izmenenieNapravleniaDvigeniaY = false;
	private int hagovVOtsshetePopravkaY = Param_Dvijenie.getInstance()
			.getProperty(Param_Dvijenie.HAGOVVOTSSHETEPOPRAVKAY);
	private boolean izmenenieNapravleniaDvigeniaZ = false;
	private int hagovVOtsshetePopravkaZ = Param_Dvijenie.getInstance()
			.getProperty(Param_Dvijenie.HAGOVVOTSSHETEPOPRAVKAZ);

	private int pressedBite = 0;

	public Dvijenie(Zapusk zapusk) {
		this.zapusk = zapusk;
		pressedBite = Integer.highestOneBit(lptWork.read((short) (pPort.portAddress + 1)));
		shustvitelnostVumiruvanna = zapusk.snatieRazmerov.jscrollBar_chustvitelnostVumiruvanna.getValue();
		if (Param_Profile.isSecondGeneration()) {
			vlevoVpravoX = new int[] { 1, 3, 2, 6, 4, 12, 8, 9 };
			vverhVnuzY = new int[] { 128, 160, 32, 96, 64, 80, 16, 144 };
			vverhVnuzZ = new int[] { 15, 14, 10, 2, 3, 1, 9, 13 };
		}
	}

	// Команди для моторів на переміщення на один крок, з врахуванням люфтів
	public void dvijenieNaOdinHag(Napravlenie_dvigenia napravlenie, boolean izmenenieKoordinat, int scorost) {
		int multiStepsZ = 4;
		int multiScorostZ=scorost;
		if (Param_Profile.isSecondGeneration()) {
			multiStepsZ = 0;
		} else{
			multiScorostZ=scorost/2;
		}
		switch (napravlenie) {
		case VVERH:
			popravkaLuftov(Napravlenie_dvigenia.VVERH, scorost);
			if (izmenenieKoordinat)
				pozitsia_y--;
			else
				popravka_y--;
			podashaSignalaXY(scorost);
			break;
		case VNUZ:
			popravkaLuftov(Napravlenie_dvigenia.VNUZ, scorost);
			if (izmenenieKoordinat)
				pozitsia_y++;
			else
				popravka_y++;
			podashaSignalaXY(scorost);
			break;
		case VLEVO:
			popravkaLuftov(Napravlenie_dvigenia.VLEVO, scorost);
			if (izmenenieKoordinat)
				pozitsia_x--;
			else
				popravka_x--;
			podashaSignalaXY(scorost);
			break;
		case VPRAVO:
			popravkaLuftov(Napravlenie_dvigenia.VPRAVO, scorost);
			if (izmenenieKoordinat)
				pozitsia_x++;
			else
				popravka_x++;
			podashaSignalaXY(scorost);
			break;
		case VVERH_VLEVO:
			popravkaLuftov(Napravlenie_dvigenia.VVERH, scorost);
			popravkaLuftov(Napravlenie_dvigenia.VLEVO, scorost);
			if (izmenenieKoordinat) {
				pozitsia_y--;
				pozitsia_x--;
			} else {
				popravka_y--;
				popravka_x--;
			}
			podashaSignalaXY(scorost);
			break;
		case VVERH_VPRAVO:
			popravkaLuftov(Napravlenie_dvigenia.VVERH, scorost);
			popravkaLuftov(Napravlenie_dvigenia.VPRAVO, scorost);
			if (izmenenieKoordinat) {
				pozitsia_y--;
				pozitsia_x++;
			} else {
				popravka_y--;
				popravka_x++;
			}
			podashaSignalaXY(scorost);
			break;
		case VNUZ_VLEVO:
			popravkaLuftov(Napravlenie_dvigenia.VLEVO, scorost);
			popravkaLuftov(Napravlenie_dvigenia.VNUZ, scorost);
			if (izmenenieKoordinat) {
				pozitsia_y++;
				pozitsia_x--;
			} else {
				popravka_y++;
				popravka_x--;
			}
			podashaSignalaXY(scorost);
			break;
		case VNUZ_VPRAVO:
			popravkaLuftov(Napravlenie_dvigenia.VNUZ, scorost);
			popravkaLuftov(Napravlenie_dvigenia.VPRAVO, scorost);
			if (izmenenieKoordinat) {
				pozitsia_y++;
				pozitsia_x++;
			} else {
				popravka_y++;
				popravka_x++;
			}
			podashaSignalaXY(scorost);
			break;
		case PODNAT_INSRUMENT:
			popravkaLuftov(Napravlenie_dvigenia.PODNAT_INSRUMENT, scorost);
			if (izmenenieKoordinat)
				pozitsia_z--;
			else
				popravka_z--;
			podashaSignalaZ(multiScorostZ);
			for (int i = 0; i < multiStepsZ; i++) {
				popravka_z--;
				podashaSignalaZ(multiScorostZ);
			}
			break;
		case OPUSTIT_INSRUMENT:
			popravkaLuftov(Napravlenie_dvigenia.OPUSTIT_INSRUMENT, scorost);

			if (izmenenieKoordinat)
				pozitsia_z++;
			else
				popravka_z++;
			podashaSignalaZ(scorost);

			for (int i = 0; i < multiStepsZ; i++) {
				popravka_z++;
				podashaSignalaZ(scorost);
			}
			break;

		}
		// System.out.println(pozitsia_x+" "+pozitsia_y+" "+pozitsia_z);
		zapusk.obrobkaEgg.draw_obrablayemaiyKartinka.drawCursor(pozitsia_x / 2, pozitsia_y / 2);
		if (zapusk.obrobkaEgg.draw_height)
			zapusk.obrobkaEgg.draw_vusotaInstrumenta.drawVusota(pozitsia_z - zapusk.snatieRazmerov.razrez[pozitsia_y]);
		else
			zapusk.obrobkaEgg.draw_vusotaInstrumenta.drawVusota(-100);
	}

	private void popravkaLuftov(Napravlenie_dvigenia napravnenie, int scorost) {
		switch (napravnenie) {
		case VNUZ:
			if (izmenenieNapravleniaDvigeniaY)
				for (int i = 0; i < hagovVOtsshetePopravkaY; i++) {
					popravka_y++;
					podashaSignalaXY(scorost);
				}
			izmenenieNapravleniaDvigeniaY = false;
			break;
		case VVERH:
			if (!izmenenieNapravleniaDvigeniaY)
				for (int i = 0; i < hagovVOtsshetePopravkaY; i++) {
					popravka_y--;
					podashaSignalaXY(scorost);
				}
			izmenenieNapravleniaDvigeniaY = true;
			break;
		case VLEVO:
			if (!izmenenieNapravleniaDvigeniaX)
				for (int i = 0; i < hagovVOtsshetePopravkaX; i++) {
					popravka_x--;
					podashaSignalaXY(scorost);
				}
			izmenenieNapravleniaDvigeniaX = true;
			break;
		case VPRAVO:
			if (izmenenieNapravleniaDvigeniaX)
				for (int i = 0; i < hagovVOtsshetePopravkaX; i++) {
					popravka_x++;
					podashaSignalaXY(scorost);
				}
			izmenenieNapravleniaDvigeniaX = false;
			break;
		case PODNAT_INSRUMENT:
			if (!izmenenieNapravleniaDvigeniaZ)
				for (int i = 0; i < hagovVOtsshetePopravkaZ; i++) {
					popravka_z--;
					podashaSignalaZ(scorost);
				}
			izmenenieNapravleniaDvigeniaZ = true;
			break;
		case OPUSTIT_INSRUMENT:
			if (izmenenieNapravleniaDvigeniaZ)
				for (int i = 0; i < hagovVOtsshetePopravkaZ; i++) {
					popravka_z++;
					podashaSignalaZ(scorost);
				}
			izmenenieNapravleniaDvigeniaZ = false;
			break;
		default:
			throw new RuntimeException("Incorrect using of luft correcting logic");
		}
	}

	// Команда на порт для моторів, що переміщюють заготовку
	private void podashaSignalaXY(int scorost) {
		sleepNano(scorost * 10000);
		// if (pozitsia_z) System.out.println("x="+pozitsia_x+" y="+pozitsia_y+"
		// z="+pozitsia_z);
		lptWork.write(pPort.portAddress, (byte) (vverhVnuzY[(pozitsia_y + popravka_y) % vverhVnuzY.length]
				+ vlevoVpravoX[(pozitsia_x + popravka_x) % vlevoVpravoX.length]));

	}

	// Команда на порт для мотору, що переміщює інструмент
	private void podashaSignalaZ(int scorost) {
		sleepNano(scorost * 10000);
		lptWork.write((short) (pPort.portAddress + 2),
				(byte) (vverhVnuzZ[(pozitsia_z + popravka_z) % vverhVnuzZ.length]));

	}

	private void sleepNano(long interval) {
		long start = System.nanoTime();
		long end = 0;
		do {
			end = System.nanoTime();
		} while (start + interval >= end);
	}

	void notSignalXYZ(boolean notX, boolean notY, boolean notZ) {
		lptWork.write(pPort.portAddress, (byte) ((notX ? 0 : vverhVnuzY[(pozitsia_y + popravka_y) % 4])
				+ (notY ? 0 : vlevoVpravoX[(pozitsia_x + popravka_x) % 4])));
		lptWork.write((short) (pPort.portAddress + 2), (byte) (notZ ? 11 : vverhVnuzZ[(pozitsia_z + popravka_z) % 8]));
	}

	// Переміщення в вказану точку
	public void goTo(int x, int y, int z, final int scorost, boolean popravkaKruviznuPoZ) {
		int xx = pozitsia_x;
		int yy = pozitsia_y;
		int zz = pozitsia_z;
		if (xx == x && yy == y && zz == z)
			return;
		if (z - zz > 0)
			for (int i = 1; i <= z - zz; i++) {
				zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.OPUSTIT_INSRUMENT, true, scorost);
			}
		else
			for (int i = 1; i <= zz - z; i++) {
				zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.PODNAT_INSRUMENT, true, scorost);
			}
		if (y - yy > 0 && x - xx > 0)
			for (int i = 1; i <= Math.min(y - yy, x - xx); i++) {
				(new PopravkaKruviznuPoZ(popravkaKruviznuPoZ, true, scorost) {
					@Override
					public void stepTemplate() {
						zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.VNUZ_VPRAVO, true, scorost);
					}
				}).execute();
			}
		xx = pozitsia_x;
		yy = pozitsia_y;
		if (y - yy > 0 && x - xx < 0)
			for (int i = 1; i <= Math.min(y - yy, xx - x); i++) {
				(new PopravkaKruviznuPoZ(popravkaKruviznuPoZ, true, scorost) {
					@Override
					public void stepTemplate() {
						zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.VNUZ_VLEVO, true, scorost);
					}
				}).execute();
			}
		xx = pozitsia_x;
		yy = pozitsia_y;
		if (y - yy < 0 && x - xx > 0)

			for (int i = 1; i <= Math.min(yy - y, x - xx); i++) {
				(new PopravkaKruviznuPoZ(popravkaKruviznuPoZ, false, scorost) {
					@Override
					public void stepTemplate() {
						zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.VVERH_VPRAVO, true, scorost);
					}
				}).execute();
			}
		xx = pozitsia_x;
		yy = pozitsia_y;
		if (y - yy < 0 && x - xx < 0)
			for (int i = 1; i <= Math.min(yy - y, xx - x); i++) {
				(new PopravkaKruviznuPoZ(popravkaKruviznuPoZ, false, scorost) {
					@Override
					public void stepTemplate() {
						zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.VVERH_VLEVO, true, scorost);
					}
				}).execute();
			}
		xx = pozitsia_x;
		yy = pozitsia_y;
		if (y - yy > 0)
			for (int i = 1; i <= y - yy; i++) {
				(new PopravkaKruviznuPoZ(popravkaKruviznuPoZ, true, scorost) {
					@Override
					public void stepTemplate() {
						zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.VNUZ, true, scorost);
					}
				}).execute();
			}
		else
			for (int i = 1; i <= yy - y; i++) {
				(new PopravkaKruviznuPoZ(popravkaKruviznuPoZ, false, scorost) {
					@Override
					public void stepTemplate() {
						zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.VVERH, true, scorost);
					}
				}).execute();
			}
		if (x - xx > 0)
			for (int i = 1; i <= x - xx; i++) {
				zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.VPRAVO, true, scorost);
			}
		else
			for (int i = 1; i <= xx - x; i++) {
				zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.VLEVO, true, scorost);
			}
	}

	// Перевірка для датчика чи є контакт з поверхнею
	public boolean nowKontact() {
		return pressedBite != Integer.highestOneBit(lptWork.read((short) (pPort.portAddress + 1)));
	}

	// Виконуються зсуви по Z в залежності від кривизни (проміряних радіусів)
	// яйця
	private abstract class PopravkaKruviznuPoZ implements Runnable {
		private boolean popravkaKruviznuPoZ;
		private boolean uvilishenieY;
		private int scorost;

		public PopravkaKruviznuPoZ(boolean popravkaKruviznuPoZ, boolean uvilishenieY, int scorost) {
			this.popravkaKruviznuPoZ = popravkaKruviznuPoZ;
			this.uvilishenieY = uvilishenieY;
			this.scorost = scorost;
		}

		public abstract void stepTemplate();

		public void execute() {
			Thread popravkaKruviznuPoZThread = null;
			if (popravkaKruviznuPoZ) {
				popravkaKruviznuPoZThread = new Thread(this);
				popravkaKruviznuPoZThread.start();
			}

			stepTemplate();
			try {
				if (popravkaKruviznuPoZThread != null) {
					popravkaKruviznuPoZThread.join();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			int delta;
			if (pozitsia_y + 1 > zapusk.snatieRazmerov.pomeraniRazmeru.length - 1 || pozitsia_y - 1 < 0)
				return;
			if (uvilishenieY)
				delta = zapusk.snatieRazmerov.pomeraniRazmeru[pozitsia_y + 1][4]
						- zapusk.snatieRazmerov.pomeraniRazmeru[pozitsia_y][4];
			else
				delta = zapusk.snatieRazmerov.pomeraniRazmeru[pozitsia_y - 1][4]
						- zapusk.snatieRazmerov.pomeraniRazmeru[pozitsia_y][4];
			if (delta == 0)
				return;
			if (delta > 0)
				for (int i = 1; i <= delta; i++) {
					zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.OPUSTIT_INSRUMENT, true, scorost);
				}
			else
				for (int i = 1; i <= -delta; i++) {
					zapusk.dvijenie.dvijenieNaOdinHag(Napravlenie_dvigenia.PODNAT_INSRUMENT, true, scorost);
				}
		}
	}
}
