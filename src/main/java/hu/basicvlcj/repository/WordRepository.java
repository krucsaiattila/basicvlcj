package hu.basicvlcj.repository;

import hu.basicvlcj.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Class that is responsible for the database operations.
 */
@Repository
public interface WordRepository extends JpaRepository<Word, String> {
    Optional<Word> findByForeignWord(String foreignWord);
    List<Word> findAllByFilename(String filename);
}
