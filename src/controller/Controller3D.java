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
    private Solid dodecahedron = new Dodecahedron();
    private Solid axisX = new AxisX();
    private Solid axisY = new AxisY();
    private Solid axisZ = new AxisZ();

    private Camera camera;
    private final Mat4 perspProj;
    private final Mat4 orthoProj;

    private Projection projection = Projection.PERSPECTIVE;

    private int lastX, lastY;

    private final double cameraSpeed = 0.1;
    private final double cameraRotationSpeed = 0.005;
    private final double mouseSensitivity = 0.01;
    private final double scaleUpFactor = 1.05;
    private final double scaleDownFactor = 0.95;

    private boolean isShiftPressed = false;

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

                lastX = e.getX();
                lastY = e.getY();

                if (isShiftPressed) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        moveObject(cube, dx * mouseSensitivity, -dy * mouseSensitivity);
                        moveObject(dodecahedron, dx * mouseSensitivity, -dy * mouseSensitivity);
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        rotateObject(cube, dx * mouseSensitivity, dy * mouseSensitivity);
                        rotateObject(dodecahedron, dx * mouseSensitivity, dy * mouseSensitivity);
                    }
                } else {
                    moveCamera(-dx, -dy);
                }
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
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftPressed = true;
                    shouldDrawScene = true;
                } else if (isShiftPressed && e.getKeyCode() == KeyEvent.VK_UP) {
                    scaleObject(cube, scaleUpFactor);
                    scaleObject(dodecahedron, scaleUpFactor);
                    shouldDrawScene = true;
                } else if (isShiftPressed && e.getKeyCode() == KeyEvent.VK_DOWN) {
                    scaleObject(cube, scaleDownFactor);
                    scaleObject(dodecahedron, scaleDownFactor);
                    shouldDrawScene = true;
                }

                if (requiresCameraUpdate) renderer.setView(camera.getViewMatrix());
                if (shouldDrawScene || requiresCameraUpdate) drawScene();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftPressed = false;
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
        renderer.renderSolid(dodecahedron);

        renderUI();

        panel.repaint();
    }

    private void renderUI() {
        Graphics g = panel.getRaster().getImage().getGraphics();

        g.setColor(Color.WHITE);
        g.drawString("[P] Projection: " + this.projection, 10, 20);
        g.drawString("[Shift + lmb] Move solid", 10, 40);
        g.drawString("[Shift + rmb] Rotate solid", 10, 60);
        g.drawString("[Shift + up/down] Scale solid", 10, 80);

        g.drawString("[WASD] Camera position", 10, 180);
        g.drawString("[drag] Camera direction", 10, 200);

        g.dispose();
    }

    private enum Projection {
        ORTHOGRAPHIC,
        PERSPECTIVE
    }

    private enum Axis {
        X, Y, Z
    }

    private Camera createCamera() {
        return new Camera()
                .withPosition(new Vec3D(1.5, -2.5, 1.5))
                .withAzimuth(Math.toRadians(110)) // - -> look right, + -> look left
                .withZenith(Math.toRadians(-15)) // - -> look down, + -> look up
                .withFirstPerson(true);
    }

    // Finds Math.max(|x|, |y|, |z|) axis => axis most aligned with the camera view vector
    private Axis getMostAlignedAxis() {
        Vec3D view = camera.getViewVector();

        double dotX = Math.abs(view.getX());
        double dotY = Math.abs(view.getY());
        double dotZ = Math.abs(view.getZ());

        if (dotX >= dotY && dotX >= dotZ) {
            return Axis.X;
        } else if (dotY >= dotX && dotY >= dotZ) {
            return Axis.Y;
        } else {
            return Axis.Z;
        }
    }

    private void moveCamera(double dx, double dy) {
        camera = camera.addAzimuth(-dx * cameraRotationSpeed)
                .addZenith(-dy * cameraRotationSpeed);

        renderer.setView(camera.getViewMatrix());
    }

    private void moveObject(Solid solid, double dx, double dy) {
        Axis aligned = getMostAlignedAxis();
        Mat4 translation = switch (aligned) {
            case X -> new Mat4Transl(0, dx, dy);
            case Y -> new Mat4Transl(dx, 0, dy);
            case Z -> new Mat4Transl(dx, dy, 0);
        };
        solid.setModel(solid.getModel().mul(translation));
    }

    private void rotateObject(Solid solid, double dx, double dy) {
        Axis aligned = getMostAlignedAxis();
        Mat4 rotation = switch (aligned) {
            case X -> new Mat4RotZ(dx).mul(new Mat4RotY(dy));
            case Y -> new Mat4RotX(dy).mul(new Mat4RotZ(dx));
            case Z -> new Mat4RotX(dy).mul(new Mat4RotY(dx));
            };
        solid.setModel(rotation.mul(solid.getModel()));
    }

    private void scaleObject(Solid solid, double change) {
        solid.setModel(solid.getModel().mul(new Mat4Scale(change)));
    }
}
