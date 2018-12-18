package hu.basicvlcj.repositories;

import hu.basicvlcj.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordsRepository extends JpaRepository<Word, String> {
    public Optional<Word> findByForeignWord(String foreignWord);
}
