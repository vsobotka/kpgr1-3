package controller;

import fill.Filler;
import fill.SeedFiller;
import model.Line;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.LineRasterizerTrivial;
import rasterize.PolygonRasterizer;
import transforms.Col;
import view.Panel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller2D {
    private final Panel panel;

    private Line line;
    private LineRasterizer lineRasterizer;

    private Polygon polygon;
    private PolygonRasterizer polygonRasterizer;

    private Filler filler;
    private Point seedFillStart;

    public Controller2D(Panel panel) {
        this.panel = panel;

        lineRasterizer = new LineRasterizerGraphics(panel.getRaster());
        //lineRasterizer = new LineRasterizerTrivial(panel.getRaster());
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);

        polygon = new Polygon();

        initListeners();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    polygon.addPoint(new Point(e.getX(), e.getY()));
                    drawScene();
                }

                if(e.getButton() == MouseEvent.BUTTON3) {
                    seedFillStart = new Point(e.getX(), e.getY());
                    drawScene();
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int centerX = panel.getWidth() / 2;
                int centerY = panel.getHeight() / 2;

                line = new Line(centerX, centerY, e.getX(), e.getY());

                drawScene();
            }
        });
    }

    private void drawScene() {
        panel.getRaster().clear();

        // tady budeme rasterizovat
        if (line != null)
            lineRasterizer.rasterize(line, new Col(0xff0000));

        polygonRasterizer.rasterize(polygon);

        if(seedFillStart != null)
        {
            filler = new SeedFiller(panel.getRaster(), seedFillStart.getX(), seedFillStart.getY(), 0x0000ff);
            filler.fill();
        }

        panel.repaint();
    }
}
