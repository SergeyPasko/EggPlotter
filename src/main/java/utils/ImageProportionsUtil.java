package utils;

import Config.Param_Profile;

public class ImageProportionsUtil {
	public static int getMashtabedValueY(int y) {
		if (Param_Profile.isSecondGeneration()) {
			return y * 12 / 10;
		} else {
			return y;
		}
	}

	public static int getMashtabedValueX(int x) {
		if (Param_Profile.isSecondGeneration()) {
			return x * 15 / 10;
		} else {
			return x;
		}
	}
}
