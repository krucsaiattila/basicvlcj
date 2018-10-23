import org.omg.CORBA.INTERNAL;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class MenuBar extends JMenuBar implements ActionListener {

    private JMenu mediaMenu;
    private JMenuItem mediaPlayFileMenuItem;
    private JMenuItem mediaExitMenuItem;
    private JMenu playbackMenu;
    private JMenu playbackChapterMenu;
    private JMenu subtitlesMenu;
    private JMenu helpMenu;
    private JMenuItem helpAboutMenuItem;
    private JMenuItem noSubtitle;

    private JFrame mainFrame;
    private TestPlayer testPlayer;

    private List<JMenuItem> subtitleList;

    public MenuBar(JFrame mainFrame, TestPlayer testPlayer){
        this.mainFrame = mainFrame;
        this.testPlayer = testPlayer;

        noSubtitle = new JMenuItem("No subtitle");
        noSubtitle.addActionListener(this);

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

        playbackMenu = new JMenu("Playback");
        playbackMenu.setMnemonic('p');

        playbackChapterMenu = new JMenu("Chapter");
        playbackChapterMenu.setMnemonic('c');
        for(int i = 1; i <= 25; i ++ ) {
            JMenuItem chapterMenuItem = new JMenuItem("Chapter " + i);
            playbackChapterMenu.add(chapterMenuItem);
        }
        playbackMenu.add(playbackChapterMenu);

        subtitlesMenu = new JMenu("Subtitles");
        subtitlesMenu.add(noSubtitle);
        playbackChapterMenu.setMnemonic('s');

        playbackMenu.add(subtitlesMenu);

        add(playbackMenu);

        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('h');

        helpAboutMenuItem = new JMenuItem("About...");
        helpAboutMenuItem.setMnemonic('a');
        helpMenu.add(helpAboutMenuItem);

        add(helpMenu);
    }

    public void handleSubtitles(String subtitle){
        if(!TestPlayer.SUBTITLE_LIST.contains(subtitle)){
            JMenuItem subMenuItem = new JMenuItem(subtitle);
            subMenuItem.addActionListener(this);
            subtitlesMenu.add(subMenuItem);
            subtitleList.add(subMenuItem);
            TestPlayer.SUBTITLE_LIST.add(subtitle);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == mediaExitMenuItem){
            mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        } else if(e.getSource() == mediaPlayFileMenuItem){
            testPlayer.getControlsPanel().addMedia();
        } else if (e.getSource() == noSubtitle){
            //TODO
            // testPlayer.getMediaPlayer().setSubTitleFile("");
        } else {
            for (JMenuItem i:subtitleList) {
                if(e.getSource() == i){
                    //TODO
                    testPlayer.getMediaPlayer().setSpu(Integer.parseInt(i.getText()));

                    //testPlayer.getMediaPlayer().setSubTitleFile(i.getText());
                }
            }
        }
    }

    public void removeSubtitles() {
        subtitlesMenu.removeAll();
        subtitlesMenu.add(noSubtitle);
        subtitleList.clear();
        TestPlayer.SUBTITLE_LIST.clear();
    }
}


