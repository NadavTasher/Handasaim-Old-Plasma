package nadav.tasher.scheduleboard.board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import nadav.tasher.scheduleboard.board.appcore.AppCore;
import nadav.tasher.scheduleboard.board.appcore.components.Classroom;

public class ScheduleView extends JPanel {

	private File schedule;
	private Runnable doOnUpdate = null;
	private double screenPrecantage;

	public ScheduleView(int screenPrecentage) {
		// setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		this.screenPrecantage = (double) screenPrecantage / 100;
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

	private static JLabel getLabel(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(Color.BLACK);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setVerticalTextPosition(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		return label;
	}

	private static JLabel getLabelFormatted(String text) {
		return getLabel("<html>\u200F" + text + "</html>");
	}

	private void initSchedule() {
		removeAll();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		JPanel schedulePane = new JPanel();
		ArrayList<Classroom> sc = AppCore.getClasses(AppCore.getSheet(schedule));
		if (!sc.isEmpty()) {
			JPanel titles = new JPanel();
			titles.setLayout(new GridLayout(1, sc.size() + 1));
			titles.setPreferredSize(new Dimension(getWidth(), (int) (screen.height * 0.15)));
			titles.setBackground(
					new Color(255, 200 - new Random().nextInt(10) * 4, 200 - new Random().nextInt(10) * 10));
			for (int t = sc.size() - 1; t >= 0; t--) {
				JLabel l = getLabel(sc.get(t).name);
				titles.add(l);
			}
			JLabel classes = getLabel("כיתות");
			classes.setBackground(new Color(200, 200, 200));
			titles.add(classes);
			add(titles);
			schedulePane.setLayout(new GridLayout(1, sc.size() + 1));
			int longest = 0;
			for (int c = 0; c < sc.size(); c++) {
				if (sc.get(c).subjects.size() > longest)
					longest = sc.get(c).subjects.size();
			}
			schedulePane.setPreferredSize(
					new Dimension(getWidth(), (int) (screen.height * ((double) longest / (double) 8))));
			schedulePane.setMinimumSize(getPreferredSize());
			schedulePane.setMaximumSize(getPreferredSize());
			for (int c = sc.size() - 1; c >= 0; c--) {
				schedulePane.add(new ClassView(sc.get(c), longest, c));
			}
			schedulePane.add(new HourView(longest));
			JScrollPane scheduleScroll = new JScrollPane(schedulePane);
			scheduleScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scheduleScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			add(scheduleScroll);
			new Thread(new Runnable() {

				@Override
				public void run() {
					Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
					while (true) {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (int a = 0; a < schedulePane.getHeight() - screen.height; a += 1) {
							// System.out.println(a);
							try {
								scheduleScroll.getVerticalScrollBar().setValue(a);
								try {
									Thread.sleep(20);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							} catch (Exception e) {
							}
						}
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (int a = schedulePane.getHeight() - screen.height - 1; a > 0; a -= 1) {
							// System.out.println(a);
							try {
								scheduleScroll.getVerticalScrollBar().setValue(a);
								try {
									Thread.sleep(20);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							} catch (Exception e) {
							}
						}
					}
				}
			}).start();
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
			int colorRemover = (index + 1) % 16;
			classColor = new Color(200 - colorRemover * 10, 200 - colorRemover * 10, 200);
			setLayout(new GridLayout(hours + 1, 1));
			setBackground(classColor);
			for (int i = 0; i < hours; i++) {
				String text = "";
				if (i < sc.subjects.size()) {
					text = sc.subjects.get(i).name;
				}
				JLabel label = getLabelFormatted(text);
				label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
				label.setPreferredSize(new Dimension(getWidth(), label.getHeight()));
				label.setMinimumSize(label.getPreferredSize());
				label.setMaximumSize(label.getPreferredSize());
				add(label);
			}
		}
	}

	public static class HourView extends JPanel {
		public HourView(int hours) {
			setLayout(new GridLayout(hours + 1, 1));
			setBackground(new Color(200, 200, 200));
			for (int i = 0; i < hours; i++) {
				JLabel lb=getLabel(String.valueOf(i));
				lb.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
				add(lb);
			}
		}
	}
}
