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
import java.util.regex.Pattern;

public class Plasma {
    // Static Values
    static final String programName = "Handasaim Plasma";
    static final double programVersion = 2.2;
    static final String programReleaseDate = "~September 2018";
    static final String schedulePage = "http://handasaim.co.il/2018/08/31/%D7%9E%D7%A2%D7%A8%D7%9B%D7%AA-%D7%95%D7%A9%D7%99%D7%A0%D7%95%D7%99%D7%99%D7%9D-2/";
    static final String homePage = "http://handasaim.co.il/";
    // Private Values
    private static final File scheduleFileXLSX = new File(System.getProperty("user.dir"), "schedule.xlsx");
    private static final File scheduleFileXLS = new File(System.getProperty("user.dir"), "schedule.xls");
    private static Timer timer = new Timer();
    private static JFrame mainFrame;
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
        mainFrame = new JFrame(programName + " " + programVersion);
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

    private static String getScheduleLink() {
        String file = null;
        //        file = "http://nockio.com/h/schedulearchives/15-5.xls";
        try {
            // Main Search At Schedule Page
            Document document = Jsoup.connect(schedulePage).get();
            Elements elements = document.select("a");
            for (int i = 0; (i < elements.size() && file == null); i++) {
                String attribute = elements.get(i).attr("href");
                if (attribute.endsWith(".xls") || attribute.endsWith(".xlsx")) {
                    file = attribute;
                }
            }
            // Fallback Search At Home Page
            if (file == null) {
                Document documentFallback = Jsoup.connect(homePage).get();
                Elements elementsFallback = documentFallback.select("a");
                for (int i = 0; (i < elementsFallback.size() && file == null); i++) {
                    String attribute = elementsFallback.get(i).attr("href");
                    //                    Log.i("LinkFallback",attribute);
                    if ((attribute.endsWith(".xls") || attribute.endsWith(".xlsx")) && Pattern.compile("(/.[^a-z]+\\..+)$").matcher(attribute).find()) {
                        file = attribute;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static void refreshSchedule() {
        String link = getScheduleLink();
        if (link != null) {
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
                    Schedule schedule;
                    if (link.endsWith(".xlsx")) {
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
    }
}
