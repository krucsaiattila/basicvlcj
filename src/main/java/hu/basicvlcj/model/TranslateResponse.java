package hu.basicvlcj.model;

import lombok.Data;

import java.util.List;

/**
 * Class that represents the response from the translate API. It is used to deserialize the GSON response object.
 */
@Data
public class TranslateResponse {

    private String normalizedSource;

    private String displaySource;

    private List<Translation> translations;
}
