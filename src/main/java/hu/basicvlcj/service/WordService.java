package hu.basicvlcj.service;

import hu.basicvlcj.model.Word;
import hu.basicvlcj.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WordService {

    @Autowired
    WordRepository wordRepository;

    public Word getByName(String foreginWord){
        Optional<Word> word = wordRepository.findByForeignWord(foreginWord);
        if(word.isPresent()){
            return word.get();
        } else {
            //TODO
            return new Word();
        }
    }

    public List<Word> getAllByFilename(String filename){
        return wordRepository.findAllByFilename(filename);
    }


    public List<Word> getAll() {
        return wordRepository.findAll();
    }

    public void create(Word word){
        word.setId(UUID.randomUUID().toString());
        wordRepository.save(word);
    }

    public void update(String id, Word word){
        Optional<Word> optionalWord = wordRepository.findById(id);
        if(optionalWord.isPresent()){
            Word finalWord = optionalWord.get();
            finalWord.setId(word.getId());
            finalWord.setForeignWord(word.getForeignWord());
            finalWord.setMeaning(word.getMeaning());
            finalWord.setExample(word.getExample());
            wordRepository.save(finalWord);
        } else {
            //TODO
        }
    }

    public void delete(Word word){
        wordRepository.delete(word);
    }
}
