package nadav.tasher.handasaim.plasma.views;

import javax.swing.*;
import java.awt.*;

import static nadav.tasher.handasaim.plasma.Utils.x;
import static nadav.tasher.handasaim.plasma.Utils.y;

public class PlasmaView extends JPanel {
    private static final long serialVersionUID = 1L;
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
        add(barView);
        add(scheduleView);
    }
}
