package hu.basicvlcj.translate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.*;

import java.io.IOException;

/**
 * A class that is responsible for the translation. It uses the Microsoft Translate API.
 */
public class Translator {

    private String subscriptionKey = "69be7b5e75ff473492695708eebc332d";
    private String givenLanguageUrl = "https://api.cognitive.microsofttranslator.com/dictionary/lookup?api-version=3.0&from=";
    private String languageDetectionUrl = "https://api.cognitive.microsofttranslator.com/detect?api-version=3.0";

    private OkHttpClient client = new OkHttpClient();

    // This function performs a POST request.
    public TranslateResponse[] PostWithGivenLanguages(String from, String to, String string) throws IOException {
        givenLanguageUrl += from + "&to=";
        givenLanguageUrl += to;
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "[{\n\t\"Text\": \"" + string + "\"\n}]");
        Request request = new Request.Builder()
                .url(givenLanguageUrl).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Content-type", "application/json").build();
        Response response = client.newCall(request).execute();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TranslateResponse[] translateResponse = gson.fromJson(response.body().string(), TranslateResponse[].class);

        return translateResponse;
    }

    //the detected stuff
    public DetectedLanguageResponse[] PostWithLanguageDetection(String string) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "[{\n\t\"Text\": \"" + string + "\"\n}]");
        Request request = new Request.Builder()
                .url(languageDetectionUrl).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Content-type", "application/json").build();
        Response response = client.newCall(request).execute();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        DetectedLanguageResponse[] translateResponse = gson.fromJson(response.body().string(), DetectedLanguageResponse[].class);

        return translateResponse;
    }

}
