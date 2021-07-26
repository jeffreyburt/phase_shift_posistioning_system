import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI {

    private SimPanel simPanel;


    private int nodeDiam = 20;
    private Color selectedNodeColor = Color.green;
    private Color normalNodeColor = Color.blue;
    private Coordinate prev_mouse_cord;

    //how fast/extent of scale changes
    private double scale_step = 0.025;

    //ratio of display pixels to simulator pixels
    //e.g. a 2x zoom will have 2 scale pixels per simulator pixel and a scale ratio of 2
    public double scale_ratio;

    //width of the scale window in the base coordinate system
    public double scale_window_width;

    //height of the scaled window in the base coordinate system
    public double scale_window_height;

    //top left corner coordinate of the scale window in the base coordinate system
    public double scale_window_origin_x;
    public double scale_window_origin_y;

    public GUI(){
        JFrame mainFrame = new JFrame("signal offset");
        JPanel mainPanel = new JPanel(new BorderLayout());
        simPanel = new SimPanel(this);

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


        public SimPanel(GUI gui){
            //this.setBackground(Color.BLACK);
            setSize(new Dimension(Controller.width, Controller.height));

            scale_window_height = this.getHeight();
            scale_window_width = this.getWidth();
            scale_window_origin_x = 0;
            scale_window_origin_y = 0;
            scale_ratio = 1;

            simulator = new Simulator(this.getWidth(), this.getHeight(), Controller.node1, Controller.node2, gui);

        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;


            double[][] pixel_array = simulator.pixelArray;
            //todo this may not work

            for (int x = 0; x < pixel_array.length; x++) {
                g2d.setStroke(new BasicStroke(1));
                for (int y = 0; y < pixel_array[x].length; y++) {
                    Color color = new Color((float) pixel_array[x][y],(float) 0.0,(float) 0.0);
                    //drawCenterCircle(x,y,1,g, color);
                    g2d.setColor(color);
                    g2d.drawLine(x,y,x,y);
                }
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(Color.GREEN);
                g2d.drawRect((int) scale_window_origin_x, (int) scale_window_origin_y,
                        (int) scale_window_width, (int) scale_window_height);
            }


            Coordinate node_1_coordinate = convert_to_scaled_cords(new Coordinate(Controller.node1));
            Coordinate node_2_coordinate = convert_to_scaled_cords(new Coordinate(Controller.node2));

            Coordinate test_cord = convert_to_scaled_cords(new Coordinate(750,500));
            drawCenterCircle(test_cord.x, test_cord.y,nodeDiam, g, Color.GREEN);

            drawCenterCircle(node_1_coordinate.x, node_1_coordinate.y, nodeDiam, g, Controller.node1.color);
            drawCenterCircle(node_2_coordinate.x, node_2_coordinate.y, nodeDiam, g, Controller.node2.color);


            System.out.println();
            System.out.println(new Coordinate(Controller.node1 ));
            System.out.println(node_1_coordinate);

        }

        private Coordinate convert_to_scaled_cords(Coordinate sim_coordinate){
            double x = (sim_coordinate.x - scale_window_origin_x) * scale_ratio;
            double y = (sim_coordinate.y - scale_window_origin_y) * scale_ratio;

            return new Coordinate(x,y);
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e)) {
                double node1Distance = calcDistance(Controller.node1, e);
                double node2Distance = calcDistance(Controller.node2, e);

                if (node1Distance <= node2Distance) {
                    selectedNode = Controller.node1;
                } else {
                    selectedNode = Controller.node2;
                }
                selectedNode.color = selectedNodeColor;
                this.repaint();
            } else {
              prev_mouse_cord = new Coordinate(e);
              System.out.println("right click!");
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                selectedNode.color = normalNodeColor;
                selectedNode = null;
                simulator.recalculate();
                this.repaint();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e)) {
                selectedNode.x = e.getX();
                selectedNode.y = e.getY();
            }else {
                Coordinate new_mouse_cord = new Coordinate(e);
                double change_x = (new_mouse_cord.x - prev_mouse_cord.x) / scale_ratio;
                scale_window_origin_x -= change_x;

                double change_y = (new_mouse_cord.y - prev_mouse_cord.y) / scale_ratio;
                scale_window_origin_y -= change_y;

                System.out.println("Scale ratio (from panning) " + scale_ratio);
                simulator.recalculate();
            }
            this.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            System.out.println(e.getPreciseWheelRotation());
            rescale(e);
            simulator.recalculate();
            this.repaint();
        }

        //positive scale factor zooms in
        private void rescale(MouseWheelEvent e){
            double scaleFactor;
            if(e.getPreciseWheelRotation() > 0){
                //zooming out
                scaleFactor = 1 + scale_step;
            }else if(e.getPreciseWheelRotation() < 0){
                //zooming in
                scaleFactor = 1 - scale_step;
            }else {
                //error catch: really shouldn't happen
                scaleFactor = 1;
            }

            //calculating the location of the mouse in the simulator coordinate grid
            Coordinate simulator_mouse_cord = convert_cord_to_sim(new Coordinate(e));


            scale_window_height *= scaleFactor;
            scale_window_width *= scaleFactor;
            scale_ratio /= scaleFactor;
            System.out.println("Scale factor: " + scale_ratio);
            System.out.println("Scaled Width: " + scale_window_width);

            //calculating the new location of the mouse after the window has been scaled
            Coordinate new_mouse_location = convert_cord_to_sim(new Coordinate(e));

            double x_offset = new_mouse_location.x - simulator_mouse_cord.x;
            double y_offset = new_mouse_location.y - simulator_mouse_cord.y;

            scale_window_origin_x -= x_offset;
            scale_window_origin_y -= y_offset;



            //new position calculations
            //note this is made difficult by all mouse positions being in the scaled cord system
            /*
            so just to repeat this process to myself again, I need to move the scaled window so that the mouse stays in the same
            relative position in the scaled window. In other words, the mouse or window can appear to move at all
            in order to do this I need the mouse's position in the simulator coordinate space
            once I have that I can do some semi fancy math to move the zoomed window so that
            the mouse's coordinate in the zoomed windows stays at the mouse's coordinate in the simulator window

            Order of operations:
            record mice coordinate in the scaled window
            convert that saved Coordinate to a coordinate in the simulator cord system
            TODO this should be done before scaling done above


             */





        }

        private Coordinate convert_cord_to_sim(Coordinate scaled_cord){
            double x_cord = (scaled_cord.x / scale_ratio) + scale_window_origin_x;
            double y_cord = (scaled_cord.y / scale_ratio) + scale_window_origin_y;

            return new Coordinate(x_cord, y_cord);
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