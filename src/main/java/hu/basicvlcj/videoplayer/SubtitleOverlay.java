package hu.basicvlcj.videoplayer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
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

public class SubtitleOverlay extends Window implements MouseListener{

	private static final long serialVersionUID = 1L;
	
	private SRTInfo subtitle;
	private EmbeddedMediaPlayer mediaPlayer;
	private String actSubtitle = "";
	
	private Rectangle2D actSubtitleBoundingBox;
	private List<Entry<String, Rectangle2D>> boundingBoxes = new ArrayList<Map.Entry<String,Rectangle2D>>();
	
	// offset from the bottom of the overlay
	private int subtitleYOffset = 20;
	
	public SubtitleOverlay(Window owner, EmbeddedMediaPlayer mediaPlayer) {
        super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
        this.mediaPlayer = mediaPlayer;
        AWTUtilities.setWindowOpaque(this, false);

        this.addMouseListener(this);
        
        setLayout(null);
    }
	
	public void setSRTInfo(SRTInfo info) {
		this.subtitle = info;
	}
	
	@Override
    public void paint(Graphics g) {
        super.paint(g);
        
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(new Font("Serif", Font.PLAIN, 16)); 
        g2.setColor(new Color(255, 255, 255));
        
        
        Rectangle2D rect = calculateSubtitleBoundingBox(g2, actSubtitle);
        actSubtitleBoundingBox = rect;
        
        g2.drawString(actSubtitle, (int)rect.getX(), (int)(rect.getY()+rect.getHeight()));
        g2.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
        
        //draw all bounding boxes
        for(Entry<String, Rectangle2D> box : boundingBoxes) {
        	g2.drawRect((int)box.getValue().getX(), (int)box.getValue().getY(), (int)box.getValue().getWidth(), (int)box.getValue().getHeight());
        }
        
    }

	private Rectangle2D calculateSubtitleBoundingBox(Graphics g2, String s) {
		int stringLen = (int) g2.getFontMetrics().getStringBounds(s, g2).getWidth();
        int startX = this.getWidth()/2 - stringLen/2;
        
        //calculate the center position for Y coord
        int stringHeight = (int) g2.getFontMetrics().getStringBounds(s, g2).getHeight();
        int startY = this.getHeight() - stringHeight - subtitleYOffset;
		
        //calculate bounding box for each word
        StringBuilder sb = new StringBuilder();
        boundingBoxes.clear();
        for(String word : s.split(" ")) { //split on spaces
        	
        	int prevsLen = (int) g2.getFontMetrics().getStringBounds(sb.toString(), g2).getWidth();
        	
        	int wordLen = (int) g2.getFontMetrics().getStringBounds(word, g2).getWidth();
            int wordX = startX + prevsLen;
            
            int wordHeight = stringHeight; //word height is same as the string height
            int wordY = startY;
            
            boundingBoxes.add(new AbstractMap.SimpleEntry<String, Rectangle2D>(word, new Rectangle2D.Double(wordX, wordY, wordLen, wordHeight)));
            
            sb.append(word).append(" ");
        }
        
        
        // this is the overall bounding box for the whole subtitle
        return new Rectangle2D.Double(startX, startY, stringLen, stringHeight);
		
	}

	public void update() {
		mediaPlayer.setSpu(-1);
		
		long time = mediaPlayer.getTime();
		
		// no update was needed
		if(!seekActSubtitle(time)) {
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
		String oldSubtitle = actSubtitle;
		
		if(subtitle == null) {
			actSubtitle = "";
			return  !oldSubtitle.equals(actSubtitle);
		}
		
		for(SRT s : subtitle) {
			if(s.startInMilliseconds <= time && s.endInMilliseconds >= time) {
				actSubtitle = String.join(" ", s.lines);
				return !oldSubtitle.equals(actSubtitle);
			}
		}
		
		actSubtitle = "";
		return !oldSubtitle.equals(actSubtitle);
		
	}

	private String seekWord(Point point) {
		
		for(Entry<String, Rectangle2D> box : boundingBoxes) {
			if(box.getValue().contains(point)) {
				return box.getKey();
			}
		}

		return null;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(actSubtitleBoundingBox.contains(e.getPoint())) {
			String clickedWord = seekWord(e.getPoint());
			if(clickedWord != null) {
				System.out.println(clickedWord);
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
