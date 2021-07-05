import javax.swing.*;
import java.awt.*;

public class GUI {

    public GUI(){
        JFrame mainFrame = new JFrame("signal offset");
        JPanel simPanel = new SimPanel();

        mainFrame.setPreferredSize(new Dimension(Controller.width,Controller.height));
        mainFrame.add(simPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public class SimPanel extends JPanel{

        public SimPanel(){
            setBackground(Color.BLACK);
        }

        public void paintComponent(Graphics g){
            Graphics2D g2d = (Graphics2D) g;

        }

    }

    private void drawCenterCircle(double x, double y, int diameterPixels, Graphics g, Color color){
        g.setColor(color);
        g.fillOval((int)(x - ((double) diameterPixels * 0.5)),(int)(y - ((double) diameterPixels * 0.5)), diameterPixels, diameterPixels );
    }

}