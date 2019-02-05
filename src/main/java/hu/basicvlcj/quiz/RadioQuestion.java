package hu.basicvlcj.quiz;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RadioQuestion extends JPanel implements ActionListener {
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

    public RadioQuestion(String[] options, String question, String ans, Quiz quiz) {
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
            if (selected == correctAns) {
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

    public void showResult() {
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