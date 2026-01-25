package solid;

import transforms.Point3D;

public class Cube extends Solid {
    public Cube() {
        vb.add(new Point3D(-0.5, -0.5, -0.5));
        vb.add(new Point3D(0.5, -0.5, -0.5));
        vb.add(new Point3D(0.5, -0.5, 0.5));
        vb.add(new Point3D(-0.5, -0.5, 0.5));

        vb.add(new Point3D(-0.5, 0.5, -0.5));
        vb.add(new Point3D(0.5, 0.5, -0.5));
        vb.add(new Point3D(0.5, 0.5, 0.5));
        vb.add(new Point3D(-0.5, 0.5, 0.5));

        ib.add(0);
        ib.add(1);

        ib.add(1);
        ib.add(2);

        ib.add(2);
        ib.add(3);

        ib.add(3);
        ib.add(0);

        ib.add(4);
        ib.add(5);

        ib.add(5);
        ib.add(6);

        ib.add(6);
        ib.add(7);

        ib.add(7);
        ib.add(4);

        ib.add(0);
        ib.add(4);

        ib.add(1);
        ib.add(5);

        ib.add(2);
        ib.add(6);

        ib.add(3);
        ib.add(7);
    }
}
