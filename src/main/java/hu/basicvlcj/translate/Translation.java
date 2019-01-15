package hu.basicvlcj.translate;

import lombok.Data;

@Data
public class Translation {

    private String normalizedTarget;

    private String displayTarget;

    private String postag;

    private double confidence;

    private String prefixWord;
}
