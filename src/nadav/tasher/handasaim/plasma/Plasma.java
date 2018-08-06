package nadav.tasher.handasaim.plasma;

import nadav.tasher.handasaim.plasma.ota.Checker;
import nadav.tasher.handasaim.plasma.views.BirthdayView;
import nadav.tasher.handasaim.plasma.views.PlasmaView;
import nadav.tasher.handasaim.plasma.views.ScheduleView;
import nadav.tasher.handasaim.plasma.views.SwitcherView;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Timer;
import java.util.TimerTask;

public class Plasma {

    // Static Values
    static final String programName = "Handasaim Plasma";
    static final double programVersion = 1.3;
    static final String programReleaseDate = "~August 2018";

    // Private Values
    private static final File configuration = new File(System.getProperty("user.dir"), "configuration.json");
    private static final File scheduleFileXLSX = new File(System.getProperty("user.dir"), "schedule.xlsx");
    private static final File scheduleFileXLS = new File(System.getProperty("user.dir"), "schedule.xls");
    private static Timer timer = new Timer();
    private static JFrame mainFrame;
    private static JSONObject settings;
    private static ScheduleView scheduleView;
    private static BirthdayView birthdayView;
    private static PlasmaView plasmaView;
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
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        scheduleView = new ScheduleView(0.86);
        birthdayView = new BirthdayView(settings.optString("maintainer", "http://nockio.com/h/plasma/ota"));
        plasmaView = new PlasmaView(scheduleView);
        SwitcherView sv = new SwitcherView();
        sv.setRepeatType(SwitcherView.INFINITE);
        sv.addView(plasmaView, 110);
        sv.addView(birthdayView, 10);
        sv.start();
        mainFrame.setContentPane(sv);
        mainFrame.setVisible(true);
    }

    private static void loadUpdater() {
        timer.schedule(new TimerTask() {
            public void run() {
                refreshSchedule();
                refreshOTA();
            }
        }, 0, 120 * 1000);
    }

    private static void refreshOTA() {
        Checker.checkOTA(settings.optString("maintainer", "http://p.nockio.com/handasaim/board/ota"), programVersion, updateAvailable -> {
            System.out.println("Searched For Updates");
            if (updateAvailable) {
//					tellUser("OTA Update Found...");
                System.out.println("Update Found");
                Checker.downloadAndStartInstaller(settings.optString("maintainer", "http://p.nockio.com/handasaim/board/ota"));
            }
        });
    }

    private static void refreshSchedule() {
        String link = null;
        try {
            Document docu = Jsoup.connect(settings.getString("search-url")).get();
            Elements doc = docu.select("a");
            for (Element aDoc : doc) {
                if (aDoc.attr("href").endsWith(".xls") || aDoc.attr("href").endsWith(".xlsx")) {
                    link = aDoc.attr("href");
                    break;
                }
            }
        } catch (IOException e) {
        }
        if (link != null) {
            // System.out.println(link);
            // Add An !
            // !link.equals(scheduleFileName)
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
