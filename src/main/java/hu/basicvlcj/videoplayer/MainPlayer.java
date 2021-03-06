package hu.basicvlcj.videoplayer;/*
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


import lombok.Getter;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A class that is the core of the mediaplayer.
 * It manages the controls panel, the menubar and the screen surface.
 *
 */
@Getter
public class MainPlayer implements MouseMotionListener, MouseListener {

    private final JFrame mainFrame;

    private Canvas videoSurface;

    private final PlayerControlsPanel controlsPanel;

    private MenuBar menuBar;

    private MediaPlayerFactory mediaPlayerFactory;

    private EmbeddedMediaPlayer mediaPlayer;


    public MainPlayer() {

        videoSurface = new Canvas();

        videoSurface.setBackground(Color.black);
        videoSurface.setSize(800, 600);

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        List<String> vlcArgs = new ArrayList<String>();

        vlcArgs.add("--no-snapshot-preview");
        vlcArgs.add("--quiet");
        vlcArgs.add("--quiet-synchro");
        vlcArgs.add("--intf");
        vlcArgs.add("dummy");

        mainFrame = new JFrame("VLC Language Learner");

        FullScreenStrategy fullScreenStrategy = new Win32FullScreenStrategy(mainFrame);

        mediaPlayerFactory = new MediaPlayerFactory(vlcArgs.toArray(new String[vlcArgs.size()]));
        mediaPlayerFactory.setUserAgent("vlcj test player");

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

        // Global AWT key handler
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
                        else if((keyEvent.getKeyCode() == KeyEvent.VK_UP) && ((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                        	((SubtitleOverlay)(mediaPlayer.getOverlay())).increaseYOffset(5);
                        }
                        else if((keyEvent.getKeyCode() == KeyEvent.VK_DOWN) && ((keyEvent.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                        	((SubtitleOverlay)(mediaPlayer.getOverlay())).decreaseYOffset(5);
                        }
                        else if((keyEvent.getKeyCode() == KeyEvent.VK_ADD)) {
                        	((SubtitleOverlay)(mediaPlayer.getOverlay())).increaseFontSize(2);
                        }
                        else if((keyEvent.getKeyCode() == KeyEvent.VK_SUBTRACT)) {
                        	((SubtitleOverlay)(mediaPlayer.getOverlay())).decreaseFontSize(2);
                        }
                        
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);

        mainFrame.setVisible(true);

        mediaPlayer.addMediaPlayerEventListener(new TestPlayerMediaPlayerEventListener());

        videoSurface.addMouseListener(this);
        videoSurface.addMouseMotionListener(this);

        mediaPlayer.setOverlay(new SubtitleOverlay(mainFrame, mediaPlayer));
        mediaPlayer.enableOverlay(true);
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 1) {
            mediaPlayer.getOverlay().dispatchEvent(e);
        }
    	
    	if (e.getClickCount() == 2 && !e.isConsumed()) {
            e.consume();
            controlFullScreen();
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
            if (e.getYOnScreen() < Toolkit.getDefaultToolkit().getScreenSize().height - 5 && controlsPanel.isVisible()) {
                controlsPanel.setVisible(false);
                ((SubtitleOverlay)(mediaPlayer.getOverlay())).decreaseYOffset(controlsPanel.getHeight());
            } else if (e.getYOnScreen() >= Toolkit.getDefaultToolkit().getScreenSize().height - 5 && !controlsPanel.isVisible()) {
                controlsPanel.setVisible(true);
                ((SubtitleOverlay)(mediaPlayer.getOverlay())).increaseYOffset(controlsPanel.getHeight());
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
        }

        @Override
        public void stopped(MediaPlayer mediaPlayer){
        }

        @Override
        public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
            if(newCount == 0) {
                return;
            }

            final Dimension dimension = mediaPlayer.getVideoDimension();
            if(dimension != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        videoSurface.setSize(dimension);
                        mainFrame.pack();
                    }
                });
            }

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
     * Method that controls the full screen
     */
    protected void controlFullScreen(){
        mediaPlayer.toggleFullScreen();
        
        if(mediaPlayer.isFullScreen()){
            controlsPanel.setVisible(false);
            getMenuBar().setVisible(false);
        } else {
            controlsPanel.setVisible(true);
            getMenuBar().setVisible(true);
        }
        
        getMainFrame().invalidate();
        getMainFrame().validate();
    }
}