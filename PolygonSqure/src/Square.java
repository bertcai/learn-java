public class Square {

    //triangle OAB's square,O--(0,0)
    public double triangleSquare(Vertex a,Vertex b){
        double x1 = a.getX(), x2 = b.getX(),y1 = a.getY(),y2 =b.getY();
        return 0.5*(x1*y2 - x2*y1);
    }
    public static void main(String[] args){
        Vertex[] polygon = new Vertex[5];
        Square s = new Square();

        //must int sorted by clockwise or anticlockwise
        polygon[0] = new Vertex(2,3);
        polygon[1] = new Vertex(4,3);
        polygon[2] = new Vertex(3,2);
        polygon[3] = new Vertex(4,1);
        polygon[4] = new Vertex(2,1);

        double square = 0;
        for(int i = 0;i<4;i++){
//            double temp = s.triangleSquare(polygon[i],polygon[i+1]);
            square += s.triangleSquare(polygon[i],polygon[i+1]);
        }
        square += s.triangleSquare(polygon[4],polygon[0]);
        square = Math.abs(square);
        System.out.println(square);
    }
}
