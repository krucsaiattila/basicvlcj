package hu.basicvlcj.videoplayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SubtitleFontSizeFrame extends JFrame implements ActionListener {

    private JButton okButton;
    private JTextField fontSizeSpinner;

    public SubtitleFontSizeFrame() {
        setTitle("Set subtitle font size");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 100);
        setAlwaysOnTop(true);
        setMinimumSize(new Dimension(300, 100));

        okButton = new JButton("OK");
        okButton.addActionListener(this);

        fontSizeSpinner = new JTextField("", 2);
        fontSizeSpinner.setText(String.valueOf(SubtitleOverlay.fontSize));

        JPanel panel = new JPanel(new FlowLayout());

        panel.add(fontSizeSpinner);
        panel.add(okButton);

        add(panel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            try {
                SubtitleOverlay.fontSize = Integer.parseInt(fontSizeSpinner.getText());
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Only numbers can be set!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
