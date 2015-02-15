/*
 * Equidistant.java
 * 
 * Created: 28.12.2014
 * 
 * Copyright (c) ExpertPB 2014
 * All information contained herein is, and remains the property of
 * ExpertPB and its suppliers, if any.
 */
package ru.saintcat.client.interpolcurves;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Roman Chernyshev
 */
public class Equidistant {

    public static List<Vector2D> getEquidistantLine(List<Vector2D> P, double r) {
        List<Vector2D> result = new ArrayList<>();

        Vector2D p0 = P.get(0);
        Vector2D p1 = P.get(1);
        Vector2D pn_1 = P.get(P.size() - 2);
        Vector2D pn = P.get(P.size() - 1);

        Vector2D V0 = new Vector2D(p1.x - p0.x, p1.y - p0.y);
        double e0Dx = p0.x + r * (V0.normal().rRot().x - V0.normal().x);
        double e0Dy = p0.y + r * (V0.normal().rRot().y - V0.normal().y);
        Vector2D e0 = new Vector2D(e0Dx, e0Dy);
        result.add(e0);

        for (int i = 1; i < P.size() - 1; i++) {

            Vector2D pi = P.get(i);
            Vector2D pi_1 = P.get(i - 1);
            Vector2D pi1 = P.get(i + 1);
            System.out.println("WORK FOR " + pi.toString());
            System.out.println("NEXT " + pi1.toString());
            System.out.println("PREV " + pi_1.toString());

            Vector2D vi = new Vector2D(pi1.x - pi.x, pi1.y - pi.y);
            Vector2D vi_1 = new Vector2D(pi.x - pi_1.x, pi.y - pi_1.y);
            //sec(0.5 * ang(vi_1, vi)) *(Vector2D.add(vi, vi_1).normal().rRot().x
            double eiDx = pi.x + r * sec(0.5 * ang(vi.normal(), vi_1.normal())) * (Vector2D.add(vi_1, vi).rRot().normal().x);
            double eiDy = pi.y + r * sec(0.5 * ang(vi.normal(), vi_1.normal())) * (Vector2D.add(vi, vi_1).rRot().normal().y);
            Vector2D ei = new Vector2D(eiDx, eiDy);
            result.add(ei);
        }

        Vector2D Vn_1 = new Vector2D(pn.x - pn_1.x, pn.y - pn_1.y);
        double enDx = pn.x + r * (Vn_1.normal().rRot().x + Vn_1.normal().x);
        double enDy = pn.y + r * (Vn_1.normal().rRot().y + Vn_1.normal().y);
        Vector2D en = new Vector2D(enDx, enDy);
        result.add(en);
        return result;
    }

    public static double ang(Vector2D V, Vector2D W) {
        boolean positive = false;
        if (MatrixOperations.det(new double[][]{{V.x, V.y}, {W.x, W.y}}) >= 0) {
            positive = true;
        }
        System.out.println("ANG RETURN + " + (positive ? 1 : -1) * Math.acos(Vector2D.multipl(V, W) / (V.module() * W.module())));
        return (positive ? 1 : -1) * Math.acos(Vector2D.multipl(V, W) / (V.module() * W.module()));
    }

    public static double sec(double val) {
        return 1.0 / Math.cos(val);
    }
}
