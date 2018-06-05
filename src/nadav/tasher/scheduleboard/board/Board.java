package nadav.tasher.scheduleboard.board;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import nadav.tasher.scheduleboard.board.views.BirthdayView;
import nadav.tasher.scheduleboard.board.views.ScheduleView;
import nadav.tasher.scheduleboard.board.views.SwitcherView;
import nadav.tasher.scheduleboard.board.views.TotalView;
import nadav.tasher.scheduleboard.ota.Checker;
import nadav.tasher.scheduleboard.ota.Checker.OTAListener;

public class Board {

	// Static Values
	static final String programName = "Handasaim Schedule Board";
	static final double programVersion = 0.9;
	static final String programReleaseDate = "~June 2018";

	// Private Values
	private static final File configuration = new File(System.getProperty("user.dir"), "configuration.json");
	private static final File scheduleFileXLSX = new File(System.getProperty("user.dir"), "schedule.xlsx");
	private static final File scheduleFileXLS = new File(System.getProperty("user.dir"), "schedule.xls");
	private static JFrame mainFrame;
	private static JSONObject settings;
	private static ScheduleView scheduleView;
	private static BirthdayView birthdayView;
	private static TotalView totalView;
	private static Thread scheduleUpdater;
	private static String scheduleFileName = "";

	public static void main(String[] args) {
		loadTheme();
		if (loadConfiguration()) {
			loadGUI();
			loadUpdater();
		} else {
			Utils.tellUser("Failed To Load Configuration.");
		}
	}

	private static void loadTheme() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
			Utils.tellUser("Failed To Set Desired Theme.");
		}
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
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		scheduleView = new ScheduleView(0.86);
		birthdayView = new BirthdayView(settings.optString("maintainer","http://p.nockio.com/handasaim/board/ota"));
		totalView=new TotalView(scheduleView);
		SwitcherView sv = new SwitcherView();
		sv.setRepeatType(SwitcherView.INFINITE);
		sv.addView(totalView, 110);
		sv.addView(birthdayView, 10);
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
					for (int i = 0; i < 120; i++) {
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
		Checker.checkOTA(settings.optString("maintainer","http://p.nockio.com/handasaim/board/ota"), programVersion, new OTAListener() {
			
			@Override
			public void onOTACheck(boolean updateAvailable) {
				System.out.println("Searched For Updates");
				if(updateAvailable) {
//					tellUser("OTA Update Found...");
					System.out.println("Update Found");
					Checker.downloadAndStartInstaller(settings.optString("maintainer","http://p.nockio.com/handasaim/board/ota"));
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
			// Add An !
			// !link.equals(scheduleFileName)
			if (true) {
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
