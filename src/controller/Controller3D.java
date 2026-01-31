package controller;

import config.Config;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import renderer.Renderer;
import solid.*;
import transforms.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Controller3D {
    private final Panel panel;
    private final Renderer renderer;

    private final Timer timer;

    private Camera camera;
    private final Mat4 perspProj;
    private final Mat4 orthoProj;

    private Projection projection = Projection.PERSPECTIVE;

    private int lastX, lastY;


    private boolean isShiftPressed = false;
    private boolean isAnimationRunning = false;

    private final ArrayList<Solid> solids = new ArrayList<>();
    private final ArrayList<Solid> axes = new ArrayList<>();
    private final ArrayList<Solid> curves = new ArrayList<>();
    private int selectedSolidIndex = -1;

    public Controller3D(Panel panel) {
        this.panel = panel;

        LineRasterizer lineRasterizer = new LineRasterizerGraphics(panel.getRaster());

        camera = createCamera();

        double fov = Math.toRadians(Config.FOV_DEGREES);

        perspProj = new Mat4PerspRH(
                fov,
                panel.getRaster().getHeight() / (double) panel.getRaster().getWidth(),
                Config.NEAR_CLIP,
                Config.FAR_CLIP
        );

        double h = 6 * Math.tan(fov / 2);
        double w = h * panel.getRaster().getWidth() / (double) panel.getRaster().getHeight();

        orthoProj = new Mat4OrthoRH(
            w, h, Config.NEAR_CLIP, Config.FAR_CLIP
        );

        renderer = new Renderer(
                lineRasterizer,
                panel.getRaster().getWidth(),
                panel.getRaster().getHeight(),
                camera.getViewMatrix(),
                projection == Projection.PERSPECTIVE ? perspProj : orthoProj
        );

        timer = new Timer(Config.ANIMATION_DELAY_MS, _ -> {
            ArrayList<Solid> selectedSolids = getSelectedSolids();

            for (Solid solid : selectedSolids) {
                double dx = -0.003;
                double dy = -0.007;

                if (solid instanceof Cube) {
                    dx = 0.01;
                    dy = -0.003;
                } else if (solid instanceof Dodecahedron) {
                    dx = 0.005;
                    dy = 0.005;
                }

                rotateObject(solid, dx, dy);
            }

            drawScene();
        });

        axes.add(new AxisX());
        axes.add(new AxisY());
        axes.add(new AxisZ());

        Cube cube = new Cube();
        Dodecahedron dodecahedron = new Dodecahedron();
        PentagonalPrism pentagonalPrism = new PentagonalPrism();
        solids.add(cube);
        solids.add(dodecahedron);
        solids.add(pentagonalPrism);

        curves.add(new BezierCurve(cube));
        curves.add(new FergusonCurve(dodecahedron));
        curves.add(new CoonsCurve(pentagonalPrism));

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
                        for (Solid solid : getSelectedSolids()) {
                            moveObject(solid, dx * Config.MOUSE_SENSITIVITY, -dy * Config.MOUSE_SENSITIVITY);
                        }
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        for (Solid solid : getSelectedSolids()) {
                            rotateObject(solid, dx * Config.MOUSE_SENSITIVITY, dy * Config.MOUSE_SENSITIVITY);
                        }
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
                    camera = camera.forward(Config.CAMERA_SPEED);
                    requiresCameraUpdate = true;
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    camera = camera.backward(Config.CAMERA_SPEED);
                    requiresCameraUpdate = true;
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    camera = camera.left(Config.CAMERA_SPEED);
                    requiresCameraUpdate = true;
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    camera = camera.right(Config.CAMERA_SPEED);
                    requiresCameraUpdate = true;
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftPressed = true;
                    shouldDrawScene = true;
                } else if (isShiftPressed && e.getKeyCode() == KeyEvent.VK_UP) {
                    for (Solid solid : getSelectedSolids()) {
                        scaleObject(solid, Config.SCALE_UP_FACTOR);
                    }
                    shouldDrawScene = true;
                } else if (isShiftPressed && e.getKeyCode() == KeyEvent.VK_DOWN) {
                    for (Solid solid : getSelectedSolids()) {
                        scaleObject(solid, Config.SCALE_DOWN_FACTOR);
                    }
                    shouldDrawScene = true;
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (isAnimationRunning) {
                        timer.stop();
                        shouldDrawScene = true; // not needed for scene, only UI
                    } else {
                        timer.start();
                    }

                    isAnimationRunning = !isAnimationRunning;
                } else if (e.getKeyCode() == KeyEvent.VK_N) {
                    selectedSolidIndex = selectedSolidIndex == solids.size() - 1 ? -1 : selectedSolidIndex + 1;
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

        for (Solid axis : axes) {
            renderer.renderSolid(axis);
        }

        for (Solid solid : solids) {
            renderer.renderSolid(solid);
        }

        for (Solid curve : curves) {
            renderer.renderSolid(curve);
        }

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
        g.drawString("[Space] Animation: " + this.isAnimationRunning, 10, 100);
        g.drawString("[N] Select solid: " + getSelectedSolidName(), 10, 120);

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
                .withPosition(new Vec3D(Config.CAMERA_INIT_X, Config.CAMERA_INIT_Y, Config.CAMERA_INIT_Z))
                .withAzimuth(Math.toRadians(Config.CAMERA_INIT_AZIMUTH)) // - -> look right, + -> look left
                .withZenith(Math.toRadians(Config.CAMERA_INIT_ZENITH)) // - -> look down, + -> look up
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
        camera = camera.addAzimuth(-dx * Config.CAMERA_ROTATION_SPEED)
                .addZenith(-dy * Config.CAMERA_ROTATION_SPEED);

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

    private ArrayList<Solid> getSelectedSolids() {
        if (selectedSolidIndex >= 0 && selectedSolidIndex < solids.size()) return new ArrayList<>(java.util.List.of(solids.get(selectedSolidIndex)));

        return solids;
    }

    private String getSelectedSolidName() {
        if (selectedSolidIndex >= 0 && selectedSolidIndex < solids.size()) return solids.get(selectedSolidIndex).getClass().getSimpleName();

        return "ALL";
    }
}
