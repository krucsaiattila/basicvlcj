package hu.basicvlcj;

import hu.basicvlcj.service.WordService;
import hu.basicvlcj.videoplayer.MainPlayer;
import hu.basicvlcj.videoplayer.Vlcj;
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
 * It then instantiates a {@link MainPlayer} which is the main class for the player.
 *
 */
@SpringBootApplication(scanBasePackages = {"hu.basicvlcj"})
public class Application extends Vlcj {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Application.class)
                .headless(false).run(args);

        LibVlc libVlc = LibVlcFactory.factory().create();

        setLookAndFeel();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainPlayer();
            }
        });
    }

    @PostConstruct
    private void deleteRecordsFromDatabase() {
        WordService wordService = new WordService();
        wordService.deleteAll();
    }
}
