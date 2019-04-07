package hu.basicvlcj.videoplayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A class that manages the options for the subtitles. The font size, and the delay can be set here.
 */
public class SubtitleOptionsFrame extends JFrame implements ActionListener {

    private JButton okButton;
    private JButton cancelButton;
    private JTextField fontSizeTextField;
    private JTextField delayTextField;

    public SubtitleOptionsFrame() {
        setTitle("Set subtitle font size");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 200);
        setAlwaysOnTop(true);
        setMinimumSize(new Dimension(
                450, 200));

        okButton = new JButton("OK");
        okButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        fontSizeTextField = new JTextField("", 2);
        fontSizeTextField.setText(String.valueOf(SubtitleOverlay.fontSize));

        delayTextField = new JTextField("", 4);
        delayTextField.setText(String.valueOf(SubtitleOverlay.subtitleDelay));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel fontSizePanel = new JPanel(new FlowLayout());
        JPanel delayPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());

        fontSizePanel.add(new JLabel("Font size:"));
        fontSizePanel.add(fontSizeTextField);

        delayPanel.add(new JLabel("Subtitle delay in milliseconds:"));
        delayPanel.add(delayTextField);

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        panel.add(fontSizePanel);
        panel.add(delayPanel);
        panel.add(buttonPanel);

        add(panel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            try {
                if(Integer.parseInt(fontSizeTextField.getText()) <= 0){
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Only numbers that are greater than zero can be set!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    SubtitleOverlay.fontSize = Integer.parseInt(fontSizeTextField.getText());
                }
                SubtitleOverlay.subtitleDelay = Long.parseLong(delayTextField.getText());
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Only whole numbers can be set!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }
}
