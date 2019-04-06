package hu.basicvlcj.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.*;
import hu.basicvlcj.model.DetectedLanguageResponse;
import hu.basicvlcj.model.OtherLanguagesResponse;
import hu.basicvlcj.model.TranslateResponse;

import java.io.IOException;

/**
 * A class that is responsible for the translation. It uses the Microsoft Translate API.
 */
public class TranslatorService {

    private final String subscriptionKey = "69be7b5e75ff473492695708eebc332d";
    private String givenLanguageUrl = "https://api.cognitive.microsofttranslator.com/dictionary/lookup?api-version=3.0&from=";
    private String otherLanguagesUrl = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=";


    private OkHttpClient client = new OkHttpClient();

    /**
     * This function performs a POST request.
     * @param from the language that the text translated from
     * @param to the language that the text is translated to
     * @param string the text to be translated
     * @return a {@link hu.basicvlcj.model.TranslateResponse} array that contains the APIs responses
     * @throws IOException
     */
    public TranslateResponse[] postWithGivenLanguages(String from, String to, String string) throws IOException {
        givenLanguageUrl += from + "&to=";
        givenLanguageUrl += to;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(createResoponse(string, givenLanguageUrl).body().string(), TranslateResponse[].class);
    }

    /**
     *
     * @param string the text to be translated
     * @return a {@link hu.basicvlcj.model.DetectedLanguageResponse} array that contains the APIs responses
     * @throws IOException
     */
    public DetectedLanguageResponse[] postWithLanguageDetection(String string) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(createResoponse(string, "https://api.cognitive.microsofttranslator.com/detect?api-version=3.0")
                .body().string(), DetectedLanguageResponse[].class);
    }

    public OtherLanguagesResponse[] postWithOtherLanguages(String to, String string) throws IOException {
        otherLanguagesUrl += to;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(createResoponse(string, otherLanguagesUrl).body().string(), OtherLanguagesResponse[].class);
    }

    private Response createResoponse(String string, String url) throws IOException{
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "[{\n\t\"Text\": \"" + string + "\"\n}]");
        Request request = new Request.Builder()
                .url(url).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Content-type", "application/json").build();
        return client.newCall(request).execute();
    }

}
