import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI {

    private SimPanel simPanel;


    private int nodeDiam = 20;
    private Color selectedNodeColor = Color.green;
    private Color normalNodeColor = Color.blue;

    //how fast/extent of scale changes
    private double scale_multiplier = 0.125;

    //ratio of display pixels to simulator pixels
    private double scale_ratio;

    //width of the scale window in the base coordinate system
    private double scale_window_width;

    //height of the scaled window in the base coordinate system
    private double scale_window_height;

    //top left corner coordinate of the scale window in the base coordinate system
    private double scale_window_origin_x;
    private double scale_window_origin_y;

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
        mainFrame.addMouseWheelListener(simPanel);

        Controller.pixelsToMeters = calcPixelDistance(Controller.node1, Controller.node2) / Controller.metersBetweenNodes;
    }

    private double calcPixelDistance(Node node1, Node node2){
        double xDistance = Math.pow(node1.x - node2.x, 2);
        double yDistance = Math.pow(node1.y - node2.y, 2);
        return Math.sqrt(xDistance + yDistance);
    }

    public class SimPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

        private Node selectedNode;
        private final Simulator simulator;


        public SimPanel(){
            //this.setBackground(Color.BLACK);
            setSize(new Dimension(Controller.width, Controller.height));

            scale_window_height = this.getHeight();
            scale_window_width = this.getWidth();
            scale_window_origin_x = 0;
            scale_window_origin_y = 0;
            scale_ratio = 1;

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
                g.setColor(Color.GREEN);
                g.drawRect((int) scale_window_origin_x, (int) scale_window_origin_y,
                        (int) scale_window_width, (int) scale_window_height);
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

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            System.out.println(e.getPreciseWheelRotation());
        }

        //posative scale factor zooms in
        private void rescale(MouseWheelEvent e){
            double scaleFactor;
            if(e.getPreciseWheelRotation() > 0){
                scaleFactor = (1/e.getPreciseWheelRotation()) * scale_multiplier;
            }else if(e.getPreciseWheelRotation() < 0){
                scaleFactor = ((1/Math.abs(e.getPreciseWheelRotation())) * scale_multiplier) + 1;
            }else {
                scaleFactor = 1;
            }

            //relative scale offset calculations
            double left_relative_offset = e.getX() / this.getWidth();
            double right_relative_offset = 1 - left_relative_offset;

            double top_relative_offset = e.getY()/ this.getWidth();
            double bottom_relative_offset = 1 - top_relative_offset;







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

    /*
    ok so here is the plan for dynamic scaling
    basically before any scaling is done,
    we calculate the offset ratios from the current scaling window
    then we scale/zoom the window to fit the dimensions required by the scroll
    then we allign the window with the previous offset

    alright htis plan needs a lot more fleshing out
    basically I need to hae a clear dileanator between the two artiesian coordinates systems
    however this is made really difficult by the simulator, which will need to
    run all calculations in the base coordinate systems, yet limit those calculations to
    and only display in the scaled coordinate system

    aaaaaa I really need a filly integrated plan for this, so lets try to lay one out
    Once again the first thing to do is calculate the offset in the scaled window, this should be pretty simple task
    everything is in the scaled coordinate system

    next step is to scale the scaled coordinate system, this should actually be fairly simple too




    So that is a strategy that could work, I don't really like doing the multiple
    calculations though
    strat 2 is to just dynamically scales the walls of the window rectangle
    basically this would entail calculating the x/y offset ratio

     */

}