package rasterize;

import model.Line;
import raster.RasterBufferedImage;
import transforms.Col;

import java.awt.*;

public abstract class LineRasterizer {
    protected RasterBufferedImage raster;

    public LineRasterizer(RasterBufferedImage raster) {
        this.raster = raster;
    }

    public void rasterize(Line line, Col color) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2(), color);
    }

    public void rasterize(int x1, int y1, int x2, int y2, Col color) {

    }
}
