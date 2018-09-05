package nadav.tasher.handasaim.plasma.views;

import nadav.tasher.handasaim.plasma.Utils;
import nadav.tasher.handasaim.plasma.appcore.components.Classroom;
import nadav.tasher.handasaim.plasma.appcore.components.Schedule;
import nadav.tasher.handasaim.plasma.appcore.components.Subject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduleViewOld extends JPanel {
    public static final Color background = new JPanel().getBackground();
    private static final long serialVersionUID = 1L;
    private Schedule schedule;
    private Timer scrollTimer = new Timer();
    private boolean scrolling = false;
    private ArrayList<Layer> scheduleLayers = new ArrayList<>();
    private TimerTask scroll = new TimerTask() {
        private int scrollIndex = 0;
        private int maxScrollIndex = scheduleLayers.size() - ((PlasmaView.scheduleViewHeight - new Layer(1).getPreferredSize().height) / new Layer(1).getPreferredSize().height);

        @Override
        public void run() {
            update();
            if (scrolling) {
                if (scrollIndex >= maxScrollIndex) {
                    for (Layer l : scheduleLayers) {
                        if (!l.isVisible())
                            l.setVisible(true);
                    }
                } else {
                    scheduleLayers.get(scrollIndex).setVisible(false);
                }
                if (scrollIndex < maxScrollIndex) {
                    scrollIndex++;
                } else {
                    scrollIndex = 0;
                }
                revalidate();
                repaint();
            }
        }

        public void update() {
//            scrollIndex = scheduleLayers.size();
            scrollIndex = (scrollIndex > maxScrollIndex) ? 0 : scrollIndex;
            maxScrollIndex = scheduleLayers.size() - ((PlasmaView.scheduleViewHeight - new Layer(1).getPreferredSize().height) / new Layer(1).getPreferredSize().height);
            maxScrollIndex = (maxScrollIndex < 0) ? 0 : maxScrollIndex;
        }
    };

    public ScheduleViewOld() {
        setBackground(background);
        setWaitingView();
        setupTimer();
    }

    private void setWaitingView() {
        disableScrolling();
        removeAll();
        setLayout(new GridLayout(1, 1));
        TextView label = new TextView("Waiting For Schedule");
        label.setOpaque(true);
        add(label);
    }

    public void enableScrolling() {
        scrolling = true;
    }

    public void disableScrolling() {
        scrolling = false;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
        setScheduleView();
        enableScrolling();
    }

    public void setupTimer() {
        scrollTimer.schedule(scroll, 0, 3000);
    }

    private int getLastHour() {
        int lastHour = 0;
        for (Classroom classroom : schedule.getClassrooms()) {
            int classroomLastHour = 0;
            for (int subjectIndex = classroom.getSubjects().size() - 1; subjectIndex >= 0; subjectIndex--) {
                if (!classroom.getSubjects().get(subjectIndex).getDescription().isEmpty()) {
                    classroomLastHour = subjectIndex;
                    break;
                }
            }
            if (classroomLastHour > lastHour) lastHour = classroomLastHour;
        }
        return lastHour + 1;
    }

    private void setScheduleView() {
        removeAll();
        scheduleLayers.clear();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        int lastHour = getLastHour();
        int layerLength = schedule.getClassrooms().size() + 1; // +1 Because Of Hour Number
//        setLayout(new GridLayout(lastHour+1,1));
        Layer classNames = new Layer(layerLength, Utils.y() / 12);
        classNames.addText("כיתות");
        for (Classroom classroom : schedule.getClassrooms()) {
            classNames.addText(classroom.getName());
        }
        add(classNames);
        for (int hour = 0; hour < lastHour; hour++) {
            Layer currentLayer = new Layer(layerLength);
            String value = (hour != 0) ? String.valueOf(hour) : "טרום";
            currentLayer.addText(value);
            for (Classroom classroom : schedule.getClassrooms()) {
                currentLayer.addSubject(classroom.getSubjects().get(hour));
            }
            add(currentLayer);
            scheduleLayers.add(currentLayer);
        }
        revalidate();
        repaint();
    }

    public static class Layer extends JPanel {
        public static final Color borderColor = Color.LIGHT_GRAY;
        private final Border border = new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, borderColor), BorderFactory.createEmptyBorder(2, 2, 2, 2));

        public Layer(int length) {
            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            setLayout(new GridLayout(1, length));
            Dimension size = new Dimension(Utils.x(), Utils.y() / 7);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
        }

        public Layer(int length, int height) {
            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            setLayout(new GridLayout(1, length));
            Dimension size = new Dimension(Utils.x(), height);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
        }

        public void addText(String text) {
            JPanel currentPanel = new JPanel();
            currentPanel.setLayout(new GridLayout(1, 1));
            currentPanel.setBorder(border);
            currentPanel.add(new TextView(text));
            add(currentPanel);
        }

        public void addSubject(Subject subject) {
            if (subject.getDescription().isEmpty()) subject = null;
            JPanel currentPanel = new JPanel();
            currentPanel.setLayout(new GridLayout(1, 1));
            currentPanel.setBorder(border);
            currentPanel.setBackground(background);
            if (subject != null) {
                currentPanel.setBackground(new JPanel().getBackground());
                StringBuilder text = new StringBuilder();
                text.append("<b>");
                text.append(Utils.shrinkSubjectName(subject.getName()));
                text.append("</b>");
                text.append("<br/>");
                for (int teacher = 0; teacher < subject.getTeachers().size(); teacher++) {
                    if (teacher > 0) text.append(", ");
                    text.append(Utils.shrinkTeacherName(subject.getTeachers().get(teacher).getName().split("\\s")[0], subject.getTeachers().size()));
                }
                currentPanel.add(new TextView(text.toString()));
            }
            add(currentPanel);
        }
    }
}
