package nadav.tasher.scheduleboard.ota;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Installer {
	private static final String latestOTA = "http://p.nockio.com/handasaim/board/ota/"+Checker.otaFile;

	public static void main(String[] args) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		JFrame popup = new JFrame("OTA Installer");
		JLabel label = new JLabel("Installer Started.");
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
		try {
			URL website = new URL(latestOTA);

			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(Checker.runnableFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			label.setText("OTA Installation OK");
			Runtime.getRuntime().exec("java -jar " + Checker.runnableFile.toString());
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
					System.exit(0);
				}
			});
			timer.start();
		} catch (Exception e) {
			label.setText("OTA Installation Failed!");
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
					System.exit(0);
				}
			});
			timer.start();
		}
	}

}
