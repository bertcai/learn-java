import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Square {

    //triangle OAB's square,O--(0,0)
    public double triangleSquare(Vertex a, Vertex b) {
        double x1 = a.getX(), x2 = b.getX(), y1 = a.getY(), y2 = b.getY();
        return 0.5 * (x1 * y2 - x2 * y1);
    }

    public static void main(String[] args) {
        ArrayList<Vertex> polygonVertex = new ArrayList<>();
        Square s = new Square();

        //must int sorted by clockwise or anticlockwise
        polygonVertex.add(new Vertex(2, 3));
        polygonVertex.add(new Vertex(3, 2));
        polygonVertex.add(new Vertex(4, 3));
        polygonVertex.add(new Vertex(2, 1));
        polygonVertex.add(new Vertex(4, 1));

        polygonVertex.sort(new Cmp(polygonVertex.get(0)));

        double square = 0;
        for (int i = 0; i < 4; i++) {
//            double temp = s.triangleSquare(polygonVertex[i],polygonVertex[i+1]);
            square += s.triangleSquare(polygonVertex.get(i), polygonVertex.get(i + 1));
        }
        square += s.triangleSquare(polygonVertex.get(4), polygonVertex.get(0));
        square = Math.abs(square);
        System.out.println(square);
    }
}
