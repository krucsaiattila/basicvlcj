package hu.basicvlcj;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RadioQuestion extends JPanel implements ActionListener {
    String correctAns;
    Quiz quiz;
    String selected;
    boolean used;
    //questions
    JPanel qPanel = new JPanel();
    JPanel q2Panel = new JPanel();
    //answers
    JPanel aPanel = new JPanel();
    JRadioButton[] responses;
    ButtonGroup group = new ButtonGroup();
    //bottom
    JPanel botPanel = new JPanel();
    JButton next = new JButton("Next");
    JButton finish = new JButton("Finish");

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
        quiz.total++;
        if (selected.equals(correctAns)) {
            JOptionPane.showMessageDialog(null, text + " is correct!\nGood Job!");
        } else {
            quiz.wrongs++;
            JOptionPane.showMessageDialog(null, text + " is wrong.\nTry again!");
        }
    }
}