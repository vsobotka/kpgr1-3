package rasterize;

import model.Line;
import model.Point;
import model.Polygon;
import transforms.Col;

import java.awt.*;

public class PolygonRasterizer {
    private LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {
        if(polygon.getSize() < 3) return;

        for(int i = 0; i < polygon.getSize(); i++) {
            int indexA = i;
            int indexB = i + 1;

            if(indexB == polygon.getSize())
                indexB = 0;

            Point pA = polygon.getPoint(indexA);
            Point pB = polygon.getPoint(indexB);

            lineRasterizer.rasterize(new Line(pA, pB), new Col(0xff0000));
        }
    }
}
