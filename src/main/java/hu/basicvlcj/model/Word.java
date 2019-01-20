package hu.basicvlcj.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;


/**
 * Entity class that represents a given word that has been translated to other language.
 */
@Data
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
}
