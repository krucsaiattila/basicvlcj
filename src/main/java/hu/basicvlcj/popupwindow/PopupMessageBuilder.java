package hu.basicvlcj.popupwindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopupMessageBuilder {

    private int delay;
    private Point location;
    private String message;

    public PopupMessageBuilder at(Point p) {
        this.location = p;
        return this;
    }

    public PopupMessageBuilder withDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public PopupMessageBuilder withMessage(String msg) {
        this.message = msg;
        return this;
    }

    public PopupMessageBuilder show() {

        final JWindow frame = new JWindow();
        frame.setOpacity(0f);
        frame.setBackground(new Color(0, 0, 0, 0));
        BackgroundPane pane = new BackgroundPane();
        pane.setMessage(message);
        frame.add(pane);
        frame.pack();
        if (location == null) {
            frame.setLocationRelativeTo(null);
        } else {
            frame.setLocation(location);
        }
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);

        new FadeTimer(frame, 1000, 0f, 1f, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Timer timer = new Timer(delay, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new FadeTimer(frame, 1000, 1f, 0f, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                frame.dispose();
                            }
                        }).start();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }).start();

        return this;
    }
}