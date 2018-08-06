package nadav.tasher.handasaim.plasma.views;

import nadav.tasher.handasaim.plasma.Utils;
import nadav.tasher.handasaim.plasma.appcore.AppCore;
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
		JLabel label = Utils.getLabel("Waiting For Schedule");
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
		for (int hour = 0; hour < lastHour; hour++) {
			for (Classroom classroom : schedule.getClassrooms()) {

			}
		}
	}

	private void initSchedule() {
		removeAll();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel schedulePane = new JPanel();
		schedulePane.setBackground(Color.WHITE);
		sheet = AppCore.getSheet(schedule);
		ArrayList<Classroom> sc = AppCore.getClasses(sheet);
		if (!sc.isEmpty()) {
			JPanel labelHolder = new JPanel(new GridLayout(2, 1));
			JLabel day = Utils.getLabel("מערכת ליום " + AppCore.getDay(sheet));
			Utils.enlargeFont(day, 25);
			labelHolder.setPreferredSize(new Dimension(getWidth(), (int) (0.15 * screen.height)));
			labelHolder.setMinimumSize(labelHolder.getPreferredSize());
			labelHolder.setMaximumSize(labelHolder.getPreferredSize());
			labelHolder.add(day);
			labelHolder.setBackground(MessageView.topColor);
			add(labelHolder);
			JPanel titles = new JPanel();
			titles.setLayout(new GridLayout(1, sc.size() + 1));
			titles.setMinimumSize(titles.getPreferredSize());
			titles.setMaximumSize(titles.getPreferredSize());
			titles.setBackground(MessageView.topColor);
			for (int t = sc.size() - 1; t >= 0; t--) {
				JLabel l = Utils.getLabel(sc.get(t).name);
				titles.add(l);
			}
			JLabel classes = Utils.getLabel("כיתות");
			classes.setBackground(new Color(255, 255, 255));
			titles.add(classes);
			labelHolder.add(titles);
			add(labelHolder);
			schedulePane.setLayout(new GridLayout(1, sc.size() + 1));
			int longest = 0;
			for (int c = 0; c < sc.size(); c++) {
				int current = getLastHour(sc.get(c).subjects);
				if(longest<current) {
					longest=current;
				}
			}
			longest+=1;
			int movingPixels = (screen.height) / 6;
			schedulePane.setPreferredSize(new Dimension(schedulePane.getWidth(), movingPixels * longest));
			schedulePane.setMinimumSize(getPreferredSize());
			schedulePane.setMaximumSize(getPreferredSize());
			for (int c = sc.size() - 1; c >= 0; c--) {
				schedulePane.add(new ClassView(sc.get(c), longest, c));
			}
			schedulePane.add(new HourView(longest));
			// JScrollPane(schedulePane);
			 scheduleScroll = new JScrollPane(schedulePane);
			scheduleScroll.setBackground(MessageView.topColor);
			// scheduleScroll.setForeground(MessageView.topColor);
			// scheduleScroll.setOpaque(false);

			scheduleScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scheduleScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			add(scheduleScroll);
			startScrolling(movingPixels);
		}
		revalidate();
		repaint();
	}

	private void startScrolling(int movin) {
		if(scroll!=null) {
			scroll.interrupt();
		}
		scroll=null;
		scroll=new Thread(new Runnable() {

			@Override
			public void run() {
				if(scheduleScroll!=null) {
				while (true) {
					int previousPosition = -1;
					while (previousPosition != scheduleScroll.getVerticalScrollBar().getValue()) {

						try {
							try {
								Thread.sleep(8000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							previousPosition = scheduleScroll.getVerticalScrollBar().getValue();
							scheduleScroll.getVerticalScrollBar()
									.setValue(scheduleScroll.getVerticalScrollBar().getValue() + movin*5);
						} catch (Exception e) {
						}
					}
					scheduleScroll.getVerticalScrollBar().setValue(0);
				}
				}

			}
		});
		scroll.start();
	}

	public static class Layer extends JPanel {
		public Layer(int length) {
			setLayout(new GridLayout(1, length));
		}

		public void addSubject(Subject subject) {
			if (subject != null) {
				// TODO add 'subject'
			} else {
				add(new JPanel());
			}
		}
	}

	public static class ClassView extends JPanel {
//		private Color classColor;

		private static final long serialVersionUID = 1L;

		public ClassView(Classroom sc, int hours, int index) {
//			int colorRemover = (index + 1) % 16;
			// classColor = new Color(255 - colorRemover * 10, 255 - colorRemover * 10,
			// 255);
			setLayout(new GridLayout(hours, 1));
			setBackground(Color.WHITE);
			// setBackground(new Color(200,200,200));
			for (int i = 0; i < hours; i++) {
				String text = "", teacher = "";
				if (i < sc.subjects.size()) {
					text = sc.subjects.get(i).name;
					String[] teacherSplit = sc.subjects.get(i).fullName.split("\n");
					if (teacherSplit.length > 1) {
						teacher = teacherSplit[1];
					}
				}
				if (text.contains("מתמטיקה") && !text.contains("טכניונית") && text.contains("טכ")) {
					text = "מתמט' + טכ";
				} else if (text.contains("מתמטיקה") && text.contains("טכניונית")) {
					text = "טכניונית";
				}
				JLabel label = Utils.getClassLabel(text, teacher);
				Utils.enlargeFont(label, 19f);
				Border real = new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 4, 2, Color.BLACK),
						BorderFactory.createEmptyBorder(2, 2, 2, 2));
				// label.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.BLACK));
				label.setBorder(real);
				// label.setOpaque(true);
				// label.setBackground(Color.RED);
				label.setPreferredSize(new Dimension(getWidth(), label.getHeight()));
				label.setMinimumSize(label.getPreferredSize());
				label.setMaximumSize(label.getPreferredSize());
				add(label);
			}
		}
	}

	public static class HourView extends JPanel {

		private static final long serialVersionUID = 1L;

		public HourView(int hours) {
			setLayout(new GridLayout(hours, 1));
			setBackground(Color.WHITE);
			for (int i = 0; i < hours; i++) {
				JLabel lb = Utils.getLabel(String.valueOf(i));
				lb.setBorder(BorderFactory.createMatteBorder(0, 2, 4, 0, Color.BLACK));
				add(lb);
			}
		}
	}
}
