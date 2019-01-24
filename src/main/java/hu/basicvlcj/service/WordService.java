package hu.basicvlcj.service;

import hu.basicvlcj.model.Word;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A service class that is responsible for communicating with the {@link hu.basicvlcj.repository.WordRepository}.
 */
//@Service
public class WordService {

//    @Autowired
//    WordRepository wordRepository;
//
//    public Word getByName(String foreginWord){
//        Optional<Word> word = wordRepository.findByForeignWord(foreginWord);
//        if(word.isPresent()){
//            return word.get();
//        } else {
//            //TODO
//            return new Word();
//        }
//    }
//
//    public List<Word> getAllByFilename(String filename){
//        return wordRepository.findAllByFilename(filename);
//    }
//
//
//    public List<Word> getAll() {
//        return wordRepository.findAll();
//    }
//
//    public void create(Word word){
//        word.setId(UUID.randomUUID().toString());
//        wordRepository.save(word);
//    }
//
//    public void update(String id, Word word){
//        Optional<Word> optionalWord = wordRepository.findById(id);
//        if(optionalWord.isPresent()){
//            Word finalWord = optionalWord.get();
//            finalWord.setId(word.getId());
//            finalWord.setForeignWord(word.getForeignWord());
//            finalWord.setMeaning(word.getMeaning());
//            finalWord.setExample(word.getExample());
//            wordRepository.save(finalWord);
//        } else {
//            //TODO
//        }
//    }
//
//    public void delete(Word word){
//        wordRepository.delete(word);
//    }

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
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occured", "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occured", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return wordsList;
    }

    public void deleteAll() {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
             Statement stat = connection.createStatement()) {
            stat.executeUpdate("DELETE FROM WORDS");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occured", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
