package solid;

import transforms.Col;
import transforms.Mat4RotX;
import transforms.Mat4RotY;
import transforms.Point3D;

import java.awt.*;

public class PentagonalPrism extends Solid {
    public PentagonalPrism() {
        color = new Col(Color.CYAN.getRGB());

        int sides = 5;
        double radius = 0.25;
        double halfHeight = 1.0;

        // Bottom pentagon vertices (0-4)
        for (int i = 0; i < sides; i++) {
            double angle = 2 * Math.PI * i / sides;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            vb.add(new Point3D(x, y, -halfHeight));
        }

        // Top pentagon vertices (5-9)
        for (int i = 0; i < sides; i++) {
            double angle = 2 * Math.PI * i / sides;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            vb.add(new Point3D(x, y, halfHeight));
        }

        // Bottom pentagon edges
        for (int i = 0; i < sides; i++) {
            ib.add(i);
            ib.add((i + 1) % sides);
        }

        // Top pentagon edges
        for (int i = 0; i < sides; i++) {
            ib.add(sides + i);
            ib.add(sides + (i + 1) % sides);
        }

        // Vertical edges connecting top and bottom
        for (int i = 0; i < sides; i++) {
            ib.add(i);
            ib.add(sides + i);
        }

        // Tilt the prism ~45 degrees on both X and Y axes for interesting rotation
        model = new Mat4RotX(Math.toRadians(45)).mul(new Mat4RotY(Math.toRadians(30)));
    }
}