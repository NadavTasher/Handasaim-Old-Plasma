package nadav.tasher.handasaim.plasma.views;

import nadav.tasher.handasaim.plasma.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class TextView extends JLabel {
    public static final float defaultSize = 22f;

    public TextView() {
        init();
    }

    public TextView(String text) {
        init();
        setText(text);
    }

    private void init() {
        InputStream is = Utils.class.getResourceAsStream("/internal/rubik.ttf");
        Font font;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(defaultSize);
        } catch (FontFormatException | IOException e) {
            font = Font.getFont(Font.SANS_SERIF, getFont()).deriveFont(defaultSize);
            e.printStackTrace();
        }
        setFont(font);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlignmentY(Component.CENTER_ALIGNMENT);
        setVerticalTextPosition(SwingConstants.CENTER);
        setHorizontalTextPosition(SwingConstants.CENTER);
    }

    @Override
    public void setText(String text) {
        super.setText("<html>\n" + "<p align=\"center\">" + text + "</p>" + "\n</html>");
    }

    public void setTextSize(float size) {
        setFont(getFont().deriveFont(size));
    }

    public void setTextColor(Color color) {
        setForeground(color);
    }
}
