import java.util.ArrayList;

public class Vertex {
    private double x;
    private double y;
//    private double degree;
//    private double distanceToO;

    Vertex(){
        x =0;
        y=0;
//        degree = 0;
//        distanceToO = 0;
    }

    Vertex(double x,double y){
        this.x = x;
        this.y = y;
//        if(x == 0&&y==0)
//            degree = 0;
//        else if(x == 0&&y>0)
//            degree = Math.PI/2;
//        else if(x == 0&&y<0)
//            degree = -Math.PI/2;
//        else if(x>0&&y==0)
//            degree = 0;
//        else if(x<0&&y==0)
//            degree = Math.PI;
//        else
//            degree = Math.atan(y/x);

    }

    public double getX() {
        return x;
    }

    public double getY(){
        return y;
    }

    //    boolean compare(Vertex b){
//        if(degree < b.degree)
//            return false;
//        if(degree > b.degree)
//            return true;
//        if(degree == b.degree){
//            if()
//        }
//    }
}
