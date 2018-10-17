/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009-2018 Caprica Software Limited.
 */


import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.LibVlcFactory;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.*;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class TestPlayer extends VlcjTest implements MouseListener {

    /**
     * Log.
     */
    private static final Logger logger = LoggerFactory.getLogger(TestPlayer.class);

    private final JFrame mainFrame;
    private final Canvas videoSurface;
    private final JPanel controlsPanel;
    private JMenuBar menuBar;

    private MediaPlayerFactory mediaPlayerFactory;

    private EmbeddedMediaPlayer mediaPlayer;

    public static void main(final String[] args) throws Exception {
        LibVlc libVlc = LibVlcFactory.factory().create();

        logger.info("  version: {}", libVlc.libvlc_get_version());
        logger.info(" compiler: {}", libVlc.libvlc_get_compiler());
        logger.info("changeset: {}", libVlc.libvlc_get_changeset());

        setLookAndFeel();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TestPlayer(args);
            }
        });
    }

    public TestPlayer(String[] args) {
        videoSurface = new Canvas();

        videoSurface.setBackground(Color.black);
        videoSurface.setSize(800, 600); // Only for initial layout
        videoSurface.addMouseListener(this);

        // Since we're mixing lightweight Swing components and heavyweight AWT
        // components this is probably a good idea
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        TestPlayerMouseListener mouseListener = new TestPlayerMouseListener();
        videoSurface.addMouseListener(mouseListener);
        videoSurface.addMouseMotionListener(mouseListener);
        videoSurface.addMouseWheelListener(mouseListener);
        videoSurface.addKeyListener(new TestPlayerKeyListener());

        List<String> vlcArgs = new ArrayList<String>();

        vlcArgs.add("--no-snapshot-preview");
        vlcArgs.add("--quiet");
        vlcArgs.add("--quiet-synchro");
        vlcArgs.add("--intf");
        vlcArgs.add("dummy");

        logger.debug("vlcArgs={}", vlcArgs);

        mainFrame = new JFrame("VLCJ Test Player");
//        mainFrame.setIconImage(new ImageIcon(getClass().getResource("/icons/vlcj-logo.png")).getImage());

        FullScreenStrategy fullScreenStrategy = new Win32FullScreenStrategy(mainFrame);

        mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
        mediaPlayerFactory.setUserAgent("vlcj test player");

        List<AudioOutput> audioOutputs = mediaPlayerFactory.getAudioOutputs();
        logger.debug("audioOutputs={}", audioOutputs);

        mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer(fullScreenStrategy);
        mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoSurface));
        mediaPlayer.setPlaySubItems(true);
        // BEÉGETETT FILE
        String file = "starwars.mkv";
        mediaPlayer.prepareMedia(file);

        mediaPlayer.setEnableKeyInputHandling(false);
        mediaPlayer.setEnableMouseInputHandling(false);

        menuBar = buildMenuBar();

        controlsPanel = new PlayerControlsPanel(mediaPlayer, menuBar, mainFrame);

        mainFrame.setLayout(new BorderLayout());
        mainFrame.setBackground(Color.black);
        mainFrame.add(videoSurface, BorderLayout.CENTER);
        mainFrame.add(controlsPanel, BorderLayout.SOUTH);
        mainFrame.setJMenuBar(menuBar);
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                logger.debug("windowClosing(evt={})", evt);

                if(mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                if(mediaPlayerFactory != null) {
                    mediaPlayerFactory.release();
                    mediaPlayerFactory = null;
                }
            }
        });

        // Global AWT key handler, you're better off using Swing's InputMap and
        // ActionMap with a JFrame - that would solve all sorts of focus issues too
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                if(event instanceof KeyEvent) {
                    KeyEvent keyEvent = (KeyEvent)event;
                    if(keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                        if(keyEvent.getKeyCode()==KeyEvent.VK_SPACE){
                            if(mediaPlayer.isPlaying()){
                                mediaPlayer.setPause(true);
                            } else {
                                mediaPlayer.setPause(false);
                            }
                        } else if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE){
                            if(mediaPlayer.isFullScreen()){
                                controlsPanel.setVisible(!controlsPanel.isVisible());
                                mediaPlayer.toggleFullScreen();
                                menuBar.setVisible(!menuBar.isVisible());
                                mainFrame.invalidate();
                                mainFrame.validate();
                            }
                        }
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);

        mainFrame.setVisible(true);

        mediaPlayer.addMediaPlayerEventListener(new TestPlayerMediaPlayerEventListener());

        // Won't work with OpenJDK or JDK1.7, requires a Sun/Oracle JVM (currently)
        boolean transparentWindowsSupport = true;
        try {
            Class.forName("com.sun.awt.AWTUtilities");
        }
        catch(Exception e) {
            transparentWindowsSupport = false;
        }

        logger.debug("transparentWindowsSupport={}", transparentWindowsSupport);

        if(transparentWindowsSupport) {
            final Window test = new Window(null, WindowUtils.getAlphaCompatibleGraphicsConfiguration()) {
                private static final long serialVersionUID = 1L;

                @Override
                public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g;

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                    g.setColor(Color.white);
                    g.fillRoundRect(100, 150, 100, 100, 32, 32);

                    g.setFont(new Font("Sans", Font.BOLD, 32));
                    g.drawString("Heavyweight overlay test", 100, 300);
                }
            };

            AWTUtilities.setWindowOpaque(test, false); // Doesn't work in full-screen exclusive
            // mode, you would have to use 'simulated'
            // full-screen - requires Sun/Oracle JDK
            test.setBackground(new Color(0, 0, 0, 0)); // This is what you do in JDK7

            // mediaPlayer.setOverlay(test);
            // mediaPlayer.enableOverlay(true);
        }
    }

    private JMenuBar buildMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu mediaMenu = new JMenu("Media");
        mediaMenu.setMnemonic('m');

        JMenuItem mediaPlayFileMenuItem = new JMenuItem("Play File...");
        mediaPlayFileMenuItem.setMnemonic('f');
        mediaMenu.add(mediaPlayFileMenuItem);

        mediaMenu.add(new JSeparator());

        JMenuItem mediaExitMenuItem = new JMenuItem("Exit");
        mediaExitMenuItem.setMnemonic('x');
        mediaMenu.add(mediaExitMenuItem);

        menuBar.add(mediaMenu);

        JMenu playbackMenu = new JMenu("Playback");
        playbackMenu.setMnemonic('p');

        JMenu playbackChapterMenu = new JMenu("Chapter");
        playbackChapterMenu.setMnemonic('c');
        for(int i = 1; i <= 25; i ++ ) {
            JMenuItem chapterMenuItem = new JMenuItem("Chapter " + i);
            playbackChapterMenu.add(chapterMenuItem);
        }
        playbackMenu.add(playbackChapterMenu);

        JMenu subtitlesMenu = new JMenu("Subtitles");
        playbackChapterMenu.setMnemonic('s');

        for (String s:PlayerControlsPanel.SUBTITLE_LIST) {
            JMenuItem subMenuItem = new JMenuItem(s);
            subtitlesMenu.add(subMenuItem);
        }

        String[] subs = {""};
        for(int i = 0; i < subs.length; i ++ ) {
            JMenuItem subtitlesMenuItem = new JMenuItem(subs[i]);
            subtitlesMenu.add(subtitlesMenuItem);
        }
        playbackMenu.add(subtitlesMenu);

        menuBar.add(playbackMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('h');

        JMenuItem helpAboutMenuItem = new JMenuItem("About...");
        helpAboutMenuItem.setMnemonic('a');
        helpMenu.add(helpAboutMenuItem);

        menuBar.add(helpMenu);

        return menuBar;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && !e.isConsumed()) {
            e.consume();
            controlsPanel.setVisible(!controlsPanel.isVisible());
            mediaPlayer.toggleFullScreen();
            menuBar.setVisible(!menuBar.isVisible());
            mainFrame.invalidate();
            mainFrame.validate();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private final class TestPlayerMediaPlayerEventListener extends MediaPlayerEventAdapter {
        @Override
        public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
            logger.debug("mediaChanged(mediaPlayer={},media={},mrl={})", mediaPlayer, media, mrl);
        }

        @Override
        public void finished(MediaPlayer mediaPlayer) {
            logger.debug("finished(mediaPlayer={})", mediaPlayer);
        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {
            logger.debug("paused(mediaPlayer={})", mediaPlayer);
        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            logger.debug("playing(mediaPlayer={})", mediaPlayer);
            MediaDetails mediaDetails = mediaPlayer.getMediaDetails();
            logger.info("mediaDetails={}", mediaDetails);
        }

        @Override
        public void stopped(MediaPlayer mediaPlayer) {
            logger.debug("stopped(mediaPlayer={})", mediaPlayer);
        }

        @Override
        public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
            logger.debug("videoOutput(mediaPlayer={},newCount={})", mediaPlayer, newCount);
            if(newCount == 0) {
                return;
            }

            MediaDetails mediaDetails = mediaPlayer.getMediaDetails();
            logger.info("mediaDetails={}", mediaDetails);

            MediaMeta mediaMeta = mediaPlayer.getMediaMeta();
            logger.info("mediaMeta={}", mediaMeta);

            final Dimension dimension = mediaPlayer.getVideoDimension();
            logger.debug("dimension={}", dimension);
            if(dimension != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        videoSurface.setSize(dimension);
                        mainFrame.pack();
                    }
                });
            }

            // Demo the marquee
            //mediaPlayer.setMarqueeText("vlcj java bindings for vlc");
            mediaPlayer.setMarqueeSize(40);
            mediaPlayer.setMarqueeOpacity(95);
            mediaPlayer.setMarqueeColour(Color.white);
            mediaPlayer.setMarqueeTimeout(5000);
            mediaPlayer.setMarqueeLocation(50, 120);
            mediaPlayer.enableMarquee(true);
        }

        @Override
        public void error(MediaPlayer mediaPlayer) {
            logger.debug("error(mediaPlayer={})", mediaPlayer);
        }

        @Override
        public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
            logger.debug("mediaSubItemAdded(mediaPlayer={},subItem={})", mediaPlayer, subItem);
        }

        @Override
        public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
            logger.debug("mediaDurationChanged(mediaPlayer={},newDuration={})", mediaPlayer, newDuration);
        }

        @Override
        public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
            logger.debug("mediaParsedChanged(mediaPlayer={},newStatus={})", mediaPlayer, newStatus);
        }

        @Override
        public void mediaFreed(MediaPlayer mediaPlayer) {
            logger.debug("mediaFreed(mediaPlayer={})", mediaPlayer);
        }

        @Override
        public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
            logger.debug("mediaStateChanged(mediaPlayer={},newState={})", mediaPlayer, newState);
        }

        @Override
        public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
            logger.debug("mediaMetaChanged(mediaPlayer={},metaType={})", mediaPlayer, metaType);
        }
    }

    /**
     *
     *
     * @param enable
     */
    @SuppressWarnings("unused")
    private void enableMousePointer(boolean enable) {
        logger.debug("enableMousePointer(enable={})", enable);
        if(enable) {
            videoSurface.setCursor(null);
        }
        else {
            Image blankImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            videoSurface.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(blankImage, new Point(0, 0), ""));
        }
    }

    /**
     *
     */
    private final class TestPlayerMouseListener extends MouseAdapter {
        public void mouseMoved(MouseEvent e) {
            logger.trace("mouseMoved(e={})", e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            logger.debug("mousePressed(e={})", e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            logger.debug("mouseReleased(e={})", e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            logger.debug("mouseClicked(e={})", e);
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            logger.debug("mouseWheelMoved(e={})", e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            logger.debug("mouseEntered(e={})", e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            logger.debug("mouseExited(e={})", e);
        }
    }

    /**
     *
     */
    private final class TestPlayerKeyListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            logger.debug("keyReleased(e={})", e);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            logger.debug("keyTyped(e={})", e);
        }
    }
}