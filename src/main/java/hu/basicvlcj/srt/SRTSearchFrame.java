package hu.basicvlcj.srt;

import com.github.wtekiela.opensub4j.api.OpenSubtitlesClient;
import com.github.wtekiela.opensub4j.impl.OpenSubtitlesClientImpl;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import hu.basicvlcj.translate.LanguageSelectorFrame;
import hu.basicvlcj.wget.Wget;
import org.apache.xmlrpc.XmlRpcException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class SRTSearchFrame extends JFrame implements ActionListener {

    private JTextField nameTextField;
    private JTextField seasonTextField;
    private JTextField episodeTextField;

    private JButton okButton;

    private JToggleButton isSerial;

    private String directoryPath;

    public SRTSearchFrame(String directoryPath) {
        setTitle("Search for subtitles online");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 100);
        setAlwaysOnTop(true);
        setMinimumSize(new Dimension(700, 100));

        this.directoryPath = directoryPath;

        JPanel panel = new JPanel(new FlowLayout());

        isSerial = new JToggleButton("Serial");
        isSerial.setSelected(false);
        isSerial.addActionListener(this);
        panel.add(isSerial);

        panel.add(new JLabel("Name:"));

        nameTextField = new JTextField("", 20);
        panel.add(nameTextField);

        panel.add(new JLabel("Season:"));

        seasonTextField = new JTextField("", 4);
        seasonTextField.setEnabled(false);
        panel.add(seasonTextField);

        panel.add(new JLabel("Episode:"));

        episodeTextField = new JTextField("", 4);
        episodeTextField.setEnabled(false);
        panel.add(episodeTextField);

        okButton = new JButton("Search");
        okButton.addActionListener(this);
        panel.add(okButton);

        add(panel);
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == isSerial) {
            seasonTextField.setEnabled(!seasonTextField.isEnabled());
            episodeTextField.setEnabled(!episodeTextField.isEnabled());
        } else if (e.getSource() == okButton) {
            this.dispose();
            try {
                URL serverUrl = new URL("https", "api.opensubtitles.org", 443, "/xml-rpc");
                OpenSubtitlesClient osClient = new OpenSubtitlesClientImpl(serverUrl);

                osClient.login("atesz0505", "basicvlcj", "en", "TemporaryUserAgent");

                List<SubtitleInfo> subtitles;
                //if it is a serial
                if (isSerial.isSelected()) {
                    subtitles = osClient.searchSubtitles(LanguageSelectorFrame.currentToLanguage = System.getProperty("user.language"), nameTextField.getText(), seasonTextField.getText(), episodeTextField.getText()).stream().filter(sub -> sub.getFormat().equals("srt")).collect(Collectors.toList());
                    //if it is a movie
                } else {
                    subtitles = osClient.searchSubtitles(LanguageSelectorFrame.currentToLanguage = System.getProperty("user.language"), nameTextField.getText(), "0", "0").stream().filter(sub -> sub.getFormat().equals("srt")).collect(Collectors.toList());
                }
                if (!subtitles.isEmpty()) {
                    JFrame availableSubtitlesFrame = new JFrame("Available subtitles online");
                    availableSubtitlesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    availableSubtitlesFrame.setSize(850, 400);


                    JTable subtitlesTable = new JTable(new NotEditableTableModel(subtitles));
                    subtitlesTable.setFillsViewportHeight(true);
                    subtitlesTable.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int row = subtitlesTable.rowAtPoint(e.getPoint());
                            Wget.wGet(subtitles.get(row).getZipDownloadLink(), directoryPath);
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
                } else {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "No subtitles found online.", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
                osClient.logout();
            } catch (IOException | XmlRpcException ex) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occurred. Please check your internet connection.", "Error", JOptionPane.ERROR_MESSAGE);
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


        public NotEditableTableModel(List<SubtitleInfo> subtitles) {
            columnNames = new String[]{"Name", "Language", "Times downloaded", "Download"};

            data = new Object[subtitles.size()][4];
            for (int i = 0; i < subtitles.size(); i++) {
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
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

    }
}
