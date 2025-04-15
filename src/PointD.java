import java.awt.*;

public class PointD {
    public double x;
    public double y;

    public PointD(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public PointD() {
        this(0, 0);
    }

    public PointD(Point point) {
        this(point.x, point.y);
    }

    public PointD clone() {
        return new PointD(x, y);
    }
}