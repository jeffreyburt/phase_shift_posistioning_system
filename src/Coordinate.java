import java.awt.event.MouseEvent;

public class Coordinate {
    public double x;
    public double y;

    public Coordinate(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Coordinate(MouseEvent e){
        x = e.getX();
        y = e.getY();
    }

    public Coordinate(Node node){
        x = node.x;
        y = node.y;
    }

    public String toString(){
        return "("+ (int) x +","+ (int) y + ")";
    }
}
