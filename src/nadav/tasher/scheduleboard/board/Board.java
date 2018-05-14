package nadav.tasher.scheduleboard.board;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.json.JSONException;
import org.json.JSONObject;

public class Board {
	
	// Static Values
		static final String programName = "Handasaim Schedule Board";
		static final double programVersion = 0.1;
		static final String programReleaseDate = "~May 2018";

	// Private Values
	private static final File configuration = new File(System.getProperty("user.dir"), "configuration.json");
	private static JFrame mainFrame;
	private static JSONObject settings;

	public static void main(String[] args) {
		loadTheme();
		if (loadConfiguration()) {
			loadGUI();
			loadUpdater();
		} else {
			tellUser("Failed To Load Configuration.");
		}
	}

	private static void loadTheme() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
			tellUser("Failed To Set Desired Theme.");
		}
	}

	private static void tellUser(String text) {
		JFrame popup = new JFrame(programName);
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setVerticalTextPosition(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		popup.setContentPane(label);
		popup.setPreferredSize(new Dimension(200, 50));
		popup.setMinimumSize(popup.getPreferredSize());
		popup.setMaximumSize(popup.getPreferredSize());
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

	private static boolean loadConfiguration() {
		try {
			String fileContents = Utils.readFile(configuration);
			if (fileContents != null) {
				settings = new JSONObject();
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
	}

	private static void loadGUI() {
		mainFrame=new JFrame(programName);
		mainFrame.setUndecorated(true);
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		
		
		mainFrame.setVisible(true);
	}

	private static void loadUpdater() {

	}
}
