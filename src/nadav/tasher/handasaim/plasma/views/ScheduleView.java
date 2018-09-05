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

public class ScheduleView extends JPanel {
    public static final Color background = new JPanel().getBackground();
    private static final long serialVersionUID = 1L;
    private Schedule schedule;
    private Timer scrollTimer = new Timer();
    private boolean scrolling = false;
    private ArrayList<Layer> scheduleLayers = new ArrayList<>();
    private TimerTask scroll = new TimerTask() {
        private int scrollIndex = 0;
        private int maxScrollIndex = 0;

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
            maxScrollIndex = scheduleLayers.size() - ((PlasmaView.scheduleViewHeight - Layer.DEFAULT_HEIGHT) / Layer.DEFAULT_HEIGHT);
            maxScrollIndex = (maxScrollIndex < 0) ? 0 : maxScrollIndex;
        }
    };

    public ScheduleView() {
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
        int layerLength = getLastHour() + 1;//+1 Cause Title
        int lastHour = getLastHour();
        Layer hours = new Layer(layerLength, Utils.y() / 12, background);
        hours.addText("שעה");
        for (int hour = 0; hour < lastHour; hour++) {
            String value = (hour != 0) ? String.valueOf(hour) : "טרום";
            hours.addText(value);
        }
        add(hours);
        for (int classroomIndex = 0; classroomIndex < schedule.getClassrooms().size(); classroomIndex++) {
            Classroom classroom = schedule.getClassrooms().get(classroomIndex);
            Layer currentClassroom = new Layer(layerLength, background);
            currentClassroom.addText(classroom.getName());
            if (classroom.getSubjects().size() >= lastHour) {
                for (int index = 0; index < lastHour; index++) {
                    currentClassroom.addSubject(classroom.getSubjects().get(index));
                }
            }
            scheduleLayers.add(currentClassroom);
            add(currentClassroom);
        }
        revalidate();
        repaint();
    }

    public static class Layer extends JPanel {
        public static final Color borderColor = Color.LIGHT_GRAY;
        public static final int DEFAULT_HEIGHT = Utils.y() / 7;
        private final Border border = new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, borderColor), BorderFactory.createEmptyBorder(2, 2, 2, 2));
        private Color background = ScheduleView.background;

        public Layer(int length, Color color) {
            init(length, DEFAULT_HEIGHT, color);
        }

        public Layer(int length, int height, Color color) {
            init(length, height, color);
        }

        private void init(int length, int height, Color background) {
            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            setLayout(new GridLayout(1, length));
            Dimension size = new Dimension(Utils.x(), height);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setBorder(border);
            this.background = background;
        }

        public void addText(String text) {
            JPanel currentPanel = new JPanel();
            currentPanel.setLayout(new GridLayout(1, 1));
            currentPanel.setBackground(this.background);
            currentPanel.add(new TextView(text));
            add(currentPanel);
        }

        public void addSubject(Subject subject) {
            if (subject.getDescription().isEmpty()) subject = null;
            TextView currentText = new TextView();
            currentText.setOpaque(true);
            currentText.setBackground(this.background);
            if (subject != null) {
                StringBuilder text = new StringBuilder();
                text.append("<center>");
                text.append("<i>");
//                text.append("<b>");
                text.append(Utils.shrinkSubjectName(subject.getName()));
//                text.append("</b>");
                text.append("</i>");
//                text.append("<br/>");
                text.append("<p style=\"font-size:13px;\">");
                for (int teacher = 0; teacher < subject.getTeachers().size(); teacher++) {
                    if (teacher > 0) text.append(",");
                    text.append(Utils.shrinkTeacherName(subject.getTeachers().get(teacher).getName().split("\\s")[0], subject.getTeachers().size()));
                }
                text.append("</p>");
                text.append("</center>");
                currentText.setText(text.toString());
            }
            add(currentText);
        }
    }
}
