import java.util.Comparator;

class Cmp implements Comparator<Vertex> {
    private Vertex baseP;

    Cmp(Vertex baseP) {
        this.baseP = baseP;
    }

    @Override
    public int compare(Vertex o1, Vertex o2) {
        double temp = (o1.getX() - baseP.getX()) * (o2.getY() - baseP.getY())
                - (o1.getX() - baseP.getX()) * (o2.getX() - baseP.getX());
        if (temp == 0)
            if (o1.getX() >= Math.min(baseP.getX(), o2.getX())
                    && o1.getX() <= Math.max(baseP.getX(), o2.getX()))
                return 1;
            else
                return -1;
        else if (temp > 0)
            return 1;
        else
            return -1;
    }
}
    