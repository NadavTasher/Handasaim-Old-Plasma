package nadav.tasher.handasaim.plasma;

import nadav.tasher.handasaim.plasma.views.BarView;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

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
                return sb.toString();
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static ArrayList<BarView.Message> toMessages(Collection<String> collection, int type) {
        ArrayList<BarView.Message> messages = new ArrayList<>();
        for (String s : collection) {
            messages.add(new BarView.Message(s, type));
        }
        return messages;
    }

    public static String shrinkSubjectName(String name) {
        String result = name;
        if (result.contains("מתמטיקה") && !result.contains("טכניונית") && result.contains("טכ")) {
            result = "מתמט' + טכ";
        } else if (result.contains("מתמטיקה") && result.contains("טכניונית")) {
            result = "טכניונית";
        }
        if (result.contains("אזרחות")) {
            result = "אזרחות";
        }
        if (result.contains("פסיכולוגיה")) {
            result = "פסיכו'";
        }
        if (result.contains("רובוטיקה")) {
            result = "שרטוט";
        }
        return result;
    }

    public static String shrinkTeacherName(String name, int teacherAmount) {
        String result = name;
        result = result.split("\\s")[0];
        if (teacherAmount > 2) {
            if (result.length() > 2) {
                result = result.substring(0, 2);
            }
        } else {
            if (result.length() > 5) {
                result = result.substring(0, 5);
            }
        }
        return result;
    }

    public static String dayConvert(int dayOfWeek) {
        switch (dayOfWeek) {
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
        Thread timer = new Thread(() -> {
            final int seconds = 10;
            for (int second = seconds; second >= 0; second--) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            popup.setVisible(false);
        });
        timer.start();
    }

    public static int x() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        return screen.width;
    }

    public static int y() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        return screen.height;
    }
}
