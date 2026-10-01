import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Input {

  private boolean leftMouseDown = false;

  public MouseListener mouseListener = new MouseListener() {

    @Override
    public void mousePressed(MouseEvent event) {
      //Check to see if left mouse button is pressed and set leftMouseDown to true
      if (event.getButton() == MouseEvent.BUTTON1) {
        leftMouseDown = true;
      }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
      //Check to see if left mouse button is released and set leftMouseDown to false
      if (event.getButton() == MouseEvent.BUTTON1) {
        leftMouseDown = false;
      }
    }

    //unused overrides
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
  };

  public boolean isLeftMouseDown(){
    return leftMouseDown;
  }
}