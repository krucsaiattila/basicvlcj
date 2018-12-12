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

public class TestPlayer extends VlcjTest implements MouseMotionListener, MouseListener {

    private final JFrame mainFrame;

    private final VideoSurface videoSurface;
    private final PlayerControlsPanel controlsPanel;
    private MenuBar menuBar;

    private MediaPlayerFactory mediaPlayerFactory;

    private EmbeddedMediaPlayer mediaPlayer;

    public static List<Subtitle> SUBTITLE_LIST;

    private boolean controlPanelVisible;

    public static void main(final String[] args) throws Exception {
        LibVlc libVlc = LibVlcFactory.factory().create();

        setLookAndFeel();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TestPlayer(args);
            }
        });
    }

    public TestPlayer(String[] args) {
        SUBTITLE_LIST = new ArrayList<>();

        controlPanelVisible = true;

        videoSurface = new VideoSurface();

        videoSurface.setBackground(Color.black);
        videoSurface.setSize(800, 600); // Only for initial layout
        videoSurface.addMouseListener(this);
        videoSurface.addMouseMotionListener(this);

        // Since we're mixing lightweight Swing components and heavyweight AWT
        // components this is probably a good idea
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        List<String> vlcArgs = new ArrayList<String>();

        vlcArgs.add("--no-snapshot-preview");
        vlcArgs.add("--quiet");
        vlcArgs.add("--quiet-synchro");
        vlcArgs.add("--intf");
        vlcArgs.add("dummy");

        mainFrame = new JFrame("VLCJ Test Player");
//        mainFrame.setIconImage(new ImageIcon(getClass().getResource("/icons/vlcj-logo.png")).getImage());

        FullScreenStrategy fullScreenStrategy = new Win32FullScreenStrategy(mainFrame);

        mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
        mediaPlayerFactory.setUserAgent("vlcj test player");

        List<AudioOutput> audioOutputs = mediaPlayerFactory.getAudioOutputs();

        mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer(fullScreenStrategy);
        mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoSurface));
        mediaPlayer.setPlaySubItems(true);
        mediaPlayer.setEnableKeyInputHandling(false);
        mediaPlayer.setEnableMouseInputHandling(false);

        menuBar = new MenuBar(mainFrame, this);

        controlsPanel = new PlayerControlsPanel(mediaPlayer, this);

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

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && !e.isConsumed()) {
            e.consume();
            controlFullScreen(isControlPanelVisible());
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

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(mediaPlayer.isFullScreen()) {
            if (e.getYOnScreen() < 1000) {
                controlPanelVisible = false;
                controlsPanel.setVisible(false);
            } else {
                controlPanelVisible = true;
                controlsPanel.setVisible(true);
            }
        }
    }

    private final class TestPlayerMediaPlayerEventListener extends MediaPlayerEventAdapter {
        @Override
        public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {

        }

        @Override
        public void finished(MediaPlayer mediaPlayer) {

        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {

        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            MediaDetails mediaDetails = mediaPlayer.getMediaDetails();
        }

        @Override
        public void stopped(MediaPlayer mediaPlayer){
        }

        @Override
        public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
            if(newCount == 0) {
                return;
            }

            MediaDetails mediaDetails = mediaPlayer.getMediaDetails();

            MediaMeta mediaMeta = mediaPlayer.getMediaMeta();

            final Dimension dimension = mediaPlayer.getVideoDimension();
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
        public void error(MediaPlayer mediaPlayer){
        }

        @Override
        public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t subItem) {
        }

        @Override
        public void mediaDurationChanged(MediaPlayer mediaPlayer, long newDuration) {
        }

        @Override
        public void mediaParsedChanged(MediaPlayer mediaPlayer, int newStatus) {
        }

        @Override
        public void mediaFreed(MediaPlayer mediaPlayer){
        }

        @Override
        public void mediaStateChanged(MediaPlayer mediaPlayer, int newState) {
        }

        @Override
        public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
        }
    }

    /**
     *
     *
     * @param enable
     */
    @SuppressWarnings("unused")
    private void enableMousePointer(boolean enable) {
        if(enable) {
            videoSurface.setCursor(null);
        }
        else {
            Image blankImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            videoSurface.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(blankImage, new Point(0, 0), ""));
        }
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public PlayerControlsPanel getControlsPanel() {
        return controlsPanel;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public EmbeddedMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public VideoSurface getVideoSurface() { return videoSurface; }

    public boolean isControlPanelVisible() {
        return controlPanelVisible;
    }

    public void controlFullScreen(boolean b){
        mediaPlayer.toggleFullScreen();
        if(b){
            controlsPanel.setVisible(controlsPanel.isVisible());
        } else {
            controlsPanel.setVisible(!controlsPanel.isVisible());
        }
        getMenuBar().setVisible(!getMenuBar().isVisible());
        getMainFrame().invalidate();
        getMainFrame().validate();
    }
}