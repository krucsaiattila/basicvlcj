package hu.basicvlcj.model;

import lombok.Data;

/**
 * A class that contains the translations of a given word.
 */
@Data
public class Translation {

    private String normalizedTarget;

    private String displayTarget;

    private String posTag;

    private double confidence;

    private String prefixWord;

    public Translation(String text) {
        normalizedTarget = text;
    }
}
