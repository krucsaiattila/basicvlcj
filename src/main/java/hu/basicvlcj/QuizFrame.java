package hu.basicvlcj;

import hu.basicvlcj.model.Word;
import hu.basicvlcj.service.WordService;
import hu.basicvlcj.videoplayer.PlayerControlsPanel;
import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
public class QuizFrame extends JFrame implements ActionListener {

    private WordService wordService;

    private PlayerControlsPanel playerControlsPanel;

    private JButton beginQuizButton;
    private JButton nextButton;

    private List<Word> wordList;
    List<Integer> alreadyUsedWordsIndex = new ArrayList<>();

    public QuizFrame(PlayerControlsPanel controlsPanel) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Quiz");
        setSize(500, 400);

        createBeginTestWindow(getContentPane());

        setVisible(true);

        this.playerControlsPanel = controlsPanel;
        wordService = new WordService();
    }

    private void addComponentsToPane(Container pane) {
        pane.removeAll();
        wordList = wordService.getAllByFilename(playerControlsPanel.getActualFile().getName());
        System.out.println(wordList.toString());

        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx = 30;
        c.gridx = 1;
        c.gridy = 0;
        pane.add(new JLabel("Translate the given word(s) to the original language!"), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipadx = 30;
        c.gridx = 1;
        c.gridy = 1;
        pane.add(createForeignWordLabel(wordList), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        JTextField answer = new JTextField();
        pane.add(answer, c);

        nextButton = new JButton("Next");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;
        c.anchor = GridBagConstraints.PAGE_END;
        c.insets = new Insets(10,0,0,0);  //top padding
        c.gridx = 1;
        c.gridy = 3;

    }

    private JLabel createForeignWordLabel(List<Word> wordList) {
        Random rand = new Random();
        int randomNumber;

        randomNumber = rand.nextInt(wordList.size());
        if(alreadyUsedWordsIndex.size() != wordList.size()){
            while (alreadyUsedWordsIndex.contains(randomNumber)){
                randomNumber = rand.nextInt(wordList.size());
                alreadyUsedWordsIndex.add(randomNumber);
            }
            return new JLabel(wordList.get(randomNumber).getForeignWord());
        } else {
            return new JLabel("ELFOGYOTT");
        }
    }

    private void createBeginTestWindow(Container pane){
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;

        beginQuizButton = new JButton("Begin quiz");
        beginQuizButton.addActionListener(this);

        pane.add(beginQuizButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == beginQuizButton) {
            addComponentsToPane(getContentPane());
        }
    }
}
