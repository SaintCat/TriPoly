package ru.saintcat.client.interpolcurves;

import com.sun.prism.impl.Disposer.Record;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Callback;

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
    @FXML
    private ToggleButton addModeButton;
    @FXML
    private CheckBox loopCurvesButton;
    @FXML
    private CheckBox connectCurveButton;
    @FXML
    private TableView<Circle> table;
    @FXML
    private TableColumn<Circle, Double> yCoorColumn;
    @FXML
    private TableColumn<Circle, Double> xCoorColumn;
    @FXML
    private TableColumn<Record, Boolean> removeColumn;
    @FXML
    private TextField xCoorTextField;
    @FXML
    private TextField yCoorTextField;

//    private List<Circle> circles = new ArrayList<>();
    private ObservableList<Circle> circles = FXCollections.observableArrayList();
    private List<Line> lines = new ArrayList<>();
    private List<Line> connectLines = new ArrayList<>();
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
        addModeButton.setSelected(true);
        table.setItems(circles);
        table.setPlaceholder(new Label(""));
        Callback<TableColumn<Circle, Double>, TableCell<Circle, Double>> cellFactory = new Callback<TableColumn<Circle, Double>, TableCell<Circle, Double>>() {
            @Override
            public TableCell call(TableColumn p) {
                return new EditingCell();
            }
        };

        xCoorColumn.setCellValueFactory(new PropertyValueFactory("translateX"));
        xCoorColumn.setCellFactory(cellFactory);
        yCoorColumn.setCellValueFactory(new PropertyValueFactory("translateY"));
        yCoorColumn.setCellFactory(cellFactory);

        removeColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Record, Boolean>, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Record, Boolean> p) {
                        return new SimpleBooleanProperty(p.getValue() != null);
                    }
                });

        //Adding the Button to the cell
        removeColumn.setCellFactory(
                new Callback<TableColumn<Record, Boolean>, TableCell<Record, Boolean>>() {
                    @Override
                    public TableCell<Record, Boolean> call(TableColumn<Record, Boolean> p) {
                        return new ButtonCell();
                    }

                });
    }

    private void timeTick() {
        if (circles.size() <= 2) {
            paintPanel.getChildren().removeAll(lines);
            lines.clear();
            return;
        }
        paintPanel.getChildren().removeAll(lines);
        lines.clear();
        List<Vector2D> points = getPointsFromCoords();
        List<Vector2D> resPoints = normalButton.isSelected() ? polynomial.normal(points, DELTA, secondDirectiveButton.isSelected(),
                loopCurvesButton.isSelected()) : polynomial.chord(points, DELTA, secondDirectiveButton.isSelected(), loopCurvesButton.isSelected());
        for (int i = 0; i < resPoints.size() - 1; i++) {
            Vector2D current = getPointInCoord(resPoints.get(i));
            Vector2D next = getPointInCoord(resPoints.get(i + 1));
            Line line = new Line(current.x, current.y, next.x, next.y);
            line.setStroke(Color.RED);
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
    void addNewPointAction(ActionEvent event) {
        Double x;
        Double y;
        try {
            x = Double.valueOf(xCoorTextField.getText());
        } catch (Exception e) {
            xCoorTextField.clear();
            return;
        }
        try {
            y = Double.valueOf(yCoorTextField.getText());
        } catch (Exception e) {
            yCoorTextField.clear();
            return;
        }
        xCoorTextField.clear();
        yCoorTextField.clear();
        final Circle circleSmall = createCircle("Blue circle", Color.DODGERBLUE, CIRCLE_SIZE);
        circleSmall.setTranslateX(x);
        circleSmall.setTranslateY(y);
        paintPanel.getChildren().add(circleSmall);
        if (connectCurveButton.isSelected() && circles.size() >= 1) {
            Circle first = circles.get(circles.size() - 1);
            Line line = new Line(first.getTranslateX(), first.getTranslateY(), circleSmall.getTranslateX(), circleSmall.getTranslateY());
            line.startXProperty().bindBidirectional(first.translateXProperty());
            line.startYProperty().bindBidirectional(first.translateYProperty());
            line.endXProperty().bindBidirectional(circleSmall.translateXProperty());
            line.endYProperty().bindBidirectional(circleSmall.translateYProperty());
            paintPanel.getChildren().add(line);
            connectLines.add(line);
        }
        circles.add(circleSmall);
        timeTick();
    }

    @FXML
    void addNewModeAction(ActionEvent event) {
        if (((ToggleButton) event.getSource()).isSelected()) {
            paintPanel.addEventFilter(MouseEvent.MOUSE_CLICKED, newPointAddHandler);
        } else {
            paintPanel.removeEventFilter(MouseEvent.MOUSE_CLICKED, newPointAddHandler);
        }
    }

    @FXML
    void loopCurvesAction(ActionEvent event) {
        timeTick();
    }

    @FXML
    void connectLineAction(ActionEvent event) {
        if (connectCurveButton.isSelected()) {
            for (int i = 0; i < circles.size() - 1; i++) {
                Circle first = circles.get(i);
                Circle next = circles.get(i + 1);
                Line line = new Line(first.getTranslateX(), first.getTranslateY(), next.getTranslateX(), next.getTranslateY());
                line.startXProperty().bindBidirectional(first.translateXProperty());
                line.startYProperty().bindBidirectional(first.translateYProperty());
                line.endXProperty().bindBidirectional(next.translateXProperty());
                line.endYProperty().bindBidirectional(next.translateYProperty());
                paintPanel.getChildren().add(line);
                connectLines.add(line);
            }
        } else {
            paintPanel.getChildren().removeAll(connectLines);
            connectLines.clear();
        }
    }

    @FXML
    void normalSelectAction(ActionEvent event) {
        normalButton.setSelected(true);
        chordButton.setSelected(false);
        timeTick();

    }

    @FXML
    void clearAllAction(ActionEvent event) {
        paintPanel.getChildren().removeAll(circles);
        circles.clear();
        paintPanel.getChildren().removeAll(lines);
        lines.clear();
        paintPanel.getChildren().removeAll(connectLines);
        connectLines.clear();
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
            if (connectCurveButton.isSelected() && circles.size() >= 1) {
                Circle first = circles.get(circles.size() - 1);
                Line line = new Line(first.getTranslateX(), first.getTranslateY(), circleSmall.getTranslateX(), circleSmall.getTranslateY());
                line.startXProperty().bindBidirectional(first.translateXProperty());
                line.startYProperty().bindBidirectional(first.translateYProperty());
                line.endXProperty().bindBidirectional(circleSmall.translateXProperty());
                line.endYProperty().bindBidirectional(circleSmall.translateYProperty());
                paintPanel.getChildren().add(line);
                connectLines.add(line);
            }
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
                table.getFocusModel().focus(circles.indexOf(circle));
            }
        });
        circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                table.getFocusModel().focus(circles.indexOf(circle));
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
                if (addModeButton.isSelected()) {
                    paintPanel.removeEventFilter(MouseEvent.MOUSE_CLICKED, newPointAddHandler);
                }
                circle.toFront();
            }
        });
        circle.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                if (addModeButton.isSelected()) {
                    paintPanel.addEventFilter(MouseEvent.MOUSE_CLICKED, newPointAddHandler);
                }
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

    class EditingCell extends TableCell<Circle, Double> {

        private TextField textField;

        public EditingCell() {
            setAlignment(Pos.CENTER);
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getItem().toString());
            setGraphic(null);
        }

        @Override
        public void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0,
                        Boolean arg1, Boolean arg2) {
                    if (!arg2) {
                        try {
                            Double val = Double.valueOf(textField.getText());
                            commitEdit(val);
                            timeTick();
                        } catch (Exception e) {
                        }
                    }
                }
            });
            textField.setAlignment(Pos.CENTER);
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

    private class ButtonCell extends TableCell<Record, Boolean> {

        final Button cellButton = new Button("Удалить");

        public ButtonCell() {
            //Action when the button is pressed
            cellButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    Circle current = (Circle) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                    paintPanel.getChildren().remove(current);
                    circles.remove(current);
                    timeTick();
                }
            });
            setAlignment(Pos.CENTER);
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(cellButton);
            }
        }
    }
}
