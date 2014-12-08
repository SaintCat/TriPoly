package ru.saintcat.client.interpolcurves;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class CurveController implements Initializable {

    @FXML
    private AnchorPane paintPanel;
    @FXML
    private Label coordinatsLabel;
    @FXML
    private RadioButton normalButton;
    @FXML
    private RadioButton chordButton;
    @FXML
    private RadioButton firstDirectiveButton;
    @FXML
    private RadioButton secondDirectiveButton;

    private List<Circle> circles = new ArrayList<>();
    private List<Line> lines = new ArrayList<>();
    private Polynomial polynomial = new Polynomial();

    private static final double DELTA = 0.05;
    private static final int CIRCLE_SIZE = 7;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        paintPanel.addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                coordinatsLabel.setText("X=" + t.getX() + " Y=" + t.getY());
            }
        });
        paintPanel.addEventFilter(MouseEvent.MOUSE_CLICKED, newPointAddHandler);

    }

    private void timeTick() {
        if (circles.size() <= 2) {
            return;
        }
        paintPanel.getChildren().removeAll(lines);
        lines.clear();
        List<Vector2D> points = getPointsFromCoords();
        List<Vector2D> resPoints = normalButton.isSelected() ? polynomial.normal(points, DELTA, secondDirectiveButton.isSelected(), true)
                : polynomial.chord(points, DELTA, secondDirectiveButton.isSelected(), true);
        for (int i = 0; i < resPoints.size() - 1; i++) {
            Vector2D current = getPointInCoord(resPoints.get(i));
            Vector2D next = getPointInCoord(resPoints.get(i + 1));
            Line line = new Line(current.x, current.y, next.x, next.y);
            lines.add(line);
        }
        paintPanel.getChildren().addAll(lines);
    }

    private List<Vector2D> getPointsFromCoords() {
        List<Vector2D> points = new ArrayList<>();
        for (Circle circle : circles) {
            points.add(getPointInPercent(circle.getTranslateX(), circle.getTranslateY()));
        }
        return points;
    }

    @FXML
    void normalSelectAction(ActionEvent event) {
        normalButton.setSelected(true);
        chordButton.setSelected(false);
        timeTick();
    }

    @FXML
    void chordSelectAction(ActionEvent event) {
        normalButton.setSelected(false);
        chordButton.setSelected(true);
        timeTick();
    }

    @FXML
    void firstDirectiveSelectAction(ActionEvent event) {
        firstDirectiveButton.setSelected(true);
        secondDirectiveButton.setSelected(false);
        timeTick();
    }

    @FXML
    void SecondSelectDirectiveAction(ActionEvent event) {
        firstDirectiveButton.setSelected(false);
        secondDirectiveButton.setSelected(true);
        timeTick();
    }

    EventHandler<MouseEvent> newPointAddHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            final Circle circleSmall = createCircle("Blue circle", Color.DODGERBLUE, CIRCLE_SIZE);
            circleSmall.setTranslateX(t.getX());
            circleSmall.setTranslateY(t.getY());
            paintPanel.getChildren().add(circleSmall);
            circles.add(circleSmall);
            timeTick();
        }
    };

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
                paintPanel.removeEventFilter(MouseEvent.MOUSE_CLICKED, newPointAddHandler);
                circle.toFront();
            }
        });
        circle.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                paintPanel.addEventFilter(MouseEvent.MOUSE_CLICKED, newPointAddHandler);
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

    private Vector2D getPointInPercent(double x, double y) {
        return new Vector2D((double) x / paintPanel.getPrefWidth(), (double) (y) / paintPanel.getPrefHeight());
    }

    private Vector2D getPointInCoord(Vector2D v) {
        return new Vector2D(v.x * paintPanel.getPrefWidth(), v.y * paintPanel.getPrefHeight());
    }
}
