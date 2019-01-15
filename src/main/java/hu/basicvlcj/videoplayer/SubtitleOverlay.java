package hu.basicvlcj.videoplayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;

public class SubtitleOverlay extends Window {

	private static final long serialVersionUID = 1L;
	
	private String actSubtitle;
	
	public SubtitleOverlay(Window owner) {
        super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());

        AWTUtilities.setWindowOpaque(this, false);

        setLayout(null);
        
        actSubtitle = "Subtitle mothafucka";
    }
	
	public String getActSubtitle() {
		return actSubtitle;
	}

	public void setActSubtitle(String actSubtitle) {
		this.actSubtitle = actSubtitle;
		this.repaint();
	}

	@Override
    public void paint(Graphics g) {
        super.paint(g);
        
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        
        g2.setColor(new Color(255, 255, 255));
        g2.drawString(actSubtitle, 100, 100);
    }
}
