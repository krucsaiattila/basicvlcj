package hu.basicvlcj.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "WORDS")
public class Word {

    @Column(name = "WORDS_ID")
    @Id
    String id;

    @NotEmpty
    String foreignWord;

    @NotEmpty
    String meaning;

    String example;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getForeignWord() {
        return foreignWord;
    }

    public void setForeignWord(String foreignWord) {
        this.foreignWord = foreignWord;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
