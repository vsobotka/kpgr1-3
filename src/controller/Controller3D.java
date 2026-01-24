package controller;

import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import renderer.Renderer;
import solid.*;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;
import view.Panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller3D {
    private final Panel panel;
    private LineRasterizer lineRasterizer;
    private Renderer renderer;

    // Solids
    private Solid arrow = new Arrow();
    private Solid axisX = new AxisX();
    private Solid axisY = new AxisY();
    private Solid axisZ = new AxisZ();

    private Camera camera;
    private Mat4 proj;

    public Controller3D(Panel panel) {
        this.panel = panel;

        lineRasterizer = new LineRasterizerGraphics(panel.getRaster());

        camera = new Camera()
                .withPosition(new Vec3D(1.5, -2.5, 1.5))
                .withAzimuth(Math.toRadians(110)) // - -> look right, + -> look left
                .withZenith(Math.toRadians(-15)) // - -> look down, + -> look up
                .withFirstPerson(true);

        proj = new Mat4PerspRH(
                Math.toRadians(70),
                panel.getRaster().getHeight() / (double) panel.getRaster().getWidth(),
                0.1,
                100
        );

        renderer = new Renderer(
                lineRasterizer,
                panel.getRaster().getWidth(),
                panel.getRaster().getHeight(),
                camera.getViewMatrix(),
                proj
        );

        initListeners();

        drawScene();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }
        });
    }

    private void drawScene() {
        panel.getRaster().clear();

        renderer.renderSolid(axisX);
        renderer.renderSolid(axisY);
        renderer.renderSolid(axisZ);

        renderer.renderSolid(arrow);

        panel.repaint();
    }
}
