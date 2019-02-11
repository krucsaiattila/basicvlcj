package hu.basicvlcj.service;

import hu.basicvlcj.model.Word;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A service class that is responsible for communicating with the database.
 */
public class WordService {

    /**
     * A method that saves a {@link hu.basicvlcj.model.Word} to the database
     * @param word the word to be saved
     */
    public void create(Word word) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:file:~/basicvlcj", "sa", "");
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

    /**
     * A method that gets all the {@link hu.basicvlcj.model.Word}s from the database
     * @param filename the filename that the words share
     * @return an array of words that shares the filename
     */
    public List<Word> getAllByFilename(String filename){
        List<Word> wordsList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:file:~/basicvlcj", "sa", "");
             Statement stat = connection.createStatement()) {
            ResultSet rs = stat.executeQuery("SELECT * FROM WORDS WHERE FILENAME=" + "'" + filename + "'");
            while (rs.next()) {
                Word w = new Word();
                setAttributes(w, rs);

                wordsList.add(w);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occurred", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return wordsList;
    }

    /**
     * A method that identifies if a {@link hu.basicvlcj.model.Word} is already stored in the database
     * @param foreignWord a String that the the method compares the database records
     * @return true if it is already stored, false otherwise
     */
    public boolean isAlreadySaved(String foreignWord) {
        List<Word> wordsList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:file:~/basicvlcj", "sa", "");
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

    /**
     * A method that deletes all records from the database
     */
    public void deleteAll() {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:file:~/basicvlcj", "sa", "");
             Statement stat = connection.createStatement()) {
            stat.executeUpdate("DELETE FROM WORDS");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occurred", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * A method that returns all the records from the database
     * @return a list that contains all the {@link hu.basicvlcj.model.Word}s from the database
     */
    public List<Word> getAll() {
        List<Word> wordsList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:file:~/basicvlcj", "sa", "");
             Statement stat = connection.createStatement()) {
            ResultSet rs = stat.executeQuery("SELECT * FROM WORDS");
            while (rs.next()) {
                Word w = new Word();
                setAttributes(w, rs);

                wordsList.add(w);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wordsList;
    }

    private void setAttributes(Word w, ResultSet rs) throws SQLException{
        w.setId(rs.getString(rs.findColumn("WORDS_ID")));
        w.setForeignWord(rs.getString(rs.findColumn("FOREIGN_WORD")));
        w.setMeaning(rs.getString(rs.findColumn("MEANING")));
        w.setExample(rs.getString(rs.findColumn("EXAMPLE")));
        w.setFilename(rs.getString(rs.findColumn("FILENAME")));
    }
}
