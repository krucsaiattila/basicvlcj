package hu.basicvlcj.model;

import lombok.Data;

/**
 * Class that represents the response from the translate API when the language detection is enabled. It is used to deserialize the GSON response object.
 */
@Data
public class DetectedLanguageResponse {

    private String language;

}
