package hu.basicvlcj.videoplayer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;

import hu.basicvlcj.srt.SRT;
import hu.basicvlcj.srt.SRTInfo;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class SubtitleOverlay extends Window implements MouseListener {

	private static final long serialVersionUID = 1L;

	private SRTInfo subtitle;
	private EmbeddedMediaPlayer mediaPlayer;
	private List<String> actSubtitle;

	private List<Entry<String, Rectangle2D>> boundingBoxes = new ArrayList<Map.Entry<String, Rectangle2D>>();

	// offset from the bottom of the overlay
	private int subtitleYOffset = 20;
	private int fontSize = 20; // size of the subtitle
	private int lineSpacing = 10; // pixels between lines

	
	public SubtitleOverlay(Window owner, EmbeddedMediaPlayer mediaPlayer) {
		super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
		this.mediaPlayer = mediaPlayer;
		AWTUtilities.setWindowOpaque(this, false);

		this.addMouseListener(this);
		this.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent componentEvent) {
		    	update(true); // force a subtitle update
		    }});

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
			//g2.drawRect((int) box.getValue().getX(), (int) box.getValue().getY(), (int) box.getValue().getWidth(), (int) box.getValue().getHeight());
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

		long time = mediaPlayer.getTime();

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
		
		
		actSubtitle = new ArrayList<String>();
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
			System.out.println(clickedWord);
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