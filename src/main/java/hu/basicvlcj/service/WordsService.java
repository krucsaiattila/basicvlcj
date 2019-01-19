package hu.basicvlcj.service;

import hu.basicvlcj.model.Word;
import hu.basicvlcj.repository.WordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WordsService {

    @Autowired
    WordsRepository wordsRepository;

    public Word getByName(String foreginWord){
        Optional<Word> word = wordsRepository.findByForeignWord(foreginWord);
        if(word.isPresent()){
            return word.get();
        } else {
            //TODO
            return new Word();
        }
    }

    public List<Word> getAll() {
        return wordsRepository.findAll();
    }

    public void create(Word word){
        word.setId(UUID.randomUUID().toString());
        wordsRepository.save(word);
    }

    public void update(String id, Word word){
        Optional<Word> optionalWord = wordsRepository.findById(id);
        if(optionalWord.isPresent()){
            Word finalWord = optionalWord.get();
            finalWord.setId(word.getId());
            finalWord.setForeignWord(word.getForeignWord());
            finalWord.setMeaning(word.getMeaning());
            finalWord.setExample(word.getExample());
            wordsRepository.save(finalWord);
        } else {
            //TODO
        }
    }

    public void delete(Word word){
        wordsRepository.delete(word);
    }
}
