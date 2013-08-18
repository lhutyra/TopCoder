import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;

public class Drawer extends JFrame {
    private class Ray {
        private int x1, y1;
        private int x2, y2;
        private int pointsSet = 0;
    }

    private Ray ray = new Ray();

    private class UserEstimate {
        private List<Integer> x = new ArrayList<Integer>();
        private List<Integer> y = new ArrayList<Integer>();
        private int movedPoint = -1;

        private void processPoint(int px, int py) {
            synchronized (paintMutex) {
                for (int i=0; i < x.size(); i++) {
                    if (similar(px, py, x.get(i), y.get(i))) {
                        movedPoint = i;
                        return;
                    }
                }
                x.add(px);
                y.add(py);
            }
            panel.repaint();
        }
    }

    private UserEstimate userEstimate = new UserEstimate();

    private class DrawerWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            SolutionRunner.stopSolution();
            System.exit(0);
        }
    }

    private static final int RAY_MODE = 0;
    private static final int ESTIMATE_MODE = 1;
    private static final int DONE_MODE = 2;

    private int currentMode = 0;

    private boolean similar(int x1, int y1, int x2, int y2) {
        int diff = 5 * (int)Math.round(TestCase.MAX_COORD / (double)panel.dim);
        return (Math.abs(x1 - x2) <= diff && Math.abs(y1 - y2) <= diff);
    }

    private class DrawerMouseListener extends MouseAdapter {
        private int x, y;

        private boolean outsideClick(MouseEvent e) {
            x = e.getX();
            y = e.getY();

            x = (int)Math.round((x - 5) / (double)panel.dim * TestCase.MAX_COORD);
            y = (int)Math.round((1.0 - (y - 5) / (double)panel.dim) * TestCase.MAX_COORD);

            return !(x >= TestCase.MIN_COORD && x <= TestCase.MAX_COORD &&
                y >= TestCase.MIN_COORD && y <= TestCase.MAX_COORD);
        }

        public void mousePressed(MouseEvent e) {
            if (!VisParams.manual || outsideClick(e)) {
                return;
            }
            if (currentMode == ESTIMATE_MODE) {
                userEstimate.processPoint(x, y);
            }
        }

        public void mouseReleased(MouseEvent e) {
            synchronized (paintMutex) {
                if (userEstimate.movedPoint >= 0) {
                    userEstimate.movedPoint = -1;
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (!VisParams.manual || outsideClick(e)) {
                return;
            }
            synchronized (paintMutex) {
                if (userEstimate.movedPoint >= 0) {
                    userEstimate.x.set(userEstimate.movedPoint, x);
                    userEstimate.y.set(userEstimate.movedPoint, y);
                }
            }
            repaint();
        }

        public void mouseClicked(MouseEvent e) {
            if (!VisParams.manual || outsideClick(e)) {
                return;
            }

            if (currentMode == RAY_MODE) {
                if (ray.pointsSet == 0) {
                    synchronized (paintMutex) {
                        ray.x1 = x;
                        ray.y1 = y;
                        ray.pointsSet = 1;
                    }
                    panel.repaint();
                } else {
                    if (similar(x, y, ray.x1, ray.y1)) {
                        synchronized (paintMutex) {
                            ray.pointsSet = 0;
                        }
                        panel.repaint();
                    } else {
                        synchronized (paintMutex) {
                            ray.x2 = x;
                            ray.y2 = y;
                        }
                        try {
                            SolutionRunner.handleRequest(ray.x1, ray.y1, ray.x2, ray.y2);
                        } catch (Exception ex) {
                            System.out.println("ERROR: unable to cast ray (" + ray.x1 + ", " + ray.y1 + ") -> " +
                                    "(" + ray.x2 + ", " + ray.y2 + ")");
                        }
                        synchronized (paintMutex) {
                            ray.pointsSet = 0;
                        }
                    }
                }
            } else if (currentMode == ESTIMATE_MODE) {
            }
        }
    }

    private class DrawerKeyListener extends KeyAdapter {
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == ' ') {
                synchronized (paintMutex) {
                    toggleDisplayHidden();
                }
            } else if (e.getKeyChar() == '\n') {
                if (!VisParams.manual) {
                    return;
                }
                synchronized (paintMutex) {
                    if (currentMode == RAY_MODE) {
                        ray.pointsSet = 0;
                        currentMode = ESTIMATE_MODE;
                    } else if (currentMode == ESTIMATE_MODE) {
                        int N = userEstimate.x.size();
                        int[] X = new int[N];
                        int[] Y = new int[N];
                        for (int i=0; i < N; i++) {
                            X[i] = userEstimate.x.get(i);
                            Y[i] = userEstimate.y.get(i);
                        }
                        try {
                            SolutionRunner.handleEstimate(X, Y);
                        } catch (Exception ex) {
                            System.out.println("ERROR: " + ex.getMessage());
                        }
                        currentMode = DONE_MODE;
                    }
                }
                panel.repaint();
            } else {
                synchronized (keyMutex) {
                    keyPressed = true;
                    keyMutex.notifyAll();
                }
            }
        }
    }

    private void waitForKeyPressed() {
        synchronized (keyMutex) {
            keyPressed = false;
            while (!keyPressed) {
                try {
                    keyMutex.wait();
                } catch (InterruptedException e) {
                    // nothing
                }
            }
        }
    }

    private final Object paintMutex = new Object();
    private final Object keyMutex = new Object();

    private boolean keyPressed = false;

    private boolean displayHidden = false;

    private class DrawerPanel extends JPanel {
        private JFrame parent;
        private int dim = -1, headerHeight;

        private int convertX(double x) {
            return 5 + (int)Math.round(x / TestCase.MAX_COORD * dim);
        }

        private int convertY(double y) {
            return 5 + (int)Math.round((1.0 - y / TestCase.MAX_COORD) * dim);
        }

        public void paint(Graphics g) {
            if (dim < 0) {
                return;
            }
            synchronized (paintMutex) {
                BufferedImage image = new BufferedImage(VisParams.sz, VisParams.sz, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = (Graphics2D)image.getGraphics();

                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

                graphics.setColor(new Color(192, 192, 192));
                graphics.drawRect(5, 5, dim, dim);

                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)0.5));

                if (hidden != null && displayHidden) {
                    graphics.setColor(new Color(192, 192, 192));
                    graphics.fillPolygon(hidden.xpoints, hidden.ypoints, hidden.npoints);
                }

                if (finished) {
                    graphics.setColor(new Color(128, 255, 128));
                    graphics.fillPolygon(estimate.xpoints, estimate.ypoints, estimate.npoints);
                }

                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

                if (currentMode == RAY_MODE) {
                    if (ray.pointsSet == 1) {
                        graphics.setColor(new Color(192, 0, 0));
                        graphics.fillOval(convertX(ray.x1) - 3, convertY(ray.y1) - 3, 7, 7);
                    }
                } else if (currentMode == ESTIMATE_MODE) {
                    graphics.setColor(new Color(0, 192, 0));
                    for (int i=0; i < userEstimate.x.size(); i++) {
                        graphics.fillOval(convertX(userEstimate.x.get(i)) - 3,
                                convertY(userEstimate.y.get(i)) - 3, 7, 7);
                    }
                    for (int i=0; i < userEstimate.x.size(); i++) {
                        graphics.drawLine(convertX(userEstimate.x.get(i)), convertY(userEstimate.y.get(i)),
                                convertX(userEstimate.x.get((i+1)%userEstimate.x.size())),
                                convertY(userEstimate.y.get((i+1)%userEstimate.y.size())));
                    }
                }

                for (Request req : requests) {
                    if (req.finite) {
                        graphics.setColor(new Color(0, 0, 192));
                    } else {
                        graphics.setColor(new Color(192, 0, 0));
                    }
                    graphics.drawLine(convertX(req.cx), convertY(req.cy),
                            convertX(req.ix), convertY(req.iy));
                }

                g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            }
        }

        private void updateSizes() {
            this.headerHeight = VisParams.sz - parent.getContentPane().getBounds().height;
            this.dim = VisParams.sz - 10 - headerHeight;
        }

        private DrawerPanel(JFrame parent) {
            this.parent = parent;
        }
    }

    private class Request {
        private int cx, cy;
        private double ix, iy;
        private boolean finite;
        private Request(int cx, int cy, double ix, double iy, boolean finite) {
            this.cx = cx;
            this.cy = cy;
            this.ix = ix;
            this.iy = iy;
            this.finite = finite;
        }
    }

    private Polygon hidden, estimate;
    private List<Request> requests = new ArrayList<Request>();

    private boolean finished = false;

    public void setHidden(Polygon p) {
        synchronized (paintMutex) {
            this.hidden = new Polygon();
            for (int i=0; i < p.npoints; i++) {
                this.hidden.addPoint(panel.convertX(p.xpoints[i]), panel.convertY(p.ypoints[i]));
            }
        }
        panel.repaint();
    }

    public void setEstimate(Polygon p) {
        synchronized (paintMutex) {
            this.estimate = new Polygon();
            for (int i=0; i < p.npoints; i++) {
                this.estimate.addPoint(panel.convertX(p.xpoints[i]), panel.convertY(p.ypoints[i]));
            }
            this.finished = true;
        }
        setDisplayHidden(true);
        panel.repaint();
    }

    public void addRequest(int cx, int cy, double px, double py, boolean finite) {
        synchronized (paintMutex) {
            requests.add(new Request(cx, cy, px, py, finite));
        }
        panel.repaint();
        if (!VisParams.manual) {
            if (VisParams.delay >= 0) {
                try {
                    Thread.sleep(VisParams.delay);
                } catch (InterruptedException e) {
                    // nothing
                }
            } else {
                waitForKeyPressed();
            }
        }
    }

    public void setDisplayHidden(boolean value) {
        synchronized (paintMutex) {
            displayHidden = value;
        }
        repaint();
    }

    public void toggleDisplayHidden() {
        setDisplayHidden(!displayHidden);
    }

    private DrawerPanel panel;
    private static Drawer drawer;

    private Drawer() {
        super();

        panel = new DrawerPanel(this);
        getContentPane().add(panel);

        DrawerMouseListener mouseListener = new DrawerMouseListener();
        panel.addMouseListener(mouseListener);
        panel.addMouseMotionListener(mouseListener);

        addKeyListener(new DrawerKeyListener());
        addWindowListener(new DrawerWindowListener());

        setSize(VisParams.sz, VisParams.sz);
        setTitle("Visualizer tool for problem PolygonEstimation");

        setResizable(false);
        setVisible(true);

        panel.updateSizes();

        if (VisParams.displayHidden) {
            setDisplayHidden(true);
        }
    }

    public static Drawer getDrawer() {
        if (!VisParams.vis) {
            return null;
        }
        if (drawer == null) {
            drawer = new Drawer();
        }
        return drawer;
    }
}
