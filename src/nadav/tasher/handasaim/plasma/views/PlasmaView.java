package nadav.tasher.handasaim.plasma.views;

import javax.swing.*;
import java.awt.*;

import static nadav.tasher.handasaim.plasma.Utils.x;
import static nadav.tasher.handasaim.plasma.Utils.y;

public class PlasmaView extends JPanel {
    private static final long serialVersionUID = 1L;
    public static final int scheduleViewHeight = (int) (y() * 0.85);
    public static final int barViewHeight = (int) (y() * 0.15);
    private BarView barView;
    private ScheduleView scheduleView;

    public PlasmaView() {
        // Initialize PlasmaView - Create The Layout.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(x(), y()));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
        barView = new BarView();
        scheduleView = new ScheduleView();
        // Layout Spacing And Size
        barView.setPreferredSize(new Dimension(x(), barViewHeight));
        barView.setMinimumSize(barView.getPreferredSize());
        barView.setMaximumSize(barView.getPreferredSize());
        scheduleView.setPreferredSize(new Dimension(x(), scheduleViewHeight));
        scheduleView.setMinimumSize(scheduleView.getPreferredSize());
        scheduleView.setMaximumSize(scheduleView.getPreferredSize());
        add(barView);
        add(scheduleView);
    }

    public BarView getBarView() {
        return barView;
    }

    public ScheduleView getScheduleView() {
        return scheduleView;
    }
}
