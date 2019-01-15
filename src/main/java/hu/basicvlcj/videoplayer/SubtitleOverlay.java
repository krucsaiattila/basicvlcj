package hu.basicvlcj.videoplayer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;

import hu.basicvlcj.srt.SRT;
import hu.basicvlcj.srt.SRTInfo;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class SubtitleOverlay extends Window {

	private static final long serialVersionUID = 1L;
	
	private SRTInfo subtitle;
	private EmbeddedMediaPlayer mediaPlayer;
	private String actSubtitle = "";
	
	// offset from the bottom of the overlay
	private int subtitleYOffset = 20;
	
	public SubtitleOverlay(Window owner, EmbeddedMediaPlayer mediaPlayer) {
        super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
        this.mediaPlayer = mediaPlayer;
        AWTUtilities.setWindowOpaque(this, false);

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
        
        //calculate the center position for X coord
        int stringLen = (int) g2.getFontMetrics().getStringBounds(actSubtitle, g2).getWidth();
        int startX = this.getWidth()/2 - stringLen/2;
        
        int stringHeight = (int) g2.getFontMetrics().getStringBounds(actSubtitle, g2).getHeight();
        int startY = this.getHeight() - stringHeight - subtitleYOffset;
        
        g2.drawString(actSubtitle, startX, startY);
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
}
