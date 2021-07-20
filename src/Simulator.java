public class Simulator {

    private double[] node1_xPixelArray;
    private double[] node1_yPixelArray;

    private double[] node2_xPixelArray;
    private double[] node2_yPixelArray;

    public double[][] pixelArray;



    private int width;
    private int height;

    private Node node1;
    private Node node2;

    private int node1_prevX = -1;
    private int node1_prevY =  -1;
    private int node2_prevX = -1;
    private int node2_prevY = -1;

    private final GUI gui;

    public Simulator(int width, int height, Node node1, Node node2, GUI gui){

        pixelArray = new double[width][height];

        this.width = width;
        this.height = height;
        this.node1 = node1;
        this.node2 = node2;
        this.gui = gui;

        node1_xPixelArray = new double[width];
        node1_yPixelArray = new double[height];
        node2_xPixelArray = new double[width];
        node2_yPixelArray = new double[height];

        recalculate();
    }


    //todo this can be slightly optimized to be less shitty
    public void recalculate(){

        for (int i = 0; i < width; i++) {
            double e = gui.scale_window_origin_x + (i/gui.scale_ratio);
            node1_xPixelArray[i] = calc_2D_dist_squared(e, node1.x);
            node2_xPixelArray[i] = calc_2D_dist_squared(e, node2.x);
        }
        for(int i = 0; i < height; i ++){
            double e = gui.scale_window_origin_y + (i/gui.scale_ratio);
            node1_yPixelArray[i] = calc_2D_dist_squared(i, node1.y);
            node2_yPixelArray[i] = calc_2D_dist_squared(i, node2.y);
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixelArray[i][j] = calcOffset(i,j);
            }
        }
    }


    private double calcOffset(int x_point, int y_point){
        double node1_distance = Math.sqrt(node1_xPixelArray[x_point] + node1_yPixelArray[y_point]);
        double node2_distance = Math.sqrt(node2_xPixelArray[x_point] + node2_yPixelArray[y_point]);

        double node1_offset = (node1_distance / Controller.wavelength) % 1;
        double node2_offset = (node2_distance / Controller.wavelength) % 1;

        if(node1_offset > node2_offset){
            node2_offset ++;
        }
        return node2_offset - node1_offset;

    }

    private double calc_2D_dist_squared(double pos, double pos2){
        return Math.pow(pos - pos2, 2);
    }

    private boolean has_moved(int prev_x, int prev_y, Node node){
        if(node.x != prev_x){
            return true;
        }else if(node.y != prev_y){
            return true;
        }
        return false;
    }


}
