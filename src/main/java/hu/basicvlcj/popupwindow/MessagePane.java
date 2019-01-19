package hu.basicvlcj.popupwindow;

import javax.swing.*;
import java.awt.*;

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
