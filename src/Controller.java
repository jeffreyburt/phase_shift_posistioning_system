public class Controller {

    private static GUI gui;

    public static Node node1;
    public static Node node2;

    public static int width = 1500;
    public static int height = 1000;

    public static int metersBetweenNodes = 5000;
    public static double wavelengths_between_nodes = 5;
    public static double wavelength = metersBetweenNodes / wavelengths_between_nodes;
    public static double frequency = 299792458 / wavelength; //speed of light divided by wavelength

    //pixel to meter ratio (pixels / meters), multiply by meters to get pixels
    public static double pixelsToMeters;


    public static void main(String[] args){
//        int[] encoderArray = new int[2];
//        for (int i = 0; i < 3; i++) {
//            System.out.println(encoderArray[i]);
//        }

        node1 = new Node( width / 5, height / 2);
        node2 = new Node(width - node1.x, node1.y);

        gui = new GUI();
    }



}
