package nadav.tasher.scheduleboard.board.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.apache.poi.ss.usermodel.Sheet;
import nadav.tasher.scheduleboard.board.Utils;
import nadav.tasher.scheduleboard.board.appcore.AppCore;
import nadav.tasher.scheduleboard.board.appcore.components.Classroom;
import nadav.tasher.scheduleboard.board.appcore.components.Classroom.Subject;

public class ScheduleView extends JPanel {

	private static final long serialVersionUID = 1L;
	private File schedule;
	private Sheet sheet;
	private Runnable doOnUpdate = null;
	private double screenPrecantage;
	private Thread scroll;
	private JScrollPane scheduleScroll;

	public double getPrecentage() {
		return screenPrecantage;
	}

	public ScheduleView(double screenPrecentage) {
		// setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		this.screenPrecantage = screenPrecentage;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension((int) (this.screenPrecantage * screen.width), screen.height));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
		setLayout(new GridLayout(1, 1));
		setBackground(Color.WHITE);
		JLabel label = Utils.getLabel("Waiting For Schedule");
		label.setOpaque(true);
		label.setBackground(Color.WHITE);
		add(label);
	}

	public void setFile(File scheduleFile) {
		schedule = scheduleFile;
		initSchedule();
		if (doOnUpdate != null) {
			doOnUpdate.run();
		}
	}

	public Sheet getSheet() {
		return sheet;
	}

	private void initSchedule() {
		removeAll();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension((int) (screenPrecantage * screen.width), screen.height));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
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
	
	public int getLastHour(ArrayList<Subject> subjects) {
		for (int hour = subjects.size() - 1; hour > 0; hour--) {
			if (!subjects.get(hour).fullName.isEmpty()) {
				return hour;
			}
		}
		return 0;
	}

	public void setOnUpdated(Runnable r) {
		doOnUpdate = r;
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
