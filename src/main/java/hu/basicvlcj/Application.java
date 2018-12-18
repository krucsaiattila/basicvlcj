package hu.basicvlcj;

import hu.basicvlcj.model.Word;
import hu.basicvlcj.videoplayer.TestPlayer;
import hu.basicvlcj.videoplayer.VlcjTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import hu.basicvlcj.service.WordsService;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.LibVlcFactory;

import javax.annotation.PostConstruct;
import javax.swing.*;

@SpringBootApplication(scanBasePackages = {"hu.basicvlcj.service", "hu.basicvlcj.repositories"})
public class Application extends VlcjTest {

    @Autowired
    private WordsService testService;

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
    }
}
