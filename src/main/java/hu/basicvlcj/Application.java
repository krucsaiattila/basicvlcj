package hu.basicvlcj;

import com.itextpdf.text.DocumentException;
import hu.basicvlcj.model.Word;
import hu.basicvlcj.service.PDFGeneratorService;
import hu.basicvlcj.service.WordsService;
import hu.basicvlcj.translate.Translator;
import hu.basicvlcj.videoplayer.TestPlayer;
import hu.basicvlcj.videoplayer.VlcjTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.LibVlcFactory;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * 
 * Main entry point for the player.
 * It configures the Spring context, initializes the LibVLC, sets the look and feel.
 * It then instantiates a {@link hu.basicvlcj.videoplayer.TestPlayer} which is the main class for the player.
 *
 */
@SpringBootApplication(scanBasePackages = {"hu.basicvlcj"})
public class Application extends VlcjTest {

    @Autowired
    private WordsService testService;

    @Autowired
    private PDFGeneratorService pdfGeneratorService;

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Application.class)
                .headless(false).run(args);

        LibVlc libVlc = LibVlcFactory.factory().create();

        setLookAndFeel();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TestPlayer();
            }
        });
    }

    @PostConstruct
    private void test(){
        Word word = new Word();
        word.setForeignWord("apple");
        word.setMeaning("alma");
        testService.create(word);

        try {
            pdfGeneratorService.createDictionary("dictionary");
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Translator translateRequest = new Translator();
            List<String> response = translateRequest.Post("hu", "en", "szép");
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
