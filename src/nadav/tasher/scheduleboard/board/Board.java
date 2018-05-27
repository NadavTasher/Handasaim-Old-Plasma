package nadav.tasher.scheduleboard.board;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Scrollbar;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.apache.commons.logging.impl.ServletContextCleaner;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import nadav.tasher.scheduleboard.ota.Checker;
import nadav.tasher.scheduleboard.ota.Checker.OTAListener;

public class Board {

	// Static Values
	static final String programName = "Handasaim Schedule Board";
	static final double programVersion = 0.1;
	static final String programReleaseDate = "~May 2018";

	// Private Values
	private static final File configuration = new File(System.getProperty("user.dir"), "configuration.json");
	private static final File scheduleFileXLSX = new File(System.getProperty("user.dir"), "schedule.xlsx");
	private static final File scheduleFileXLS = new File(System.getProperty("user.dir"), "schedule.xls");
	private static JFrame mainFrame;
	private static JSONObject settings;
	private static ScheduleView scheduleView;
	private static BirthdayView birthdayView;
	private static Thread scheduleUpdater;
	private static String scheduleFileName = "";

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
				settings = new JSONObject(fileContents);
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
	}

	private static void loadGUI() {
		mainFrame = new JFrame(programName);
		mainFrame.setUndecorated(true);
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		scheduleView = new ScheduleView();
		birthdayView = new BirthdayView();
		JScrollPane scheduleScroll = new JScrollPane(scheduleView);
		scheduleScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scheduleScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scheduleView.setOnUpdated(new Runnable() {

			@Override
			public void run() {
				new Thread(new Runnable() {

					@Override
					public void run() {
						Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
						while (true) {
							for (int a = 0; a < scheduleView.getHeight() - screen.height; a += 1) {
								// System.out.println(a);
								try {
									scheduleScroll.getVerticalScrollBar().setValue(a);
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								} catch (Exception e) {
								}
							}
							for (int a = scheduleView.getHeight() - screen.height - 1; a > 0; a -= 1) {
								// System.out.println(a);
								try {
									scheduleScroll.getVerticalScrollBar().setValue(a);
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								} catch (Exception e) {
								}
							}
						}
					}
				}).start();
			}

		});
		SwitcherView sv = new SwitcherView();
		sv.setRepeatType(SwitcherView.INFINITE);
		sv.addView(scheduleScroll, 40);
		sv.addView(birthdayView, 1);
		sv.start();
		mainFrame.setContentPane(sv);
		mainFrame.setVisible(true);
	}

	private static void loadUpdater() {
		scheduleUpdater = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					refreshSchedule();
					refreshOTA();
					for (int i = 0; i < 300; i++) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		scheduleUpdater.start();
	}
	
	private static void refreshOTA() {
		Checker.checkOTA(settings.optString("maintainer","http://p.nockio.com/handasaim/"), programVersion, new OTAListener() {
			
			@Override
			public void onOTACheck(boolean updateAvailable) {
				if(updateAvailable) {
//					tellUser("OTA Update Found...");
				}else {
//					tellUser("No OTA Updates");
				}
			}
		});
	}

	private static void refreshSchedule() {
		String link = null;
		try {
			Document docu = Jsoup.connect(settings.getString("search-url")).get();
			Elements doc = docu.select("a");
			for (int i = 0; i < doc.size(); i++) {
				if (doc.get(i).attr("href").endsWith(".xls") || doc.get(i).attr("href").endsWith(".xlsx")) {
					link = doc.get(i).attr("href");
					break;
				}
			}
		} catch (IOException e) {
		}
		if (link != null) {
			// System.out.println(link);
			if (!link.equals(scheduleFileName)) {
				scheduleFileName = link;
				try {
					URL website = new URL(link);
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos;
					if (link.endsWith(".xlsx")) {
						fos = new FileOutputStream(scheduleFileXLSX);
					} else {
						fos = new FileOutputStream(scheduleFileXLS);
					}
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
					if (link.endsWith(".xlsx")) {
						scheduleView.setFile(scheduleFileXLSX);
					} else {
						scheduleView.setFile(scheduleFileXLS);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
