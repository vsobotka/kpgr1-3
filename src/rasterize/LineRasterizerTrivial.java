package rasterize;

import model.Line;
import raster.RasterBufferedImage;
import transforms.Col;

import java.awt.*;

public class LineRasterizerTrivial extends LineRasterizer {
    public LineRasterizerTrivial(RasterBufferedImage raster) {
        super(raster);
    }


    @Override
    public void rasterize(int x1, int y1, int x2, int y2, Col color) {
        // y = kx + q
        float k = (y2 - y1) / (float) (x2 - x1);
        float q = y1 - k * x1;

        if(x1 > x2) {
            // TODO: prohodit
        }

        for(int x = x1; x <= x2; x++) {
            float y = k * x + q;
            raster.setPixel(x, Math.round(y), color.getRGB());
        }
    }
}
