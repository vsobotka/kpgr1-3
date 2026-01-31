package solid;

import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

import java.util.List;


public class BezierCurve extends Solid {
    Solid parent;

    public BezierCurve(Cube parent) {
        this.parent = parent;

        color = parent.getColor();

        List<Point3D> cubeVb = parent.getVb();

        Point3D p0 = cubeVb.get(0);
        Point3D p1 = cubeVb.get(4);
        Point3D p2 = cubeVb.get(2);
        Point3D p3 = cubeVb.get(6);

        Cubic cubic = new Cubic(Cubic.BEZIER, p0, p1, p2, p3);

        int segments = 20;
        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments;
            vb.add(cubic.compute(t));
        }

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