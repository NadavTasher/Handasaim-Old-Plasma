package nadav.tasher.handasaim.plasma;

import nadav.tasher.handasaim.plasma.appcore.AppCore;
import nadav.tasher.handasaim.plasma.appcore.components.Schedule;
import nadav.tasher.handasaim.plasma.ota.Checker;
import nadav.tasher.handasaim.plasma.push.Push;
import nadav.tasher.handasaim.plasma.views.BarView;
import nadav.tasher.handasaim.plasma.views.PlasmaView;
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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Plasma {

    // Static Values
    static final String programName = "Handasaim Plasma";
    static final double programVersion = 2.0;
    static final String programReleaseDate = "~August 2018";

    // Private Values
    private static final File configuration = new File(System.getProperty("user.dir"), "configuration.json");
    private static final File scheduleFileXLSX = new File(System.getProperty("user.dir"), "schedule.xlsx");
    private static final File scheduleFileXLS = new File(System.getProperty("user.dir"), "schedule.xls");
    private static Timer timer = new Timer();
    private static JFrame mainFrame;
    private static JSONObject settings;
    private static PlasmaView plasmaView;
    private static String scheduleFileName = "";

    public static void main(String[] args) {
        loadTheme();
            loadGUI();
            loadUpdater();
    }

    private static void loadTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
            Utils.tellUser("Failed To Set Desired Theme.");
        }
    }

    private static void loadGUI() {
        mainFrame = new JFrame(programName);
        mainFrame.setUndecorated(true);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        plasmaView = new PlasmaView();
        mainFrame.setContentPane(plasmaView);
        mainFrame.setVisible(true);
    }

    private static void loadUpdater() {
        timer.schedule(new TimerTask() {
            public void run() {
                refreshSchedule();
                refreshPush();
                refreshOTA();
            }
        }, 0, 120 * 1000);
    }

    private static void refreshPush() {
        Push.getPushes(pushes -> {
            ArrayList<String> messages = new ArrayList<>();
            for (int i = 0; i < pushes.length(); i++) {
                JSONObject currentPush = pushes.getJSONObject(i);
                StringBuilder pushBuilder = new StringBuilder();
                pushBuilder.append(currentPush.getString("sender"));
                pushBuilder.append(' ').append('-').append(' ');
                pushBuilder.append(currentPush.getString("title"));
                pushBuilder.append(':').append(' ');
                pushBuilder.append(currentPush.getString("message"));
                messages.add(pushBuilder.toString());
            }
            plasmaView.getBarView().setPushMessages(Utils.toMessages(messages, BarView.Message.TYPE_PUSH));
        });
    }

    private static void refreshOTA() {
        Checker.checkOTA(programVersion, updateAvailable -> {
            if (updateAvailable) {
//					Utils.tellUser("Downloading New OTA Update");
                System.out.println("Update Found");
                Checker.downloadAndStartInstaller();
            }
        });
    }

    private static void refreshSchedule() {
        try {
            // TODO change to null
            String scheduleLink = "http://handasaim.co.il/wp-content/uploads/2017/06/15-5.xls";
            Document document = Jsoup.connect("http://handasaim.co.il/2017/06/13/%D7%9E%D7%A2%D7%A8%D7%9B%D7%AA-%D7%95%D7%A9%D7%99%D7%A0%D7%95%D7%99%D7%99%D7%9D/").get();
            Elements links = document.select("a");
            for (Element link : links) {
                if (link.attr("href").endsWith(".xls") || link.attr("href").endsWith(".xlsx")) {
                    scheduleLink = link.attr("href");
                    break;
                }
            }
            if (scheduleLink != null) {
                if (!scheduleLink.equals(scheduleFileName)) {
                    scheduleFileName = scheduleLink;
                    try {
                        URL website = new URL(scheduleLink);
                        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                        FileOutputStream fos;
                        if (scheduleLink.endsWith(".xlsx")) {
                            fos = new FileOutputStream(scheduleFileXLSX);
                        } else {
                            fos = new FileOutputStream(scheduleFileXLS);
                        }
                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                        fos.close();
                        Schedule schedule;
                        if (scheduleLink.endsWith(".xlsx")) {
                            schedule = AppCore.getSchedule(scheduleFileXLSX);
                        } else {
                            schedule = AppCore.getSchedule(scheduleFileXLS);
                        }
                        plasmaView.getScheduleView().setSchedule(schedule);
                        plasmaView.getBarView().setScheduleMessages(Utils.toMessages(schedule.getMessages(), BarView.Message.TYPE_SCHEDULE));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Schedule Refresh Failed!");
        }
    }
}
