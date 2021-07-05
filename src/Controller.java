public class Controller {

    private static GUI gui;

    public static Node node1;
    public static Node node2;

    public static int width = 1500;
    public static int height = 1000;


    public static void main(String[] args){
        gui = new GUI();

        node1 = new Node( width / 5, height / 2);
        node2 = new Node(width - node1.x, node1.y);

    }



}
