package hu.basicvlcj.model;

import lombok.Data;

/**
 * A class that represents a regular translations result text and the source language
 */
@Data
public class OtherLanguageTranslation {

    private String text;

    private String to;
}
