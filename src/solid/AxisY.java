package solid;

import transforms.Col;
import transforms.Point3D;

public class AxisY extends Solid {
    public AxisY() {
        color = new Col(0x00ff00);

        vb.add(new Point3D(0.5, 0.5, 0.5));
        vb.add(new Point3D(0.5, 1.5, 0.5));

        ib.add(0);
        ib.add(1);
    }
}
