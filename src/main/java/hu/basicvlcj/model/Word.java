package hu.basicvlcj.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;


/**
 * Entity class that represents a given word that has been translated to other language.
 */
@Getter
@Setter
@Entity
@Table(name = "WORDS")
public class Word {

    @Column(name = "WORDS_ID")
    @Id
    private String id;

    @NotEmpty
    private String foreignWord;

    @NotEmpty
    private String meaning;

    private String example;

    @NotEmpty
    private String filename;

    @Override
    public boolean equals(Object object){
        if (object == null) {
            return false;
        }

        if (!Word.class.isAssignableFrom(object.getClass())) {
            return false;
        }

        final Word other = (Word)object;

        return this.foreignWord.equals(other.foreignWord)
                && this.meaning.equals(other.meaning)
                && this.example.equals(other.example)
                && this.filename.equals(other.filename);
    }
}
