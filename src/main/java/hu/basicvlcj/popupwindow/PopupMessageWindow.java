package hu.basicvlcj.popupwindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class PopupMessageWindow {

    public static void main(String[] args) {
        new PopupMessageWindow();
    }

    public PopupMessageWindow() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occured", "Error", JOptionPane.ERROR_MESSAGE);
                }

                final JWindow frame = new JWindow();
                frame.setBackground(new Color(0, 0, 0, 0));
                BackgroundPane pane = new BackgroundPane();
                pane.setMessage("Boo, This is a popup...");
                frame.add(pane);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setAlwaysOnTop(true);

                Timer timer = new Timer(10000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        System.exit(0);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }

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

    public class MessagePane extends JPanel {

        private JLabel label;

        public MessagePane() {
            setOpaque(false);
            label = new JLabel();
            label.setForeground(Color.WHITE);
            setLayout(new GridBagLayout());
            add(label);
        }

        public void setMessage(String msg) {
            label.setText(msg);
        }

    }

}