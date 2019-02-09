package hu.basicvlcj.service;

import hu.basicvlcj.model.Word;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WordServiceTest {

    private WordService testObj;


    @BeforeEach
    public void init() {
        testObj = new WordService();
    }

    @Test
    @DisplayName("Should test creating word")
    public void should_test_creating_word() {

        Word w = new Word();
        w.setForeignWord("Test");
        w.setMeaning("Teszt");
        w.setExample("This is a test.");

        testObj.create(w);

        Assertions.assertEquals(w, getAll());
    }

    private List<Word> getAll() {
        List<Word> wordsList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
             Statement stat = connection.createStatement()) {
            ResultSet rs = stat.executeQuery("SELECT * FROM WORDS");
            while (rs.next()) {
                Word w = new Word();
                w.setForeignWord(rs.getString(rs.findColumn("FOREIGN_WORD")));
                w.setMeaning(rs.getString(rs.findColumn("MEANING")));
                w.setExample(rs.getString(rs.findColumn("EXAMPLE")));

                wordsList.add(w);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wordsList;
    }
}
