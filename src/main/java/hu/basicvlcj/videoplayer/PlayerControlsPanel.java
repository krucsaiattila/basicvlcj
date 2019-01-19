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


import com.github.wtekiela.opensub4j.api.OpenSubtitlesClient;
import com.github.wtekiela.opensub4j.impl.OpenSubtitlesClientImpl;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import hu.basicvlcj.srt.SRTInfo;
import hu.basicvlcj.srt.SRTReader;
import lombok.Getter;
import lombok.Setter;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.filter.swing.SwingFileFilterFactory;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 
 * A class which is responsible for all the controls attached to the video player.
 * Such as browse media, play, stop, volume adjustment, positions slider and more.
 *
 */
public class PlayerControlsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int SKIP_TIME_MS = 10 * 1000;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final EmbeddedMediaPlayer mediaPlayer;
    private final TestPlayer testPlayer;

    private JLabel timeLabel;
    private JSlider positionSlider;
    private JLabel chapterLabel;

    private JButton previousChapterButton;
    private JButton rewindButton;
    private JButton stopButton;
    private JButton pauseButton;
    private JButton playButton;
    private JButton fastForwardButton;
    private JButton nextChapterButton;
    private JButton toggleMuteButton;
    private JSlider volumeSlider;
    private JButton captureButton;
    private JButton ejectButton;
    private JButton fullScreenButton;
    private JButton subTitlesButton;
    private JButton searchForSubtitlesButton;

    private JFileChooser fileChooser;
    private JFileChooser subtitleChooser;

    private boolean mousePressedPlaying = false;

    @Getter
    private File actualFile;

    public PlayerControlsPanel(EmbeddedMediaPlayer mediaPlayer, TestPlayer testPlayer) {
        this.mediaPlayer = mediaPlayer;
        this.testPlayer = testPlayer;

        createUI();

        executorService.scheduleAtFixedRate(new UpdateRunnable(mediaPlayer), 0L, 300L, TimeUnit.MILLISECONDS);
    }

    private void createUI() {
        createControls();
        layoutControls();
        registerListeners();
    }

    private void createControls() {
        timeLabel = new JLabel("hh:mm:ss");

        positionSlider = new JSlider();
        positionSlider.setMinimum(0);
        positionSlider.setMaximum(1000);
        positionSlider.setValue(0);
        positionSlider.setToolTipText("Position");

        chapterLabel = new JLabel("00/00");

        previousChapterButton = new JButton();
        previousChapterButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_start_blue.png")));
        previousChapterButton.setToolTipText("Go to previous chapter");

        rewindButton = new JButton();
        rewindButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_rewind_blue.png")));
        rewindButton.setToolTipText("Skip back");

        stopButton = new JButton();
        stopButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_stop_blue.png")));
        stopButton.setToolTipText("Stop");

        pauseButton = new JButton();
        pauseButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_pause_blue.png")));
        pauseButton.setToolTipText("Play/pause");

        playButton = new JButton();
        playButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_play_blue.png")));
        playButton.setToolTipText("Play");

        fastForwardButton = new JButton();
        fastForwardButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_fastforward_blue.png")));
        fastForwardButton.setToolTipText("Skip forward");

        nextChapterButton = new JButton();
        nextChapterButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_end_blue.png")));
        nextChapterButton.setToolTipText("Go to next chapter");

        toggleMuteButton = new JButton();
        toggleMuteButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/sound.png")));
        toggleMuteButton.setToolTipText("Toggle Mute");

        volumeSlider = new JSlider();
        volumeSlider.setOrientation(JSlider.HORIZONTAL);
        volumeSlider.setMinimum(LibVlcConst.MIN_VOLUME);
        volumeSlider.setMaximum(LibVlcConst.MAX_VOLUME);
        volumeSlider.setPreferredSize(new Dimension(100, 40));
        volumeSlider.setToolTipText("Change volume");

        captureButton = new JButton();
        captureButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/camera.png")));
        captureButton.setToolTipText("Take picture");

        ejectButton = new JButton();
        ejectButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_eject_blue.png")));
        ejectButton.setToolTipText("Load/eject media");

        fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Play");
        fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newVideoFileFilter());
        fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newAudioFileFilter());
        fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newPlayListFileFilter());
        FileFilter defaultFilter = SwingFileFilterFactory.newMediaFileFilter();
        fileChooser.addChoosableFileFilter(defaultFilter);
        fileChooser.setFileFilter(defaultFilter);

        fullScreenButton = new JButton();
        fullScreenButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/arrow_out.png")));
        fullScreenButton.setToolTipText("Toggle full-screen");

        subTitlesButton = new JButton();
        subTitlesButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/comment.png")));
        subTitlesButton.setToolTipText("Select subtitle");

        subtitleChooser = new JFileChooser();
        subtitleChooser.setApproveButtonText("Choose");
        subtitleChooser.addChoosableFileFilter(SwingFileFilterFactory.newSubtitleFileFilter());
        defaultFilter = SwingFileFilterFactory.newSubtitleFileFilter();
        subtitleChooser.addChoosableFileFilter(defaultFilter);
        subtitleChooser.setFileFilter(defaultFilter);

        searchForSubtitlesButton = new JButton();
        searchForSubtitlesButton.setToolTipText("Search for subtitles online");
        searchForSubtitlesButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/world.png")));

        previousChapterButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        rewindButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        stopButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        pauseButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        playButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        fastForwardButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        nextChapterButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        toggleMuteButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        captureButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        ejectButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        fullScreenButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        subTitlesButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
        searchForSubtitlesButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
    }

    private void layoutControls() {
        setBorder(new EmptyBorder(4, 4, 4, 4));

        setLayout(new BorderLayout());

        JPanel positionPanel = new JPanel();
        positionPanel.setLayout(new GridLayout(1, 1));
        // positionPanel.add(positionProgressBar);
        positionPanel.add(positionSlider);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(8, 0));

        topPanel.add(timeLabel, BorderLayout.WEST);
        topPanel.add(positionPanel, BorderLayout.CENTER);
        topPanel.add(chapterLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();

        bottomPanel.setLayout(new FlowLayout());

        bottomPanel.add(previousChapterButton);
        bottomPanel.add(rewindButton);
        bottomPanel.add(stopButton);
        bottomPanel.add(pauseButton);
        bottomPanel.add(playButton);
        bottomPanel.add(fastForwardButton);
        bottomPanel.add(nextChapterButton);

        bottomPanel.add(volumeSlider);
        bottomPanel.add(toggleMuteButton);

        bottomPanel.add(captureButton);

        bottomPanel.add(ejectButton);

        bottomPanel.add(fullScreenButton);

        bottomPanel.add(subTitlesButton);

        bottomPanel.add(searchForSubtitlesButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Broken out position setting, handles updating mediaPlayer
     */
    private void setSliderBasedPosition() {
    	if(!mediaPlayer.isSeekable()) {
            return;
        }
        float positionValue = positionSlider.getValue() / 1000.0f;
        // Avoid end of file freeze-up
        if(positionValue > 0.99f) {
            positionValue = 0.99f;
        }
        mediaPlayer.setPosition(positionValue);
    }

    private void updateUIState() {
        if(!mediaPlayer.isPlaying()) {
            // Resume play or play a few frames then pause to show current position in video
            mediaPlayer.play();
            if(!mousePressedPlaying) {
                try {
                    // Half a second probably gets an iframe
                    Thread.sleep(500);
                }
                catch(InterruptedException e) {
                    // Don't care if unblocked early
                }
                mediaPlayer.pause();
            }
        }
        long time = mediaPlayer.getTime();
        int position = (int)(mediaPlayer.getPosition() * 1000.0f);
        int chapter = mediaPlayer.getChapter();
        int chapterCount = mediaPlayer.getChapterCount();
        updateTime(time);
        updatePosition(position);
        updateChapter(chapter, chapterCount);
    }

    private void skip(int skipTime) {
        // Only skip time if can handle time setting
        if(mediaPlayer.getLength() > 0) {
            mediaPlayer.skip(skipTime);
            updateUIState();
        }
    }

    private void registerListeners() {
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
//                updateVolume(mediaPlayer.getVolume());
            }
        });

        positionSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	
            	if(mediaPlayer.isPlaying()) {
                    mousePressedPlaying = true;
                    mediaPlayer.pause();
                }
                else {
                    mousePressedPlaying = false;
                }
                setSliderBasedPosition();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setSliderBasedPosition();
                updateUIState();
            }
        });

        previousChapterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.previousChapter();
            }
        });

        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                skip(-SKIP_TIME_MS);
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.stop();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.pause();
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.play();
            }
        });

        fastForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                skip(SKIP_TIME_MS);
            }
        });

        nextChapterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.nextChapter();
            }
        });

        toggleMuteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.mute();
                if(mediaPlayer.isMute()){
                    toggleMuteButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/sound.png")));
                } else {
                    toggleMuteButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/sound_mute.png")));
                }
            }
        });

        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                mediaPlayer.setVolume(source.getValue());
            }
        });

        captureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.saveSnapshot();
            }
        });

        ejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMedia();
            }
        });

        fullScreenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testPlayer.controlFullScreen();
            }
        });

        subTitlesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(actualFile != null){
                    subtitleChooser.setCurrentDirectory(actualFile);
                }
                if(JFileChooser.APPROVE_OPTION == subtitleChooser.showOpenDialog(PlayerControlsPanel.this)) {
                    actualFile = new File(subtitleChooser.getSelectedFile().getAbsolutePath());
                    SubtitleOverlay subtitleOverlay = (SubtitleOverlay)mediaPlayer.getOverlay();
                    SRTInfo info = SRTReader.read(actualFile);
                    subtitleOverlay.setSRTInfo(info);
                    subtitleOverlay.setActualFile(actualFile);
                }
            }
        });

        searchForSubtitlesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(actualFile != null){
                    searchForSubtitles();
                } else {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "No file selected!");
                }
            }
        });
    }

    private final class UpdateRunnable implements Runnable {

        private final MediaPlayer mediaPlayer;

        private UpdateRunnable(MediaPlayer mediaPlayer) {
            this.mediaPlayer = mediaPlayer;
        }

        @Override
        public void run() {
            final long time = mediaPlayer.getTime();
            final int position = (int)(mediaPlayer.getPosition() * 1000.0f);
            final int chapter = mediaPlayer.getChapter();
            final int chapterCount = mediaPlayer.getChapterCount();

            // Updates to user interface components must be executed on the Event
            // Dispatch Thread
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer.isPlaying()) {
                        updateTime(time);
                        updateSubtitles();
                        updatePosition(position);
                        updateChapter(chapter, chapterCount);
                    }
                }
            });
        }
    }

    private void updateSubtitles() {
        ((SubtitleOverlay)mediaPlayer.getOverlay()).update(false);
    }

    private void updateTime(long millis) {
        String s = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        timeLabel.setText(s);
    }

    private void updatePosition(int value) {
        // positionProgressBar.setValue(value);
        positionSlider.setValue(value);
    }

    private void updateChapter(int chapter, int chapterCount) {
        String s = chapterCount != -1 ? (chapter + 1) + "/" + chapterCount : "-";
        chapterLabel.setText(s);
        chapterLabel.invalidate();
        validate();
    }

    private void updateVolume(int value) {
        volumeSlider.setValue(value);
    }

    public void addMedia() {
        mediaPlayer.enableOverlay(false);
        if(actualFile != null){
            fileChooser.setCurrentDirectory(actualFile);
        }
        if(JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(PlayerControlsPanel.this)) {
            actualFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
            mediaPlayer.playMedia(actualFile.getAbsolutePath());
            positionSlider.setValue(0);
        }
        mediaPlayer.enableOverlay(true);
    }

    /**
     * searches for subtitles online via subtitles.org API
     */
    private void searchForSubtitles() {
        try{
            URL serverUrl = new URL("https", "api.opensubtitles.org", 443, "/xml-rpc");
            OpenSubtitlesClient osClient = new OpenSubtitlesClientImpl(serverUrl);

            osClient.login("atesz0505", "basicvlcj", "en", "TemporaryUserAgent");

            List<SubtitleInfo> subtitles = osClient.searchSubtitles("en", new File(actualFile.getAbsolutePath())).stream().filter(sub -> sub.getFormat().equals("srt")).collect(Collectors.toList());
            if(!subtitles.isEmpty()){
                JFrame availableSubtitlesFrame = new JFrame("Available subtitles online");
                availableSubtitlesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                availableSubtitlesFrame.setSize(850, 400);


                JTable subtitlesTable = new JTable(new NotEditableTableModel(subtitles));
                subtitlesTable.setFillsViewportHeight(true);
                subtitlesTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int row = subtitlesTable.rowAtPoint(e.getPoint());
                        try {
                            Desktop.getDesktop().browse(new URI(subtitles.get(row).getDownloadLink()));
                        } catch (IOException | URISyntaxException ex) {
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Failed to download subtitle file", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                subtitlesTable.getColumnModel().getColumn(0).setPreferredWidth(500);
                subtitlesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
                subtitlesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
                subtitlesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
                subtitlesTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());

                JScrollPane scrollPane = new JScrollPane(subtitlesTable);
                availableSubtitlesFrame.add(scrollPane);

                availableSubtitlesFrame.setVisible(true);
            }

            osClient.logout();
        } catch (IOException |XmlRpcException e){
            e.printStackTrace();
        }
    }


    /**
     * class to add JButton to the JTable column
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    /**
     * class to provide a non-editable table model
     */
    class NotEditableTableModel extends AbstractTableModel {

        private String[] columnNames;
        private Object[][] data;


        public NotEditableTableModel(List<SubtitleInfo> subtitles){
            columnNames = new String[]{"Name", "Language", "Times downloaded", "Download"};

            data = new Object[subtitles.size()][4];
            for(int i = 0; i < subtitles.size(); i++){
                data[i][0] = subtitles.get(i).getFileName();
                data[i][1] = subtitles.get(i).getLanguage();
                data[i][2] = String.valueOf(subtitles.get(i).getDownloadsNo());
                data[i][3] = "Download";
            }
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex){
            return false;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

    }
}