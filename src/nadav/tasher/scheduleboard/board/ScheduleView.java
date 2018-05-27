package nadav.tasher.scheduleboard.board;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import nadav.tasher.scheduleboard.board.appcore.AppCore;
import nadav.tasher.scheduleboard.board.appcore.components.Classroom;

public class ScheduleView extends JPanel {

	private File schedule;
	private Runnable doOnUpdate = null;

	public ScheduleView() {
//		setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension(screen.width, screen.height));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
		setLayout(new GridLayout(1, 1));
		JLabel label = new JLabel("Waiting For Schedule");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setVerticalTextPosition(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		add(label);
	}

	public void setFile(File scheduleFile) {
		schedule = scheduleFile;
		initSchedule();
		if (doOnUpdate != null) {
			doOnUpdate.run();
		}
	}

	private void initSchedule() {
		removeAll();
		ArrayList<Classroom> sc = AppCore.getClasses(AppCore.getSheet(schedule));
		if (!sc.isEmpty()) {
			setLayout(new GridLayout(1, sc.size() + 1));
			int longest = 0;
			for (int c = 0; c < sc.size(); c++) {
				if (sc.get(c).subjects.size() > longest)
					longest = sc.get(c).subjects.size();
			}
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			setPreferredSize(new Dimension(screen.width, (int) (screen.height * ((double) longest / (double) 6))));
			setMinimumSize(getPreferredSize());
			setMaximumSize(getPreferredSize());
			for (int c = sc.size()-1; c>0; c--) {
				add(new ClassView(sc.get(c), longest, c));
			}
			add(new HourView(longest));
		}
		revalidate();
		repaint();
	}

	public void setOnUpdated(Runnable r) {
		doOnUpdate = r;
	}

	public static class ClassView extends JPanel {
		private Color classColor;

		public ClassView(Classroom sc, int hours, int index) {
			int colorRemover = (index+1) % 16;
			classColor = new Color(200 - colorRemover * 10, 200 - colorRemover * 10, 200);
			setLayout(new GridLayout(hours + 1, 1));
			setBackground(classColor);
			JLabel className = new JLabel(sc.name);
			className.setHorizontalAlignment(SwingConstants.CENTER);
			className.setVerticalAlignment(SwingConstants.CENTER);
			className.setForeground(Color.BLACK);
			className.setVerticalTextPosition(SwingConstants.CENTER);
			className.setHorizontalTextPosition(SwingConstants.CENTER);
			add(className);
			for (int i = 0; i < hours; i++) {
				String text = "";
				if (i < sc.subjects.size()) {
					text = sc.subjects.get(i).name;
				}
				JLabel label = new JLabel("<html>"+"\u200F"+text+"</html>");
				label.setForeground(Color.BLACK);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setVerticalAlignment(SwingConstants.CENTER);
				label.setPreferredSize(new Dimension(getWidth(), label.getHeight()));
				label.setMinimumSize(label.getPreferredSize());
				label.setMaximumSize(label.getPreferredSize());
				label.setVerticalTextPosition(SwingConstants.CENTER);
				label.setHorizontalTextPosition(SwingConstants.CENTER);
				add(label);
			}
		}
	}

	public static class HourView extends JPanel {
		public HourView(int hours) {
			setLayout(new GridLayout(hours + 1, 1));
			setBackground(new Color(200,200,200));
			JLabel classes = new JLabel("כיתות");
			classes.setHorizontalAlignment(SwingConstants.CENTER);
			classes.setForeground(Color.BLACK);
			classes.setVerticalAlignment(SwingConstants.CENTER);
			classes.setVerticalTextPosition(SwingConstants.CENTER);
			classes.setHorizontalTextPosition(SwingConstants.CENTER);
			add(classes);
			for (int i = 0; i < hours; i++) {
				JLabel label = new JLabel(String.valueOf(i));
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setVerticalAlignment(SwingConstants.CENTER);
				label.setForeground(Color.BLACK);
				label.setVerticalTextPosition(SwingConstants.CENTER);
				label.setHorizontalTextPosition(SwingConstants.CENTER);
				add(label);
			}
		}
	}
}
