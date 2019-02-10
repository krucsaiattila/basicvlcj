package hu.basicvlcj.quiz;

import hu.basicvlcj.model.Word;
import hu.basicvlcj.service.WordService;
import hu.basicvlcj.videoplayer.PlayerControlsPanel;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class that is responsible for generating a quiz from the words, that the user clicked on.
 */
public class Quiz extends JFrame {
    private JPanel p = new JPanel();
    private CardLayout cards = new CardLayout();
    private int numQs;
    @Getter
    @Setter
    private int wrongs = 0;
    @Getter
    @Setter
    private int total = 0;

    private WordService wordService;
    private List<Word> words;

    private RadioQuestion[] questions;

    public Quiz() {
        super("Corevia");
        setResizable(true);
        setSize(650, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        wordService = new WordService();
        words = wordService.getAllByFilename(PlayerControlsPanel.actualSubtitleFile.getName());

        questions = new RadioQuestion[words.size()];
        for (int i = 0; i < questions.length; i++) {
            questions[i] = new RadioQuestion(generateRandomAnswers(i), words.get(i).getMeaning(), words.get(i).getForeignWord(), this);
        }

        p.setLayout(cards);
        numQs = questions.length;
        for (int i = 0; i < numQs; i++) {
            p.add(questions[i], "q" + i);
        }
        Random r = new Random();
        int i = r.nextInt(numQs);
        cards.show(p, "q" + i);
        add(p);
        setVisible(true);
    }

    /**
     *
     * @param i the answer or the current question
     * @return an array of random alternatives and the answer for the question
     */
    private String[] generateRandomAnswers(int i) {
        List<String> alternatives = new ArrayList<>();

        Random rand = new Random();

        List<Integer> usedIndexes = new ArrayList<>();
        int counter = 0;
        while (counter < 4) {
            int r = rand.nextInt(words.size());
            if (!usedIndexes.contains(r) && r != i) {
                alternatives.add(words.get(r).getForeignWord());
                usedIndexes.add(r);
                counter++;
            }
        }
        alternatives.set(rand.nextInt(4), words.get(i).getForeignWord());
        return alternatives.toArray(new String[4]);
    }

    /**
     * Shows the next question
     */
    private void next() {
        if ((total - wrongs) == numQs) {
            showSummary();
        } else {
            Random r = new Random();
            boolean found = false;
            int i = 0;
            while (!found) {
                i = r.nextInt(numQs);
                if (!questions[i].isUsed()) {
                    found = true;
                }
            }
            cards.show(p, "q" + i);
        }
    }

    /**
     * Shows the summary after a finished quiz
     */
    private void showSummary() {
        JOptionPane.showMessageDialog(null, "That's it! Here is your summary:" +
                "\nYou answered " + wrongs + " questions wrong" +
                "\nYou answered " + (total - wrongs) + " right" +
                "\nGiving a correct answer chance: \t\t" + (int) (((float) (total - wrongs) / total) * 100) + "%"
        );
        this.dispose();
    }

    /**
     * Class that represents a question from the quiz
     */
    class RadioQuestion extends JPanel implements ActionListener {
        private String correctAns;
        private Quiz quiz;
        private String selected;
        @Getter
        private boolean used;
        //questions
        private JPanel qPanel = new JPanel();
        private JPanel q2Panel = new JPanel();
        //answers
        private JPanel aPanel = new JPanel();
        private JRadioButton[] responses;
        private ButtonGroup group = new ButtonGroup();
        //bottom
        private JPanel botPanel = new JPanel();
        private JButton next = new JButton("Next");
        private JButton finish = new JButton("Finish");

        RadioQuestion(String[] options, String question, String ans, Quiz quiz) {
            this.quiz = quiz;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            correctAns = ans;
            //question
            qPanel.add(new JLabel("Choose the correct translation of the given word!"));
            add(qPanel);

            //question word
            q2Panel.add(new JLabel(question));
            add(q2Panel);
            //answer
            responses = new JRadioButton[options.length];
            for (int i = 0; i < options.length; i++) {
                responses[i] = new JRadioButton(options[i]);
                responses[i].addActionListener(this);
                group.add(responses[i]);
                aPanel.add(responses[i]);
            }
            add(aPanel);
            //bottom
            next.addActionListener(this);
            finish.addActionListener(this);
            botPanel.add(next);
            botPanel.add(finish);
            add(botPanel);
        }

        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            //next button
            if (src.equals(next)) {
                showResult();
                if (selected.equals(correctAns)) {
                    used = true;
                    quiz.next();
                }
            }
            //finish button
            if (src.equals(finish)) {
                quiz.showSummary();
            }
            //radio buttons
            for (int i = 0; i < responses.length; i++) {
                if (src == responses[i]) {
                    selected = responses[i].getText();
                }
            }
        }

        /**
         * Method that shows the user the correctness of his/her answer
         */
        private void showResult() {
            String text = selected;
            quiz.setTotal(quiz.getTotal() + 1);
            if (selected.equals(correctAns)) {
                JOptionPane.showMessageDialog(null, text + " is correct!\nGood Job!");
            } else {
                quiz.setTotal(quiz.getTotal() + 1);
                JOptionPane.showMessageDialog(null, text + " is wrong.\nTry again!");
            }
        }
    }
}
