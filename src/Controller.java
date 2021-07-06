public class Controller {

    private static GUI gui;

    public static Node node1;
    public static Node node2;

    public static int width = 1500;
    public static int height = 1000;

    public static int metersBetweenNodes = 500;

    //pixel to meter ratio (pixels / meters), multiply by meters to get pixels
    public static double pixelsToMeters;


    public static void main(String[] args){
        gui = new GUI();

        node1 = new Node( width / 5, height / 2);
        node2 = new Node(width - node1.x, node1.y);

    }



}
