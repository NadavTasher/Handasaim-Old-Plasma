package nadav.tasher.scheduleboard.board.views;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class TotalView extends JPanel {
	private static final long serialVersionUID = 1L;
	private MessageView messageView;

	public TotalView(ScheduleView sv) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension(screen.width, screen.height));
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
		messageView = new MessageView(sv);
		add(messageView);
		add(sv);
//		System.out.println("Im Here");
	}
}
