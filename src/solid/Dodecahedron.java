package solid;

import transforms.Col;
import transforms.Point3D;

import java.awt.*;
import java.util.ArrayList;

public class Dodecahedron extends Solid {
    public Dodecahedron() {
        float phi = (float) ((1.0 + Math.sqrt(5.0)) / 2.0);
        float invPhi = 1.0f / phi;

        color = new Col(Color.MAGENTA.getRGB());

        vb.add(new Point3D(1,1,1));
        vb.add(new Point3D(0,1/phi,phi));
        vb.add(new Point3D(phi,0,1/phi));
        vb.add(new Point3D(1/phi,phi,0));
        vb.add(new Point3D(-1,1,1));
        vb.add(new Point3D(0,-1/phi,phi));
        vb.add(new Point3D(1,-1,1));
        vb.add(new Point3D(phi,0,-1/phi));
        vb.add(new Point3D(1,1,-1));
        vb.add(new Point3D(-1/phi,phi,0));
        vb.add(new Point3D(-phi,0,1/phi));
        vb.add(new Point3D(-1,-1,1));
        vb.add(new Point3D(1/phi,-phi,0));
        vb.add(new Point3D(1,-1,-1));
        vb.add(new Point3D(0,1/phi,-phi));
        vb.add(new Point3D(-1,1,-1));
        vb.add(new Point3D(-1/phi,-phi,0));
        vb.add(new Point3D(-phi,0,-1/phi));
        vb.add(new Point3D(0,-1/phi,-phi));
        vb.add(new Point3D(-1,-1,-1));

        int[] face1 = {0,1,5,6,2};
        int[] face2 = {0,2,7,8,3};
        int[] face3 = {0,3,9,4,1};
        int[] face4 = {1,4,10,11,5};
        int[] face5 = {2,6,12,13,7};
        int[] face6 = {3,8,14,15,9};
        int[] face7 = {5,11,16,12,6};
        int[] face8 = {7,13,18,14,8};
        int[] face9 = {9,15,17,10,4};
        int[] face10 = {19,16,11,10,17};
        int[] face11 = {19,17,15,14,18};
        int[] face12 = {19,18,13,12,16};

        ArrayList<int[]> faces = new ArrayList<>();
        faces.add(face1);
        faces.add(face2);
        faces.add(face3);
        faces.add(face4);
        faces.add(face5);
        faces.add(face6);
        faces.add(face7);
        faces.add(face8);
        faces.add(face9);
        faces.add(face10);
        faces.add(face11);
        faces.add(face12);

        for (int[] face : faces) {
            for (int i = 0; i < face.length; i++) {
                ib.add(face[i]);
                if (i < face1.length - 1) {
                    ib.add(face[i+1]);
                } else {
                    ib.add(face[0]);
                }
            }
        }
    }
}
