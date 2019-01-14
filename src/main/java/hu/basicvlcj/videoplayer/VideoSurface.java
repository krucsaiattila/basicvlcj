package hu.basicvlcj.videoplayer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class VideoSurface extends Canvas implements MouseListener {

	private static final long serialVersionUID = 313711816457114840L;

	private Map<String, Rectangle2D> subtitleMap = new HashMap<>();

    private String tmp = "TMP";

    @Override
    public void paint(Graphics graphics){
        super.paint(graphics);
        graphics.setColor(Color.WHITE);
        graphics.drawString(tmp, 0  ,100);

        addMouseListener(this);
//        super.paint(graphics);
//        graphics.setColor(Color.CYAN);
//        FontMetrics fm = graphics.getFontMetrics();
//        int h = fm.getHeight();
//        int w  = fm.stringWidth("ASDASDAS");
//        graphics.drawString(tmp, 0  ,100);
//        addMouseListener(this);
    }

    public void drawSubtitles(String subtitles){
        tmp = subtitles;
        paint(getGraphics());
        //getGraphics().drawString(subtitles, 150, getWidth()/2);
        Rectangle2D r = getGraphics().getFontMetrics().getStringBounds(subtitles, getGraphics());
        subtitles = subtitles.replace(",", "");
        subtitles = subtitles.replace(".", "");
        subtitles = subtitles.replace("!", "");
        subtitles = subtitles.replace("?", "");

        //subtitleMap.put(subtitles, r);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        System.out.println(subtitleMap.toString());
//        for (Map.Entry<String,Rectangle2D> pair : subtitleMap.entrySet()){
//            if(((Rectangle2D)pair.getValue()).contains(e.getPoint())){
//                System.out.println(pair.getKey().toString());
//            }
//        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void clearSubtitleMap(){
        subtitleMap.clear();
    }
}
