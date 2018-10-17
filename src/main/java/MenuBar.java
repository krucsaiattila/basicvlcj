import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
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

    private JFrame mainFrame;
    private TestPlayer testPlayer;

    public MenuBar(JFrame mainFrame, TestPlayer testPlayer){
        this.mainFrame = mainFrame;
        this.testPlayer = testPlayer;

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
        playbackChapterMenu.setMnemonic('s');

        refreshSubtitleItems(TestPlayer.SUBTITLE_LIST);

        playbackMenu.add(subtitlesMenu);

        add(playbackMenu);

        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('h');

        helpAboutMenuItem = new JMenuItem("About...");
        helpAboutMenuItem.setMnemonic('a');
        helpMenu.add(helpAboutMenuItem);

        add(helpMenu);
    }

    public void refreshSubtitleItems(List<String> subtitles){
        subtitlesMenu.removeAll();
        for (String s:TestPlayer.SUBTITLE_LIST) {
            if(!TestPlayer.SUBTITLE_LIST.contains(s)){
                JMenuItem subMenuItem = new JMenuItem(s);
                subtitlesMenu.add(subMenuItem);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == mediaExitMenuItem){
            mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        } else if(e.getSource() == mediaPlayFileMenuItem){
            testPlayer.getControlsPanel().addMedia();
        }
    }
}


