package nadav.tasher.scheduleboard.board;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
	public static String readFile(File f) {
		if (f.exists()) {
			try {
				InputStream is = new FileInputStream(f);
				BufferedReader buf = new BufferedReader(new InputStreamReader(is));
				String line = buf.readLine();
				StringBuilder sb = new StringBuilder();
				while (line != null) {
					sb.append(line).append("\n");
					line = buf.readLine();
				}
				buf.close();
				String fileAsString = sb.toString();
				return fileAsString;
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}
}
