package hu.basicvlcj.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

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
}
