package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class FileUtils {
	public static RandomAccessFile getRandomAccersFile(String sybPath) {
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(getFile(sybPath), "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return rf;
	}

	public static File getFile(String sybPath) {
		File fpng = new File(sybPath);
		if (!fpng.exists())
			fpng = new File("src/main/resources/" + sybPath);
		return fpng;
	}
}
