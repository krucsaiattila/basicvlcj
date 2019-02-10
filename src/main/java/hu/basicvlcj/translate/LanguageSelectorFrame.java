package hu.basicvlcj.translate;

import hu.basicvlcj.service.WordService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class LanguageSelectorFrame extends JFrame implements ActionListener {

    public static String currentFromLanguage;
    public static String currentToLanguage;
    public static boolean languageDetection;

    private JComboBox fromComboBox;
    private JComboBox toComboBox;

    private JButton okButton;

    private JToggleButton detectLanguageRadioButton;

    private Map<String, String> languageAndLanguageCodes;

    public LanguageSelectorFrame(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Select languages");
        setSize(600, 120);
        setAlwaysOnTop(true);
        setMinimumSize(new Dimension(400, 120));

        detectLanguageRadioButton = new JToggleButton();
        detectLanguageRadioButton.setText("Detect source language");
        detectLanguageRadioButton.addActionListener(this);

        setLanguages();
        toComboBox = new JComboBox((languageAndLanguageCodes.keySet().toArray()));
        fromComboBox = new JComboBox(languageAndLanguageCodes.keySet().toArray());

        toComboBox.addActionListener(this);
        fromComboBox.addActionListener(this);

        setButtons();

        okButton = new JButton("OK");
        okButton.addActionListener(this);

        JPanel panel = new JPanel();

        panel.setLayout(new FlowLayout());

        panel.add(detectLanguageRadioButton);
        panel.add(new JLabel("From:"));
        panel.add(fromComboBox);
        panel.add(new JLabel("To:"));
        panel.add(toComboBox);
        panel.add(okButton);

        add(panel);

        setVisible(true);
    }

    private void setButtons() {
        Iterator it = languageAndLanguageCodes.entrySet().iterator();

        if (languageDetection) {
            detectLanguageRadioButton.setSelected(true);
            fromComboBox.setEnabled(false);
            toComboBox.setEnabled(false);
            toComboBox.setSelectedItem("English");

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue().equals(currentToLanguage)) {
                    toComboBox.setSelectedItem(pair.getValue());
                }
            }
        } else {
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getValue().equals(currentFromLanguage)) {
                    fromComboBox.setSelectedItem(pair.getKey());
                }
                if (pair.getValue().equals(currentToLanguage)) {
                    toComboBox.setSelectedItem(pair.getKey());
                }
            }
        }
        it.remove();
    }

    private void setLanguages() {
        languageAndLanguageCodes = new HashMap<>();
        languageAndLanguageCodes.put("Afrikaans", "af");
        languageAndLanguageCodes.put("Arabic", "ar");
        languageAndLanguageCodes.put("Bangla", "bn");
        languageAndLanguageCodes.put("Bosnian (Latin)", "bs");
        languageAndLanguageCodes.put("Bulgarian", "bg");
        languageAndLanguageCodes.put("Catalan", "ca");
        languageAndLanguageCodes.put("Chinese Simplified", "zh-Hans");
        languageAndLanguageCodes.put("Croatian", "hr");
        languageAndLanguageCodes.put("Czech", "cs");
        languageAndLanguageCodes.put("Danish", "da");
        languageAndLanguageCodes.put("Dutch", "nl");
        languageAndLanguageCodes.put("English", "en");
        languageAndLanguageCodes.put("Estonian", "et");
        languageAndLanguageCodes.put("Finnish", "fi");
        languageAndLanguageCodes.put("French", "fr");
        languageAndLanguageCodes.put("German", "de");
        languageAndLanguageCodes.put("Greek", "el");
        languageAndLanguageCodes.put("Haitian Creole", "ht");
        languageAndLanguageCodes.put("Hebrew", "he");
        languageAndLanguageCodes.put("Hindi", "hi");
        languageAndLanguageCodes.put("Hmong Daw", "mww");
        languageAndLanguageCodes.put("Hungarian", "hu");
        languageAndLanguageCodes.put("Icelandic", "is");
        languageAndLanguageCodes.put("Indonesian", "id");
        languageAndLanguageCodes.put("Italian", "it");
        languageAndLanguageCodes.put("Japanese", "ja");
        languageAndLanguageCodes.put("Kiswahili", "sw");
        languageAndLanguageCodes.put("Klingon", "tlh");
        languageAndLanguageCodes.put("Korean", "ko");
        languageAndLanguageCodes.put("Latvian", "lv");
        languageAndLanguageCodes.put("Lithuanian", "lt");
        languageAndLanguageCodes.put("Malay", "ms");
        languageAndLanguageCodes.put("Maltese", "mt");
        languageAndLanguageCodes.put("Norwegian", "nb");
        languageAndLanguageCodes.put("Persian", "fa");
        languageAndLanguageCodes.put("Polish", "pl");
        languageAndLanguageCodes.put("Portuguese", "pt");
        languageAndLanguageCodes.put("Romanian", "ro");
        languageAndLanguageCodes.put("Russian", "ru");
        languageAndLanguageCodes.put("Serbian (Latin)", "sr-Latn");
        languageAndLanguageCodes.put("Slovak", "sk");
        languageAndLanguageCodes.put("Slovenian", "sl");
        languageAndLanguageCodes.put("Spanish", "es");
        languageAndLanguageCodes.put("Swedish", "sv");
        languageAndLanguageCodes.put("Tamil", "ta");
        languageAndLanguageCodes.put("Thai", "th");
        languageAndLanguageCodes.put("Turkish", "tr");
        languageAndLanguageCodes.put("Ukrainian", "uk");
        languageAndLanguageCodes.put("Urdu", "ur");
        languageAndLanguageCodes.put("Vietnamese", "vi");
        languageAndLanguageCodes.put("Welsh", "cy");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == okButton){
            new WordService().deleteAll();
            setLanguages();
            Iterator it = languageAndLanguageCodes.entrySet().iterator();

            if (languageDetection) {
                currentFromLanguage = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    if (pair.getKey().equals(toComboBox.getSelectedItem())) {
                        currentToLanguage = (String) pair.getValue();
                    }
                    it.remove();
                }
            } else {
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    if (pair.getKey().equals(fromComboBox.getSelectedItem())) {
                        currentFromLanguage = (String) pair.getValue();
                    }
                    if (pair.getKey().equals(toComboBox.getSelectedItem())) {
                        currentToLanguage = (String) pair.getValue();
                    }
                    it.remove();
                }
            }
            this.dispose();
        } else if (e.getSource() == fromComboBox) {
            if (!languageDetection) {
                if (!fromComboBox.getSelectedItem().equals("English")) {
                    toComboBox.setSelectedItem("English");
                    toComboBox.setEnabled(false);
                } else {
                    toComboBox.setEnabled(true);
                }
            }
        } else if (e.getSource() == toComboBox) {
            if (!languageDetection) {
                if (!toComboBox.getSelectedItem().equals("English")) {
                    fromComboBox.setSelectedItem("English");
                    fromComboBox.setEnabled(false);
                } else {
                    fromComboBox.setEnabled(true);
                }
            }
        } else if (e.getSource() == detectLanguageRadioButton) {
            languageDetection = !languageDetection;
            fromComboBox.setEnabled(!languageDetection);
            toComboBox.setEnabled(!languageDetection);
            if (languageDetection) {
                toComboBox.setSelectedItem("English");
            }
        }
    }
}
