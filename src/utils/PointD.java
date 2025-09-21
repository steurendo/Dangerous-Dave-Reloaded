package utils;

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

    @Override
    public String toString() {
        return "Point (" + x + "; " + y + ")";
    }

    @Override
    public PointD clone() {
        return new PointD(x, y);
    }

    public PointD plus(double x, double y) {
        return new PointD(this.x + x, this.y + y);
    }

    public PointD plus(PointD point) {
        return plus(point.x, point.y);
    }

    public PointD scale(double factor) {
        return new PointD(x * factor, y * factor);
    }
}