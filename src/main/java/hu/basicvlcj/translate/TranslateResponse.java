package hu.basicvlcj.translate;

import lombok.Data;

import java.util.List;

@Data
public class TranslateResponse {

    private String normalizedSource;

    private String displaySource;

    private List<Translation> translations;
}
