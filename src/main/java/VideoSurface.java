import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class VideoSurface extends Canvas implements MouseListener {

    @Override
    public void paint(Graphics graphics){
        super.paint(graphics);
        graphics.setColor(Color.CYAN);
        graphics.drawString("ASDASDAS", 0  ,100);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("ASDASD");
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
}
