package hu.basicvlcj.translate;

import lombok.Data;

@Data
public class Translation {

    private String normalizedTarget;

    private String displayTarget;

    private String posTag;

    private double confidence;

    private String prefixWord;
}
