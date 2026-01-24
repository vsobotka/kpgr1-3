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
    private final Mat4 perspProj;
    private final Mat4 orthoProj;

    private Projection projection = Projection.PERSPECTIVE;

    private int lastX, lastY;

    private final double cameraSpeed = 0.1;
    private final double mouseSensitivity = 0.01;

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

                camera = camera.addAzimuth(-dx * mouseSensitivity)
                            .addZenith(-dy * mouseSensitivity);

                lastX = e.getX();
                lastY = e.getY();

                renderer.setView(camera.getViewMatrix());
                drawScene();
            }
        });

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                boolean requiresCameraUpdate = false;
                boolean shouldDrawScene = false;

                if (e.getKeyCode() == KeyEvent.VK_P) {
                    projection = projection == Projection.PERSPECTIVE ? Projection.ORTHOGRAPHIC : Projection.PERSPECTIVE;
                    renderer.setProj(projection == Projection.PERSPECTIVE ? perspProj : orthoProj);
                    shouldDrawScene = true;
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    camera = camera.forward(cameraSpeed);
                    requiresCameraUpdate = true;
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    camera = camera.backward(cameraSpeed);
                    requiresCameraUpdate = true;
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    camera = camera.left(cameraSpeed);
                    requiresCameraUpdate = true;
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    camera = camera.right(cameraSpeed);
                    requiresCameraUpdate = true;
                }

                if (requiresCameraUpdate) renderer.setView(camera.getViewMatrix());
                if (shouldDrawScene || requiresCameraUpdate) drawScene();
            }

            @Override
            public void keyReleased(KeyEvent e) {
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
        g.drawString("[WASD] Camera position", 10, 180);
        g.drawString("[drag] Camera direction", 10, 200);

        g.dispose();
    }

    private enum Projection {
        ORTHOGRAPHIC,
        PERSPECTIVE
    }

    private Camera createCamera() {
        return new Camera()
                .withPosition(new Vec3D(1.5, -2.5, 1.5))
                .withAzimuth(Math.toRadians(110)) // - -> look right, + -> look left
                .withZenith(Math.toRadians(-15)) // - -> look down, + -> look up
                .withFirstPerson(true);
    }
}
