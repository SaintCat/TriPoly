/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saintcat.client.interpolcurves;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Circle;

/**
 *
 * @author Роман
 */
public class TestMain {

    public static void main(String[] args) {
        List<Vector2D> points = new ArrayList();
        points.add(getPointInPercent(200, 200));

        points.add(getPointInPercent(300, 300));
        points.add(getPointInPercent(400, 200));
//        points.add(getPointInPercent(300, 100));
        Polynomial pol = new Polynomial();
        List<Vector2D> resPoints;
        resPoints = pol.chord(points,
                0.01,
                false,
                true);

        for (Vector2D v : points) {
            Vector2D vv = GetPointInCoord(v);
            System.out.println("INIT POINTS: " + vv.x + " " + vv.y);
        }
        for (Vector2D v : resPoints) {
            Vector2D vv = GetPointInCoord(v);
            System.out.println("RES POINT: " + vv.x + " " + vv.y);
        }
        System.out.println("POINT COUNT : " + resPoints.size());

    }

    private static Vector2D getPointInPercent(int x, int y) {
        return new Vector2D((double) x / 1000, (double) (y) / 1000);
    }

    private static Vector2D GetPointInCoord(Vector2D v) {
        return new Vector2D(v.x * 1000, v.y * 1000);
    }
}
