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

public class ScheduleView extends JPanel {
    private static final long serialVersionUID = 1L;
    private Schedule schedule;
    private Timer scrollTimer = new Timer();
    private ArrayList<Layer> scheduleLayers = new ArrayList<Layer>();

    public ScheduleView() {
        setWaitingView();
    }

    private void setWaitingView() {
        removeAll();
        setLayout(new GridLayout(1, 1));
        TextView label = new TextView("Waiting For Schedule");
        label.setOpaque(true);
        add(label);
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
        setScheduleView();
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
        return lastHour;
    }

    private void setScheduleView() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        int lastHour = getLastHour();
        int layerLength = schedule.getClassrooms().size() + 1; // +1 Because Of Hour Number
        for (int hour = 0; hour < lastHour; hour++) {
            Layer currentLayer = new Layer(layerLength);
            currentLayer.addTime(hour);
            for (Classroom classroom : schedule.getClassrooms()) {
                currentLayer.addSubject(classroom.getSubjects().get(hour));
            }
            add(currentLayer);
        }
        revalidate();
        repaint();
    }

    public static class Layer extends JPanel {
        private Border border = new CompoundBorder(BorderFactory.createMatteBorder(2, 1, 2, 1, Color.BLACK), BorderFactory.createEmptyBorder(2, 2, 2, 2));

        public Layer(int length) {
            setLayout(new GridLayout(1, length));
        }

        public void addSubject(Subject subject) {
            if (subject.getDescription().isEmpty()) subject = null;
            JPanel currentPanel = new JPanel();
            currentPanel.setLayout(new GridLayout(1, 1));
            currentPanel.setBorder(border);
            if (subject != null) {
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

        public void addTime(int hour) {
            add(new TextView(String.valueOf(hour)));
        }
    }
}
