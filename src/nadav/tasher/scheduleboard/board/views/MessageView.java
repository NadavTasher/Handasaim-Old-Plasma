package nadav.tasher.scheduleboard.board.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import nadav.tasher.scheduleboard.board.Utils;
import nadav.tasher.scheduleboard.board.appcore.AppCore;

public class MessageView extends JPanel {
	public static final Color topColor=new Color(180,200,230);
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
		Utils.enlargeFont(timeAndDate,28f);
		timeAndDate.setPreferredSize(
				new Dimension((int) getPreferredSize().getWidth(), (int) (getPreferredSize().getHeight() * 0.15)));
		timeAndDate.setMinimumSize(timeAndDate.getPreferredSize());
		timeAndDate.setMaximumSize(timeAndDate.getPreferredSize());
		timeAndDate.setOpaque(true);
		timeAndDate.setBackground(topColor);
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
		switcher.setBackground(topColor);
		switcher.setRepeatType(SwitcherView.INFINITE);
		switcher.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		switcher.setPreferredSize(
				new Dimension((int) getPreferredSize().getWidth(), (int) (getPreferredSize().getHeight() * 0.85)));
		switcher.setMinimumSize(switcher.getPreferredSize());
		switcher.setMaximumSize(switcher.getPreferredSize());
		
		sv.setOnUpdated(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				switcher.clearViews();
				
				ArrayList<String> mArray = AppCore.getMessages(sv.getSheet());
				if(mArray.size()==0)mArray.add("\u200Fאין הודעות...");
				for (int a = 0; a < mArray.size(); a++) {
					JLabel l=Utils.getLabelFormatted(mArray.get(a));
					switcher.addView(l, 10);
				}
				switcher.start();
			}
		});
		add(switcher);
	}
}
