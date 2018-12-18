package hu.basicvlcj.videoplayer;

import uk.co.caprica.vlcj.player.TrackDescription;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
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
        playbackMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                removeSubtitles();
                testPlayer.getMenuBar().handleListOfSubtitles(testPlayer.getMediaPlayer().getSpuDescriptions());
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });

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

    public void handleSubtitles(String subtitleString){
        if(!TestPlayer.SUBTITLE_LIST.contains(subtitleString)){
            JMenuItem subMenuItem = new JMenuItem(subtitleString);
            subMenuItem.addActionListener(this);
            subtitlesMenu.add(subMenuItem);
            subtitleList.add(subMenuItem);
            Subtitle subtitle = new Subtitle(-2, subtitleString);
            TestPlayer.SUBTITLE_LIST.add(subtitle);
        }
    }

    public void  handleListOfSubtitles(List<TrackDescription> trackDescriptions){

        trackDescriptions.forEach(t -> {
            if(t.id() != -1) {
                JMenuItem subMenuItem = new JMenuItem(t.description());
                subMenuItem.addActionListener(this);
                subtitlesMenu.add(subMenuItem);
                subtitleList.add(subMenuItem);
                Subtitle subtitle = new Subtitle(t.id(), t.description());
                TestPlayer.SUBTITLE_LIST.add(subtitle);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == mediaExitMenuItem){
            mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        } else if(e.getSource() == mediaPlayFileMenuItem){
            testPlayer.getControlsPanel().addMedia();
        } else if (e.getSource() == noSubtitle){
            testPlayer.getMediaPlayer().setSpu(-1);
        } else {
            for (JMenuItem i:subtitleList) {
                if(e.getSource() == i){
                    TestPlayer.SUBTITLE_LIST.forEach(s -> {
                        if(i.getText() == s.getDescription()){
                            if(i.getText().equals(s.getDescription())){
                                //TODO
                            }
                        }
                    });
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


