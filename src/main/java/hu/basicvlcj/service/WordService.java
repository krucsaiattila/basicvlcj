package hu.basicvlcj.service;

import hu.basicvlcj.model.Word;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A service class that is responsible for communicating with the database
 */
public class WordService {

    public void create(Word word) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
             PreparedStatement stat = connection.prepareStatement("INSERT INTO WORDS (WORDS_ID, FOREIGN_WORD, MEANING, EXAMPLE, FILENAME) VALUES (?,?,?,?,?)")) {
            stat.setString(1, UUID.randomUUID().toString());
            stat.setString(2, word.getForeignWord());
            stat.setString(3, word.getMeaning());
            stat.setString(4, word.getExample());
            stat.setString(5, word.getFilename());

            stat.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occurred", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Word> getAllByFilename(String filename){
        List<Word> wordsList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
             Statement stat = connection.createStatement()) {
            ResultSet rs = stat.executeQuery("SELECT * FROM WORDS WHERE FILENAME=" + "'" + filename + "'");
            while (rs.next()) {
                Word w = new Word();
                w.setForeignWord(rs.getString(rs.findColumn("FOREIGN_WORD")));
                w.setMeaning(rs.getString(rs.findColumn("MEANING")));
                w.setExample(rs.getString(rs.findColumn("EXAMPLE")));

                wordsList.add(w);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occurred", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return wordsList;
    }

    public boolean isAlreadySaved(String foreignWord) {
        List<Word> wordsList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
             Statement stat = connection.createStatement()) {
            ResultSet rs = stat.executeQuery("SELECT * FROM WORDS WHERE FOREIGN_WORD=" + "'" + foreignWord + "'");
            while (rs.next()) {
                Word w = new Word();
                wordsList.add(w);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occurred", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return !wordsList.isEmpty();
    }

    public void deleteAll() {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
             Statement stat = connection.createStatement()) {
            stat.executeUpdate("DELETE FROM WORDS");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occurred", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
