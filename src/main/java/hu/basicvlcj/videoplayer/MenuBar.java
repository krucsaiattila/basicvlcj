package hu.basicvlcj.videoplayer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents the menubar on the top of the window.
 */
public class MenuBar extends JMenuBar implements ActionListener {

    private JMenu mediaMenu;
    private JMenuItem mediaPlayFileMenuItem;
    private JMenuItem mediaExitMenuItem;
    private JMenu quizMenu;
    private JMenuItem quizMenuItem;
    private JMenu helpMenu;
    private JMenuItem helpAboutMenuItem;

    private JFrame mainFrame;
    private TestPlayer testPlayer;

    private List<JMenuItem> subtitleList;

    public MenuBar(JFrame mainFrame, TestPlayer testPlayer){
        this.mainFrame = mainFrame;
        this.testPlayer = testPlayer;

        subtitleList = new ArrayList<>();

        mediaMenu = new JMenu("Media");
        mediaMenu.setMnemonic('m');

        mediaPlayFileMenuItem = new JMenuItem("Play File...");
        mediaPlayFileMenuItem.setMnemonic('f');
        mediaPlayFileMenuItem.addActionListener(this);
        mediaMenu.add(mediaPlayFileMenuItem);

        mediaMenu.add(new JSeparator());

        mediaExitMenuItem = new JMenuItem("Exit");
        mediaExitMenuItem.setMnemonic('x');
        mediaExitMenuItem.addActionListener(this);
        mediaMenu.add(mediaExitMenuItem);

        add(mediaMenu);

        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('h');

        helpAboutMenuItem = new JMenuItem("About...");
        helpAboutMenuItem.setMnemonic('a');
        helpMenu.add(helpAboutMenuItem);

        add(helpMenu);

        quizMenu = new JMenu("Quiz");
        quizMenuItem = new JMenuItem("Take quiz...");
        quizMenuItem.addActionListener(this);
        quizMenu.add(quizMenuItem);

        add(quizMenu);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == mediaExitMenuItem){
            mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        } else if(e.getSource() == mediaPlayFileMenuItem){
            testPlayer.getControlsPanel().addMedia();
        } else if(e.getSource() == quizMenuItem){
            //quizFrame.setPlayerControlsPanel(testPlayer.getControlsPanel());
            //quizFrame.display();
        }
    }
}


