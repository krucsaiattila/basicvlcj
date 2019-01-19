package hu.basicvlcj.repository;

import hu.basicvlcj.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, String> {
    public Optional<Word> findByForeignWord(String foreignWord);
    public List<Word> findAllByFilename(String filename);
}
