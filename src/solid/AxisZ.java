package solid;

import transforms.Col;
import transforms.Point3D;

public class AxisZ extends Solid {
    public AxisZ() {
        color = new Col(0x0000ff);

        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(0, 0, 1));

        ib.add(0);
        ib.add(1);
    }
}
