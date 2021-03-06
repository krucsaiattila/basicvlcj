package hu.basicvlcj.videoplayer;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;
import hu.basicvlcj.model.DetectedLanguageResponse;
import hu.basicvlcj.model.OtherLanguagesResponse;
import hu.basicvlcj.model.TranslateResponse;
import hu.basicvlcj.model.Word;
import hu.basicvlcj.popupwindow.PopupMessageBuilder;
import hu.basicvlcj.service.TranslatorService;
import hu.basicvlcj.service.WordService;
import hu.basicvlcj.srt.SRT;
import hu.basicvlcj.srt.SRTInfo;
import hu.basicvlcj.translate.LanguageSelectorFrame;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

/**
 * A class that manages the subtitles on the media player.
 */
public class SubtitleOverlay extends Window implements MouseListener {

	private static final long serialVersionUID = 1L;

	private SRTInfo subtitle;
	private EmbeddedMediaPlayer mediaPlayer;
	private List<String> actSubtitle;
	@Setter
	private File actualFile;

	public static long subtitleDelay;

	private List<Entry<String, Rectangle2D>> boundingBoxes = new ArrayList<Map.Entry<String, Rectangle2D>>();

	// offset from the bottom of the overlay
	private int subtitleYOffset = 20;
	public static int fontSize = 20; // size of the subtitle
	private int lineSpacing = 10; // pixels between lines

	private WordService wordsService = new WordService();

	public SubtitleOverlay(Window owner, EmbeddedMediaPlayer mediaPlayer) {
		super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
		AWTUtilities.setWindowOpaque(this, false);

		this.mediaPlayer = mediaPlayer;
		this.addMouseListener(this);
		this.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent componentEvent) {
		    	update(true); // force a subtitle update
		    }});

		subtitleDelay = 0;

		setLayout(null);
	}

	public void increaseYOffset(int d) {
		subtitleYOffset += d;
		update(true);
	}

	public void decreaseYOffset(int d) {
		subtitleYOffset -= d;
		update(true);
	}

	public void increaseFontSize(int d) {
		fontSize += d;
		update(true);
	}

	public void decreaseFontSize(int d) {
		fontSize -= d;
		update(true);
	}

	public void setSRTInfo(SRTInfo info) {
		this.subtitle = info;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		g.clearRect(0, 0, this.getWidth(), this.getHeight());

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setFont(new Font("Serif", Font.PLAIN, fontSize));
		g2.setColor(new Color(255, 255, 255));

		if(actSubtitle == null) {
			return;
		}
		
		calculateSubtitleBoundingBox(g2, actSubtitle);

		// draw all bounding boxes and the subtitle
		for (Entry<String, Rectangle2D> box : boundingBoxes) {
			g2.drawString(box.getKey(), (int)box.getValue().getX(), (int)(box.getValue().getY()+box.getValue().getHeight()));
		}

	}

	private void calculateSubtitleBoundingBox(Graphics g2, List<String> lines) {
		boundingBoxes.clear(); // clear previous bounding boxes
		if(lines == null) {
			return;
		}
		
		// Calculate bounding boxes for each line
		for (int i = 0; i < lines.size(); i++) {
			int stringLen = (int) g2.getFontMetrics().getStringBounds(lines.get(i), g2).getWidth();
			int startX = this.getWidth() / 2 - stringLen / 2;

			// calculate the center position for Y coord
			int stringHeight = (int) g2.getFontMetrics().getStringBounds(lines.get(i), g2).getHeight();
			int startY = this.getHeight() - stringHeight - subtitleYOffset - (lines.size() - i - 1) * (lineSpacing + stringHeight);

			StringBuilder sb = new StringBuilder();

			// calculate bounding box for each word in a subtitle line
			for (String word : lines.get(i).split(" ")) {
				int prevsLen = (int) g2.getFontMetrics().getStringBounds(sb.toString(), g2).getWidth();

				int wordLen = (int) g2.getFontMetrics().getStringBounds(word, g2).getWidth();
				int wordX = startX + prevsLen;

				int wordHeight = stringHeight; // word height is same as the string height
				int wordY = startY;

				boundingBoxes.add(new AbstractMap.SimpleEntry<String, Rectangle2D>(word,
						new Rectangle2D.Double(wordX, wordY, wordLen, wordHeight)));

				sb.append(word).append(" ");
			}

		}

	}

	/**
	 * Updates the subtitle.
	 * @param force: Updates it anyway (even if the subtitle did not changed).
	 */
	public void update(boolean force) {
		mediaPlayer.setSpu(-1);

		//subtitle delay
		long time = mediaPlayer.getTime() + subtitleDelay;

		// no update was needed
		if (!seekActSubtitle(time) && !force) {
			return;
		}
		mediaPlayer.enableOverlay(false);
		this.repaint();
		mediaPlayer.enableOverlay(true);
	}

	/**
	 * 
	 * @param time
	 * @return true if the act subtitle is updated, false if not
	 */
	private boolean seekActSubtitle(long time) {
		List<String> oldSubtitle = actSubtitle;

		if (subtitle == null) {
			return oldSubtitle != actSubtitle;
		}

		for (SRT s : subtitle) {
			if (s.startInMilliseconds <= time && s.endInMilliseconds >= time) {
				actSubtitle = s.lines;
				return oldSubtitle != actSubtitle;
			}
		}
		
		
		actSubtitle = new ArrayList<>();
		actSubtitle.add("");
		
		return oldSubtitle != actSubtitle;

	}

	private String seekWord(Point point) {

		for (Entry<String, Rectangle2D> box : boundingBoxes) {
			if (box.getValue().contains(point)) {
				return box.getKey();
			}
		}

		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		String clickedWord = seekWord(e.getPoint());
		if (clickedWord != null) {

			//saving the clicked word with the translation to the database
            TranslatorService translatorService = new TranslatorService();
			try {
				String parsedWord = clickedWord.replaceAll("[-+.^:,]","");

				TranslateResponse[] response;
				DetectedLanguageResponse[] detectedLanguageResponse;
				OtherLanguagesResponse[] otherLanguagesResponses;
				//language detection
				if (LanguageSelectorFrame.currentFromLanguage.equals("")) {
					//get the language
					detectedLanguageResponse = translatorService.postWithLanguageDetection(parsedWord);

					//if the from and to are the same
					if (detectedLanguageResponse[0].getLanguage().equals(LanguageSelectorFrame.currentToLanguage)) {
						JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Detected language is the same as language to translate. Please set the proper languages.", "Error", JOptionPane.ERROR_MESSAGE);
						return;
						//if from or to is english, then we can provide more accurate translation
					} else if (detectedLanguageResponse[0].getLanguage().equals("en") || LanguageSelectorFrame.currentToLanguage.equals("en")) {
						//now we look up in the dictionary
						response = translatorService.postWithGivenLanguages(detectedLanguageResponse[0].getLanguage(), LanguageSelectorFrame.currentToLanguage, parsedWord);
						//else we just use the regular text translation
					} else {
						otherLanguagesResponses = translatorService.postWithOtherLanguages(LanguageSelectorFrame.currentToLanguage, parsedWord);
						response = new TranslateResponse[1];
						response[0] = new TranslateResponse(parsedWord, otherLanguagesResponses[0].getTranslations().get(0).getText());
					}
				//chosen languages
					//if from or to is english
				} else {
					if (LanguageSelectorFrame.currentToLanguage.equals("en") || LanguageSelectorFrame.currentFromLanguage.equals("en")) {
						response = translatorService.postWithGivenLanguages(LanguageSelectorFrame.currentFromLanguage, LanguageSelectorFrame.currentToLanguage, parsedWord);
						//else we use the regular translation
					} else {
						otherLanguagesResponses = translatorService.postWithOtherLanguages(LanguageSelectorFrame.currentToLanguage, parsedWord);
						response = new TranslateResponse[1];
						response[0] = new TranslateResponse(parsedWord, otherLanguagesResponses[0].getTranslations().get(0).getText());
					}
				}
				Word word = new Word();


				//the original word
				word.setForeignWord(response[0].getNormalizedSource());

				List<String> translations = new ArrayList<>();

				Arrays.asList(response[0].getTranslations()).forEach(translation -> {
					translation.forEach(target -> {
						translations.add(target.getNormalizedTarget());
					});
				});

				word.setMeaning(String.join(", ", translations));
				word.setExample(String.join(" ", actSubtitle));
				word.setFilename(actualFile.getName());

				if (word.getMeaning() != null && !Strings.isBlank(word.getMeaning())) {
					if (!new WordService().isAlreadySaved(word.getForeignWord())) {
						wordsService.create(word);
					}
					new PopupMessageBuilder().at(new Point((int) e.getPoint().getX(), e.getY() - (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.1))).withDelay(3000).withMessage(word.getMeaning()).show();
				} else {
					new PopupMessageBuilder().at(new Point((int) e.getPoint().getX(), e.getY() - (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.1))).withDelay(3000).withMessage("No translations available").show();
				}

			} catch (Exception ex){
				ex.printStackTrace();
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "An unexpected error has occurred", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
