package hu.basicvlcj.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the response from the translate API. It is used to deserialize the GSON response object.
 */
@Data
public class TranslateResponse {

    private String normalizedSource;

    private String displaySource;

    private List<Translation> translations;

    public TranslateResponse(String from, String text) {
        normalizedSource = from;

        translations = new ArrayList<>();
        translations.add(new Translation(text));
    }
}
