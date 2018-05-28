package nadav.tasher.scheduleboard.board.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import nadav.tasher.scheduleboard.board.Egg;
import nadav.tasher.scheduleboard.board.Utils;
import nadav.tasher.scheduleboard.board.appcore.AppCore;

public class MessageView extends JPanel {
	public MessageView(ScheduleView sv) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension((int) (screen.width * (1 - sv.getPrecentage())), screen.height));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
		// System.out.println(getWidth()+"
		// "+(int)(screen.width*(1-sv.getPrecentage())));
		setBackground(Color.WHITE);
		JLabel timeAndDate = Utils.getLabel("Waiting For Date n' Time");
		timeAndDate.setPreferredSize(
				new Dimension((int) getPreferredSize().getWidth(), (int) (getPreferredSize().getHeight() * 0.2)));
		timeAndDate.setMinimumSize(timeAndDate.getPreferredSize());
		timeAndDate.setMaximumSize(timeAndDate.getPreferredSize());
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					Calendar calendar = Calendar.getInstance();
					String c = "\u200E";
					c += calendar.get(Calendar.HOUR_OF_DAY) + ":";
					if (calendar.get(Calendar.MINUTE) < 10) {
						c += "0" + calendar.get(Calendar.MINUTE);
					} else {
						c += calendar.get(Calendar.MINUTE);
					}
					c += " ,";
					c += (calendar.get(Calendar.DAY_OF_MONTH)) + "/" + (calendar.get(Calendar.MONTH) + 1);
					c += " ," + Utils.dayConvert(calendar.get(Calendar.DAY_OF_WEEK));
					timeAndDate.setText(c);
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		add(timeAndDate);
		// TODO add date and time
		// TODO add messages
		SwitcherView switcher=new SwitcherView();
		switcher.setBackground(Color.WHITE);
		
		switcher.setPreferredSize(
				new Dimension((int) getPreferredSize().getWidth(), (int) (getPreferredSize().getHeight() * 0.8)));
		switcher.setMinimumSize(switcher.getPreferredSize());
		switcher.setMaximumSize(switcher.getPreferredSize());
		
		sv.setOnUpdated(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				switcher.views.clear();
				ArrayList<String> mArray = AppCore.getMessages(sv.getSheet());
				for (int a = 0; a < mArray.size(); a++) {
					switcher.addView(Utils.getLabel(mArray.get(a)), 10);
				}
				switcher.start();
			}
		});
		add(switcher);
	}
}