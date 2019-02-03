package hu.basicvlcj;

import hu.basicvlcj.model.Word;
import hu.basicvlcj.service.WordService;
import hu.basicvlcj.videoplayer.PlayerControlsPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Quiz extends JFrame {
    JPanel p = new JPanel();
    CardLayout cards = new CardLayout();
    int numQs;
    int wrongs = 0;
    int total = 0;

    private WordService wordService;
    private List<Word> words;

    String[][] answers = {
            {"Portuguese", "Spanish", "Italian", "English"},
            {"24", "32", "36", "64"},
            {"1935", "1945", "1955", "1965"},
            {"Earth", "Jupiter", "Mars", "Uranus"},
            {"Thomas Edison", "Louis Pasteur", "Alexander Fleming", "George Orwell"},
            {"True", "False"},
            {"True", "False"},
            {"Fire", "Darkness", "Wind", "Spiders"},
            {"Volleyball", "Basketball", "Hockey", "Football"},
            {"Italy", "Spain", "France", "Switzerland"},
            {"True", "False"},
            {"Apollo 7", "Apollo 9", "Apollo 11", "Apollo 13"},
            {"Slovakia", "Ukraine", "Hungary", "Russia"},
            {"True", "False"},
            {"Britney Spears", "Scarlett Johansson", "Mila Kunis", "Angelina Jolie"},
            {"Steve Rogers", "Chris Hemsworth", "Tony Stark", "Toby Jenkins"},
            {"Labrosse", "Orleans", "Sandillon", "Attray"},
    };

    RadioQuestion[] questions;
//
//			={
//
//		new RadioQuestion(
//			"What language do they speak in Brazil?",
//			answers[0],
//			0,this
//		),
//		new RadioQuestion(
//			"How many black squares are there on a chess board?",
//			answers[1],
//			1,this
//		),
//		new RadioQuestion(
//			"What year did Albert Einstein die?",
//			answers[2],
//			2,this
//		),
//		new RadioQuestion(
//			"Which is the largest planet in the solar system?",
//			answers[3],
//			1,this
//		),
//		new RadioQuestion(
//			"Who invented Penicillin?",
//			answers[4],
//			2,this
//		),
//		new RadioQuestion(
//			"True of false: Volleyball was invented as a game for businessmen.",
//			answers[5],
//			0,this
//		),
//		new RadioQuestion(
//			"True of false: Only Americans and Soviets have walked on the Moon.",
//			answers[6],
//			1,this
//		),
//		new RadioQuestion(
//			"Anemophobia is the fear of what?",
//			answers[7],
//			2,this
//		),
//		new RadioQuestion(
//			"Which game is played with five players on either side?",
//			answers[8],
//			1,this
//		),
//		new RadioQuestion(
//			"Which of the following countries is landlocked?",
//			answers[9],
//			3,this
//		),
//		new RadioQuestion(
//			"True of false: The Oscar-winning film A Beautiful Mind (2001) deals with schizophrenia.",
//			answers[10],
//			0,this
//		),
//		new RadioQuestion(
//			"Which Apollo mission landed the first humans on the Moon?",
//			answers[11],
//			2,this
//		),
//		new RadioQuestion(
//			"The reactor at the site of the Chernobyl nuclear disaster is now in which country?",
//			answers[12],
//			1,this
//		),
//		new RadioQuestion(
//			"True of false: The tango originated in Argentina.",
//			answers[13],
//			0,this
//		),
//		new RadioQuestion(
//			"Who plays Lara Croft in the Tomb Raider series of films?",
//			answers[14],
//			3,this
//		),
//		new RadioQuestion(
//			"What is Iron Man's real name?",
//			answers[15],
//			2,this
//		)
//		,
//		new RadioQuestion(
//			"Joan of Arc is also known as the Maid of where?",
//			answers[16],
//			1,this
//		)
//	};

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
                if (!questions[i].used) {
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
