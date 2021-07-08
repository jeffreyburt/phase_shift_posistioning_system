import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GUI {

    private SimPanel simPanel;


    private int nodeDiam = 20;
    private Color selectedNodeColor = Color.green;
    private Color normalNodeColor = Color.blue;

    public GUI(){
        JFrame mainFrame = new JFrame("signal offset");
        JPanel mainPanel = new JPanel(new BorderLayout());
        simPanel = new SimPanel();

        mainPanel.add(simPanel, BorderLayout.CENTER);
        mainFrame.setPreferredSize(new Dimension(Controller.width,Controller.height));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        mainFrame.addMouseListener(simPanel);
        mainFrame.addMouseMotionListener(simPanel);

        Controller.pixelsToMeters = calcPixelDistance(Controller.node1, Controller.node2) / Controller.metersBetweenNodes;
    }

    private double calcPixelDistance(Node node1, Node node2){
        double xDistance = Math.pow(node1.x - node2.x, 2);
        double yDistance = Math.pow(node1.y - node2.y, 2);
        return Math.sqrt(xDistance + yDistance);
    }

    public class SimPanel extends JPanel implements MouseListener, MouseMotionListener {

        private Node selectedNode;
        private final Simulator simulator;


        public SimPanel(){
            //this.setBackground(Color.BLACK);
            setSize(new Dimension(Controller.width, Controller.height));
            simulator = new Simulator(this.getWidth(), this.getHeight(), Controller.node1, Controller.node2);
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            double[][] pixel_array = simulator.pixelArray;
            //todo this may not work

            for (int x = 0; x < pixel_array.length; x++) {
                for (int y = 0; y < pixel_array[x].length; y++) {
                    Color color = new Color((float) pixel_array[x][y],(float) 0.0,(float) 0.0);
                    //drawCenterCircle(x,y,1,g, color);
                    g.setColor(color);
                    g.drawLine(x,y,x,y);
                }
            }



            drawCenterCircle(Controller.node1.x, Controller.node1.y, nodeDiam, g, Controller.node1.color);
            drawCenterCircle(Controller.node2.x, Controller.node2.y, nodeDiam, g, Controller.node2.color);

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            double node1Distance = calcDistance(Controller.node1, e);
            double node2Distance = calcDistance(Controller.node2, e);

            if(node1Distance <= node2Distance){
                selectedNode = Controller.node1;
                selectedNode.color = selectedNodeColor;
            }else {
                selectedNode = Controller.node2;
                selectedNode.color = selectedNodeColor;
            }
            this.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            selectedNode.color = normalNodeColor;
            selectedNode = null;
            simulator.recalculate();
            this.repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            selectedNode.x = e.getX();
            selectedNode.y = e.getY();
            this.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }

    private void drawCenterCircle(double x, double y, int diameterPixels, Graphics g, Color color){
        g.setColor(color);
        g.fillOval((int)(x - ((double) diameterPixels * 0.5)),(int)(y - ((double) diameterPixels * 0.5)), diameterPixels, diameterPixels );
    }

    private double calcDistance(Node node, MouseEvent e) {
        //todo add pixel to meter calibration here
        double xDistance = Math.pow(node.x - e.getX(), 2);
        double yDistance = Math.pow(node.y - e.getY(), 2);
        return Math.sqrt(xDistance + yDistance);
    }

}