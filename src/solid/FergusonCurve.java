package solid;

import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

import java.util.List;

public class FergusonCurve extends Solid {
    Solid parent;

    public FergusonCurve(Dodecahedron parent) {
        color = parent.getColor();

        this.parent = parent;
        List<Point3D> parentVb = parent.getVb();

        Point3D p0 = parentVb.get(0);
        Point3D p1 = parentVb.get(19);
        Point3D t0 = new Point3D(-2, -2, 2);
        Point3D t1 = new Point3D(-2, -2, 2);

        Cubic cubic = new Cubic(Cubic.FERGUSON, p0, p1, t0, t1);

        int segments = 30;
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
