package ru.saintcat.client.interpolcurves;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class CurveController implements Initializable {

    @FXML
    private AnchorPane paintPanel;
    @FXML
    private Label coordinatsLabel;
    @FXML
    private Label resultLabel;

    private ObservableList<Circle> circles = FXCollections.observableArrayList();
    private List<Line> lines = new ArrayList<>();
    private List<Vector2D> points = new ArrayList<>();
    private List<Polygon> pols = new ArrayList<>();
    private Circle point;

    private static final int CIRCLE_SIZE = 6;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        paintPanel.addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                coordinatsLabel.setText("X=" + t.getX() + " Y=" + t.getY());
            }
        });
        point = createCircle("asd", Color.RED, CIRCLE_SIZE);
        point.setTranslateX(300);
        point.setTranslateY(300);
        paintPanel.getChildren().add(point);
//        newPolAction(null);
//        List<List<Vector2D>> result = new ArrayList<>();
//        List<Vector2D> test = new ArrayList<>();
//        test.add(new Vector2D(0, 100));
//        test.add(new Vector2D(100, 300));
//        test.add(new Vector2D(200, 100));
//        test.add(new Vector2D(100, 200));
//        test.add(new Vector2D(0, 100));
//        TrianglePoly.triPoly(points, result);
//
//        for (int i = 0; i < test.size() - 1; i++) {
////            paintPanel.getChildren().add(new Line(test.get(i).x, test.get(i).y, test.get(i + 1).x, test.get(i + 1).y));
//        }
//        double shift = 0;
//        int f = 0;
//        Random rand = new Random();
//        for (List<Vector2D> r : result) {
//            int rCol1 = rand.nextInt(256);
//            int rCol2 = rand.nextInt(256);
//            int rCol3 = rand.nextInt(256);
//            System.out.println("NEW VECTOR");
//            for (int z = 0; z < r.size() - 1; z++) {
////                System.out.print(r.get(z).x + " " + r.get(z).y + "        ");
////                Line line = new Line(r.get(z).x+shift, r.get(z).y, r.get(z + 1).x+shift, r.get(z + 1).y);
////                line.setStroke(Color.rgb(f, f * 10, f));
////                paintPanel.getChildren().add(line);
//
//            }
//            Polygon polygon = new Polygon();
//            polygon.getPoints().addAll(getPoints(r));
//            polygon.setFill(Color.rgb(rCol1, rCol2, rCol3));
//            paintPanel.getChildren().add(polygon);
//            System.out.println("");
//            f++;
//        }
        triPolyTick();
    }

    private double shift = 0.2;

    private void triPolyTick() {
        List<List<Vector2D>> result = new ArrayList<>();
        List<Vector2D> test = new ArrayList<>();
        test.add(new Vector2D(300 * shift, 100 * shift));
        test.add(new Vector2D(900 * shift, 100 * shift));
        test.add(new Vector2D(700 * shift, 200 * shift));
        test.add(new Vector2D(1000 * shift, 500 * shift));

        test.add(new Vector2D(700 * shift, 800 * shift));
        test.add(new Vector2D(400 * shift, 800 * shift));
        test.add(new Vector2D(600 * shift, 1000 * shift));
        test.add(new Vector2D(200 * shift, 900 * shift));
        test.add(new Vector2D(300 * shift, 700 * shift));
        test.add(new Vector2D(700 * shift, 500 * shift));
        test.add(new Vector2D(500 * shift, 300 * shift));
        test.add(new Vector2D(300 * shift, 400 * shift));
        test.add(new Vector2D(400 * shift, 500 * shift));
        test.add(new Vector2D(100 * shift, 500 * shift));
        test.add(new Vector2D(300 * shift, 100 * shift));
        for (int i = 0; i < test.size() - 1; i++) {
            System.out.println("DRAW :" + test.get(i).x + " " + test.get(i).y + " " + test.get(i + 1).x + " " + test.get(i + 1).y);
            paintPanel.getChildren().add(new Line(test.get(i).x, test.get(i).y, test.get(i + 1).x, test.get(i + 1).y));
        }
        TrianglePoly.triPoly(test, result);

        double shift = 0;
        int f = 0;
        Random rand = new Random();
        paintPanel.getChildren().removeAll(pols);
        pols.clear();
        for (List<Vector2D> r : result) {
            int rCol1 = rand.nextInt(256);
            int rCol2 = rand.nextInt(256);
            int rCol3 = rand.nextInt(256);
            System.out.println("NEW VECTOR");
            for (int z = 0; z < r.size() - 1; z++) {
//                System.out.print(r.get(z).x + " " + r.get(z).y + "        ");
//                Line line = new Line(r.get(z).x+shift, r.get(z).y, r.get(z + 1).x+shift, r.get(z + 1).y);
//                line.setStroke(Color.rgb(f, f * 10, f));
//                paintPanel.getChildren().add(line);

            }
            Polygon polygon = new Polygon();
            polygon.getPoints().addAll(getPoints(r));
            polygon.setFill(Color.rgb(rCol1, rCol2, rCol3));
            pols.add(polygon);
            paintPanel.getChildren().add(polygon);
            System.out.println("");
            f++;
        }
    }

    private void timeTick() {
        int res = octTest(points, new Point2D(point.getTranslateX(), point.getTranslateY()));
        switch (res) {
            case 0:
                resultLabel.setText("На границе");
                break;
            case -1:
                resultLabel.setText("Внутри полигона");
                break;
            case 1:
                resultLabel.setText("Вне полигона");
                break;
            default:
                resultLabel.setText("");
        }

    }

    private int octTest(List<Vector2D> P, Point2D q) {
        double s = 0;
        int w = 0;
        for (int i = 0; i < P.size(); i++) {
            Vector2D pp = P.get(i);
            int v = oct(pp.getX() - q.getX(), pp.getY() - q.getY());
            if (v == 0) {
                return 0;
            }
            if (i == 0) {
                w = v;
                continue;
            }
            int delta = v - w;
            if (Math.abs(delta) < 4) {
                s += delta;
                w = v;
                continue;
            }

            if (Math.abs(delta) > 4) {
                delta = delta - 8 * sign(delta);
                s += delta;
                w = v;
                continue;
            }
            double f = nf2(P.get(i - 1), pp, q);
            if (f == 0) {
                return 0;
            }
            delta = -4 * sign(f);
            s += delta;
            w = v;
        }
        return 1 - 2 * sign((s));
    }

    private double nf2(Vector2D a, Vector2D b, Point2D p) {
        double[][] first = new double[1][2];
        first[0][0] = p.getX() - a.getX();
        first[0][1] = p.getY() - a.getY();

        double[][] second = new double[2][1];
        second[0][0] = b.getY() - a.getY();
        second[1][0] = -(b.getX() - a.getX());

        double[][] res = MatrixOperations.multiply(first, second);
        System.out.println(res.length + " " + res[0].length);
        return res[0][0];
    }

    private int sign(double x) {
        if (x > 0) {
            return 1;
        }
        if (x < 0) {
            return -1;
        }
        return 0;
    }

    private int oct(double x, double y) {
        if (x == 0 && y == 0) {
            return 0;
        }
        if (0 <= y && y < x) {
            return 1;
        }
        if (0 < x && x <= y) {
            return 2;
        }
        if (-x <= y && y < 0) {
            return 8;
        }
        if (0 <= x && x < -y) {
            return 7;
        }
        if (0 < y && y <= -x) {
            return 4;
        }
        if (-y < x && x <= 0) {
            return 3;
        }
        if (x < y && y <= 0) {
            return 5;
        }
        if (y <= x && x < 0) {
            return 6;
        }

        return -100;
    }

    private double initX;
    private double initY;
    private Point2D dragAnchor;

    private Circle createCircle(final String name, final Color color, int radius) {
        final Circle circle = new Circle(radius, new RadialGradient(0, 0, 0.2, 0.3, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
            new Stop(0, Color.rgb(250, 250, 255)),
            new Stop(1, color)
        }));
        circle.setEffect(new InnerShadow(7, color.darker().darker()));
        circle.setCursor(Cursor.HAND);
        circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                me.consume();
            }
        });
        circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                double dragX = me.getSceneX() - dragAnchor.getX();
                double dragY = me.getSceneY() - dragAnchor.getY();
                double newXPosition = initX + dragX;
                double newYPosition = initY + dragY;
                if ((newXPosition >= circle.getRadius()) && (newXPosition <= paintPanel.getPrefWidth() - circle.getRadius())) {
                    circle.setTranslateX(newXPosition);
                }
                if ((newYPosition >= circle.getRadius()) && (newYPosition <= paintPanel.getPrefHeight() - circle.getRadius())) {
                    circle.setTranslateY(newYPosition);
                }
                timeTick();
            }
        });
        circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                circle.toFront();
            }
        });
        circle.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
            }

        });
        circle.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                initX = circle.getTranslateX();
                initY = circle.getTranslateY();
                dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
            }
        });
        return circle;
    }

    private List<Vector2D> apoly(Vector2D T, int a, int b, int O) {
        List<Vector2D> res = new ArrayList<>();
        double fi = 0;
        while (fi < 360) {
            double d = a + Rand.nextInt(b - a);
            double dx = T.x + d * Math.cos(fi * Math.PI / 180);
            double dy = T.y + d * Math.sin(fi * Math.PI / 180);
            System.out.println(dx + " " + dy);
            res.add(new Vector2D(dx, dy));
            System.out.println(fi);
            fi = fi + Rand.nextInt(O);
        }
        return res;
    }
    private static Random Rand = new Random();

    @FXML
    void newPolAction(ActionEvent event) {
        points.clear();
        paintPanel.getChildren().removeAll(lines);
        lines.clear();
        List<Vector2D> pol = apoly(new Vector2D(paintPanel.getPrefWidth() / 2 - 100, paintPanel.getPrefHeight() / 2), 100, 150, 100);
        points.addAll(pol);
        points.add(pol.get(0));
        for (int i = 0; i < points.size() - 1; i++) {
            Line line = new Line(points.get(i).getX(), points.get(i).getY(), points.get(i + 1).getX(), points.get(i + 1).getY());
            lines.add(line);
            paintPanel.getChildren().add(line);
        }
        timeTick();
        triPolyTick();
    }

    public static List<Double> getPoints(List<Vector2D> vect) {
        List<Double> res = new ArrayList<>();
        int z = 0;
        for (Vector2D v : vect) {
            res.add(v.x + 300);
            res.add(v.y);
        }
        return res;
    }
}
