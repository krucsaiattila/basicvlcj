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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Data
public class QuizFrame extends JFrame implements ActionListener {

    private WordService wordService;

    private PlayerControlsPanel playerControlsPanel;

    private JTextField answerField;

    private JFrame questionFrame;

    private JButton beginQuizButton;
    private JButton nextButton;

    private boolean isFinished;

    private List<Word> wordList;
    private List<Integer> alreadyUsedWordsIndex = new ArrayList<>();
    private List<String> actualAnswers;

    public QuizFrame(PlayerControlsPanel controlsPanel) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Quiz");
        setSize(500, 400);

        this.playerControlsPanel = controlsPanel;
        wordService = new WordService();
        isFinished = false;

        createBeginTestWindow(getContentPane());
        setVisible(true);
    }

    private void createQuestion() {
        if (questionFrame != null) {
            questionFrame.dispose();
        }
        questionFrame = new JFrame();
        questionFrame.setLayout(new GridLayout(3, 1));
        questionFrame.setSize(500, 400);
        questionFrame.setTitle("Task");

        JPanel header = new JPanel();
        header.setLayout(new FlowLayout());

        header.add(new JLabel("Translate the given word(s) to the original language!"));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));

        panel.add(createForeignWordLabel(wordList));

        answerField = new JTextField();
        answerField.setMinimumSize(new Dimension(100, 10));
        panel.add(answerField);

        JPanel next = new JPanel();
        nextButton = new JButton("Next");
        nextButton.addActionListener(this);
        next.add(nextButton);

        questionFrame.add(header);
        questionFrame.add(panel);
        questionFrame.add(next);

        if (isFinished) {
            questionFrame.dispose();
        } else {
            questionFrame.setVisible(true);
        }
        questionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JLabel createForeignWordLabel(List<Word> wordList) {
        Random rand = new Random();
        int randomNumber;

        randomNumber = rand.nextInt(wordList.size());
        if(alreadyUsedWordsIndex.size() != wordList.size()){
            while (alreadyUsedWordsIndex.contains(randomNumber)){
                randomNumber = rand.nextInt(wordList.size());
            }
            actualAnswers = Arrays.asList(wordList.get(randomNumber).getMeaning().split(", "));
            alreadyUsedWordsIndex.add(randomNumber);
            return new JLabel(wordList.get(randomNumber).getForeignWord());
        } else {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Finished quiz", "Finish", JOptionPane.YES_OPTION);
            isFinished = true;
            questionFrame.dispose();
            this.dispose();
            return new JLabel();
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
            dispose();
            wordList = wordService.getAllByFilename(playerControlsPanel.getActualFile().getName());
            createQuestion();
        } else if (e.getSource() == nextButton) {
            System.out.println(actualAnswers);
            if (actualAnswers.contains(answerField.getText())) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Correct answer", "Correct", JOptionPane.OK_OPTION);
                createQuestion();
            } else {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Incorrect answer", "Incorrect", JOptionPane.YES_OPTION);
                createQuestion();
            }
        }
    }
}
