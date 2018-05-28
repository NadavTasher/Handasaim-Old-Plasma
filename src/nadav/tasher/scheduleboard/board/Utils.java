package nadav.tasher.scheduleboard.board;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

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
	public static String dayConvert(int dayOfWeek) {
		switch(dayOfWeek) {
		case 1:
			return "ראשון";
		case 2:
			return "שני";
		case 3:
			return "שלישי";
		case 4:
			return "רביעי";
		case 5:
			return "חמישי";
		case 6:
			return "שישי";
		case 7:
			return "שבת";
		}
		return "אין לי מושג איזה יום היום";
	}
	
	public static JLabel getLabel(String text) {
		JLabel label = new JLabel(text);
		Font bigger = Font.getFont(Font.SANS_SERIF, label.getFont()).deriveFont(20.0f);
		label.setForeground(Color.BLACK);
		label.setFont(bigger);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setAlignmentY(Component.CENTER_ALIGNMENT);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setVerticalTextPosition(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		return label;
	}
	public static JLabel getClassLabel(String name, String teacher) {
		return getLabelFormatted("<b>" + name + "</b><br/>" + teacher);
	}

	public static JLabel getLabelFormatted(String text) {
		return getLabel("<html>" + text + "</html>");
	}
	
	public static void smallifyFont(JLabel l) {
		Font bigger = Font.getFont(Font.SANS_SERIF, l.getFont()).deriveFont(16.0f);
		l.setFont(bigger);
	}
	
	public static void tellUser(String text) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		JFrame popup = new JFrame("Notice");
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setVerticalTextPosition(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		popup.setContentPane(label);
		popup.setPreferredSize(new Dimension(200, 50));
		popup.setMinimumSize(popup.getPreferredSize());
		popup.setMaximumSize(popup.getPreferredSize());
		popup.setLocation((screen.width - popup.getWidth()) / 2, (screen.height - popup.getHeight()) / 2);
		popup.setAlwaysOnTop(true);
		popup.setUndecorated(true);
		popup.pack();
		popup.setVisible(true);
		// Start Timer
		Thread timer = new Thread(new Runnable() {

			@Override
			public void run() {
				final int seconds = 10;
				for (int second = seconds; second >= 0; second--) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
				popup.setVisible(false);
			}
		});
		timer.start();
	}
}
