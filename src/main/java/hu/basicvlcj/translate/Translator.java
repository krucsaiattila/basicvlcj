package hu.basicvlcj.translate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Translator {

    String subscriptionKey = "69be7b5e75ff473492695708eebc332d";
    String url = "https://api.cognitive.microsofttranslator.com/dictionary/lookup?api-version=3.0&from=";

    OkHttpClient client = new OkHttpClient();

    // This function performs a POST request.
    public TranslateResponse[] Post(String from, String to, String string) throws IOException {
        List<String> meanings = new ArrayList<>();

        url += from + "&to=";
        url += to;
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "[{\n\t\"Text\": \"" + string + "\"\n}]");
        Request request = new Request.Builder()
                .url(url).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Content-type", "application/json").build();
        Response response = client.newCall(request).execute();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TranslateResponse[] translateResponse = gson.fromJson(response.body().string(), TranslateResponse[].class);

        return translateResponse;
    }
}
