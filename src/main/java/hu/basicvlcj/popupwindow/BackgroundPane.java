package hu.basicvlcj.popupwindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class BackgroundPane extends JPanel {

    private MessagePane messagePane;

    public BackgroundPane() {
        setBorder(new EmptyBorder(40, 40, 40, 40));
        messagePane = new MessagePane();
        setLayout(new BorderLayout());
        add(messagePane);
        setOpaque(false);
    }

    public void setMessage(String msg) {
        messagePane.setMessage(msg);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        LinearGradientPaint glp = new LinearGradientPaint(
                new Point(0, 0),
                new Point(0, getHeight()),
                new float[]{0f, 1f},
                new Color[]{Color.GRAY, Color.BLACK});
        RoundRectangle2D frame = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g2d.setPaint(glp);
        g2d.fill(frame);
        g2d.setColor(Color.WHITE);
        g2d.draw(frame);
    }

}