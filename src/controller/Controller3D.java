package controller;

import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import renderer.Renderer;
import solid.*;
import transforms.*;
import view.Panel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller3D {
    private final Panel panel;
    private LineRasterizer lineRasterizer;
    private Renderer renderer;

    // Solids
    //private Solid arrow = new Arrow();
    private Solid cube = new Cube();
    private Solid axisX = new AxisX();
    private Solid axisY = new AxisY();
    private Solid axisZ = new AxisZ();

    private Camera camera;
    private Mat4 perspProj;
    private Mat4 orthoProj;

    private Projection projection = Projection.PERSPECTIVE;

    private int azimuth = 110;
    private int zenith = -15;
    private int lastX, lastY;

    public Controller3D(Panel panel) {
        this.panel = panel;

        lineRasterizer = new LineRasterizerGraphics(panel.getRaster());

        camera = createCamera();

        double fov = Math.toRadians(70);

        perspProj = new Mat4PerspRH(
                fov,
                panel.getRaster().getHeight() / (double) panel.getRaster().getWidth(),
                0.1,
                100
        );

        double h = 6 * Math.tan(fov / 2);
        double w = h * panel.getRaster().getWidth() / (double) panel.getRaster().getHeight();

        orthoProj = new Mat4OrthoRH(
            w, h, 0.1, 100
        );

        renderer = new Renderer(
                lineRasterizer,
                panel.getRaster().getWidth(),
                panel.getRaster().getHeight(),
                camera.getViewMatrix(),
                projection == Projection.PERSPECTIVE ? perspProj : orthoProj
        );

        initListeners();
        drawScene();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;

                azimuth += dx;
                zenith -= dy;

                lastX = e.getX();
                lastY = e.getY();

                renderer.setView(createCamera().getViewMatrix());
                drawScene();
            }
        });

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    projection = projection == Projection.PERSPECTIVE ? Projection.ORTHOGRAPHIC : Projection.PERSPECTIVE;
                    renderer.setProj(projection == Projection.PERSPECTIVE ? perspProj : orthoProj);
                    drawScene();
                }
            }
        });
    }

    private void drawScene() {
        panel.getRaster().clear();

        renderer.renderSolid(axisX);
        renderer.renderSolid(axisY);
        renderer.renderSolid(axisZ);

        renderer.renderSolid(cube);

        renderUI();

        panel.repaint();
    }

    private void renderUI() {
        Graphics g = panel.getRaster().getImage().getGraphics();

        g.setColor(Color.WHITE);
        g.drawString("[P] Projection: " + this.projection, 10, 20);
        g.drawString("Drag mouse to move camera", 10, 200);

        g.dispose();
    }

    private enum Projection {
        ORTHOGRAPHIC,
        PERSPECTIVE
    }

    private Camera createCamera() {
        return new Camera()
                .withPosition(new Vec3D(1.5, -2.5, 1.5))
                .withAzimuth(Math.toRadians(azimuth)) // - -> look right, + -> look left
                .withZenith(Math.toRadians(zenith)) // - -> look down, + -> look up
                .withFirstPerson(true);
    }
}
