package hu.basicvlcj.popupwindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class FadeTimer extends Timer implements ActionListener {

    private final float startAt;
    private final float endAt;
    private final int duration;

    private long startTime;

    private ActionListener endListener;

    private Window window;

    public FadeTimer(Window window, int duration, float startAt, float endAt, ActionListener endListener) {
        super(5, null);
        addActionListener(this);
        this.duration = duration;
        this.startAt = startAt;
        this.endAt = endAt;
        this.window = window;

        this.endListener = endListener;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
        super.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.currentTimeMillis();
        long lapsed = now - startTime;
        float opacity = startAt;
        if (lapsed >= duration) {
            opacity = endAt;
            ((Timer) e.getSource()).stop();
            if (endListener != null) {
                endListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "stopped"));
            }
        } else {
            float progress = (float) lapsed / (float) duration;
            float distance = endAt - startAt;
            opacity = (float) (distance * progress);
            opacity += startAt;
        }
        window.setOpacity(opacity);
    }

}