package solid;

import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

import java.util.List;

public class CoonsCurve extends Solid {
    Solid parent;

    public CoonsCurve(PentagonalPrism parent) {
        color = parent.getColor();

        this.parent = parent;

        List<Point3D> parentVb = parent.getVb();

        Point3D p0 = parentVb.get(0);
        Point3D p1 = parentVb.get(5);
        Point3D p2 = parentVb.get(2);
        Point3D p3 = parentVb.get(7);

        Cubic cubic = new Cubic(Cubic.COONS, p0, p1, p2, p3);

        int segments = 30;
        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments;
            vb.add(cubic.compute(t));
        }

        // Connect consecutive points with lines
        for (int i = 0; i < segments; i++) {
            ib.add(i);
            ib.add(i + 1);
        }
    }

    @Override
    public Mat4 getModel() {
        return parent.getModel();
    }
}