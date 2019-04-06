package hu.basicvlcj.model;

import lombok.Data;

import java.util.List;

/**
 * A class that represents a response from regular translation
 */
@Data
public class OtherLanguagesResponse {
    private List<OtherLanguageTranslation> translations;
}
