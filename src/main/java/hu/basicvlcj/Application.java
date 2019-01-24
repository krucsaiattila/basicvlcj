package hu.basicvlcj;

import hu.basicvlcj.service.WordService;
import hu.basicvlcj.videoplayer.TestPlayer;
import hu.basicvlcj.videoplayer.VlcjTest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.LibVlcFactory;

import javax.annotation.PostConstruct;
import javax.swing.*;

/**
 * 
 * Main entry point for the player.
 * It configures the Spring context, initializes the LibVLC, sets the look and feel.
 * It then instantiates a {@link hu.basicvlcj.videoplayer.TestPlayer} which is the main class for the player.
 *
 */
@SpringBootApplication(scanBasePackages = {"hu.basicvlcj"})
public class Application extends VlcjTest {

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
    private void deleteRecordsFromDatabase() {
        WordService wordService = new WordService();
        wordService.deleteAll();
    }
}
