package nadav.tasher.scheduleboard.board;

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ScheduleView extends JPanel{
	
	private File schedule;
	
	public ScheduleView() {
		setLayout(new GridLayout(1,1));
		JLabel label = new JLabel("Waiting For Schedule");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setVerticalTextPosition(SwingConstants.CENTER);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
	}
	
	public void setFile(File scheduleFile) {
		schedule=scheduleFile;
	}
	
	public static class ClassView{
		
	}
	
}
