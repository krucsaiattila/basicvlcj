package hu.basicvlcj.videoplayer;

import com.itextpdf.text.DocumentException;
import hu.basicvlcj.quiz.Quiz;
import hu.basicvlcj.service.PDFGeneratorService;
import hu.basicvlcj.service.WordService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;

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
    private JMenu generateMenu;
    private JMenuItem generatePdfMenuItem;

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

        generateMenu = new JMenu("Generate");
        generatePdfMenuItem = new JMenuItem("Generate PDF from unknown words");
        generatePdfMenuItem.addActionListener(this);
        generateMenu.add(generatePdfMenuItem);

        add(generateMenu);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == mediaExitMenuItem){
            mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        } else if(e.getSource() == mediaPlayFileMenuItem){
            testPlayer.getControlsPanel().addMedia();
        } else if(e.getSource() == quizMenuItem){
            if (new WordService().getAllByFilename(testPlayer.getControlsPanel().getActualFile().getName()).size() < 6) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Not enough words to generate quiz!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                new Quiz(testPlayer.getControlsPanel());
            }
        } else if (e.getSource() == generatePdfMenuItem) {
            try {
                PDFGeneratorService pdf = new PDFGeneratorService();
                pdf.createDictionary(testPlayer.getControlsPanel().getActualFile().getName());
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "PDF file successfully created");
            } catch (FileNotFoundException | DocumentException e1) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Failed to create PDF file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}


