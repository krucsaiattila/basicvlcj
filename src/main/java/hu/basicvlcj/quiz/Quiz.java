package hu.basicvlcj.quiz;

import hu.basicvlcj.model.Word;
import hu.basicvlcj.service.WordService;
import hu.basicvlcj.videoplayer.PlayerControlsPanel;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public Quiz(PlayerControlsPanel playerControlsPanel) {
        super("Corevia");
        setResizable(true);
        setSize(650, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        wordService = new WordService();
        words = wordService.getAllByFilename(playerControlsPanel.getActualFile().getName());

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

    public void next() {
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

    public void showSummary() {
        JOptionPane.showMessageDialog(null, "That's it! Here is your summary:" +
                "\nYou answered " + wrongs + " questions wrong" +
                "\nYou answered " + (total - wrongs) + " right" +
                "\nGiving a correct answer chance: \t\t" + (int) (((float) (total - wrongs) / total) * 100) + "%"
        );
        this.dispose();
    }
}
