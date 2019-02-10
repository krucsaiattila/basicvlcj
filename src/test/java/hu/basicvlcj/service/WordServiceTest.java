package hu.basicvlcj.service;

import hu.basicvlcj.model.Word;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordServiceTest {

    private WordService testObj;

    private Word w;
    private Word w2;
    private Word w3;


    @BeforeEach
    void init(){
        testObj = new WordService();

        testObj.deleteAll();

        w = new Word();
        w.setForeignWord("Test");
        w.setMeaning("Teszt");
        w.setExample("This is a test.");
        w.setFilename("test.file");

        w2 = new Word();
        w2.setForeignWord("Test2");
        w2.setMeaning("Teszt2");
        w2.setExample("This is another test.");
        w2.setFilename("test.file");

        w3 = new Word();
        w3.setForeignWord("Test3");
        w3.setMeaning("Teszt3");
        w3.setExample("More tests.");
        w3.setFilename("another.file");
    }

    @AfterEach
    void delete(){
        testObj.deleteAll();
    }

    @Test
    @DisplayName("Should test creating word")
    void should_test_creating_word() {
        testObj.create(w);

        Word createdWord = testObj.getAll().get(0);

        assertEquals(w, createdWord);
    }

    @Test
    @DisplayName("Should test getting all words by filename.")
    void should_test_getting_all_words_by_filename(){
        testObj.create(w);
        testObj.create(w2);
        testObj.create(w3);

        List<Word> wordList = testObj.getAllByFilename("test.file");

        assertAll(
                () -> assertEquals(2, wordList.size()),
                () -> assertTrue(wordList.contains(w)),
                () -> assertTrue(wordList.contains(w2))
        );
    }

    @Test
    @DisplayName("Should test is already saved word method")
    void should_test_is_already_saved_method(){
        testObj.create(w);

        assertAll(
                () -> assertTrue(testObj.isAlreadySaved(w.getForeignWord())),
                () -> assertFalse(testObj.isAlreadySaved(w2.getForeignWord()))
        );
    }

    @Test
    @DisplayName("Should test get all method")
    void should_test_get_all_method(){
        testObj.create(w);
        testObj.create(w2);
        testObj.create(w3);

        List<Word> wordList = testObj.getAll();

        assertAll(
                () -> assertEquals(3, wordList.size()),
                () -> assertTrue(wordList.contains(w)),
                () -> assertTrue(wordList.contains(w2)),
                () -> assertTrue(wordList.contains(w3))
        );
    }


}
