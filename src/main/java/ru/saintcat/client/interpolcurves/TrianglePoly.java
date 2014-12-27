/*
 * TrianglePoly.java
 * 
 * Created: 24.12.2014
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
public class TrianglePoly {

    public static int triPoly(List<Vector2D> P, List<List<Vector2D>> triangles) {
        System.out.println("NEW" + " " + P.size());
//        List<Vector2D> P = cloneList(A);
        for (Vector2D asd : P) {
            System.out.print(asd.x + " " + asd.y + "            ");
        }
        P = minPoly(P);
        int n = P.size() - 1;
        if (n == 3) {
            triangles.add(P);
            return triangles.size();
        }

        int d = dirTest(P);
        int k = 3;
        if (conv2(P) == 1) {
            resurnFunction(P, k - 1, n, triangles);
            return triangles.size();
        } else {
            while (nf2(P.get(n - 1), P.get(0), P.get(1)) > 0) {
                System.out.println("LCShift");
                LCShift(P);
            }
            boolean flag = true;
            while (flag) {
//                System.out.println("FIRST WHILE");
                flag = false;
                while (true) {
//                    System.out.println("SECOND WHILE");
                    if (k == n - 1) {
                        resurnFunction(P, k - 1, n, triangles);
                        return triangles.size();
                    }
                    double val = d * nf2(P.get(0), P.get(1), P.get(k - 1));
                    if (val >= 0) {
                        k++;
                    } else {
                        break;
                    }
                }
                for (int i = k + 1; i < n - 1; i++) {
                    double c = crossSegm(P.get(0), P.get(k - 1), P.get(i), P.get(i + 1));
                    System.out.println("c = " + c);
                    if (c <= 0) {
                    } else {
                        k = i;
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    resurnFunction(P, k - 1, n, triangles);
                    return triangles.size();
                }
            }
        }
        return 0;
    }

    public static List<Vector2D> cloneList(List<Vector2D> list) {
        List<Vector2D> clone = new ArrayList<>(list.size());
        for (Vector2D item : list) {
            clone.add(item.clone());
        }
        return clone;
    }

    public static double crossSegm(Vector2D a, Vector2D b, Vector2D c, Vector2D d) {
        double deltaXFirst = b.getX() - a.getX();
        double deltaYFirst = b.getY() - a.getY();

        double deltaXSec = c.getX() - d.getX();
        double deltaYSec = c.getY() - d.getY();

        double[][] matrix = new double[][]{{deltaXFirst, deltaYFirst}, {deltaXSec, deltaYSec}};
        int val = MatrixOperations.invert(matrix);
        if (val == 0) {
            return -1;
        }
        double[][] res = MatrixOperations.multiply(new double[][]{{c.getX() - a.getX(), c.getY() - a.getY()}}, matrix);

        return (((0 <= res[0][0] && res[0][0] <= 1) ? 1 : 0) * ((0 <= res[0][1] && res[0][1] <= 1) ? 1 : 0));
    }

    public static void resurnFunction(List<Vector2D> P, int k, int n, List<List<Vector2D>> triangles) {
//        System.out.println("RESURN");
        List<Vector2D> first = new ArrayList<>();
        first.add(P.get(0));
        //??
        for (int i = k; i <= n; i++) {
            first.add(P.get(i));
        }
        first.add(P.get(0));
        triPoly(first, triangles);
//        System.out.println("FIRST IS COMPLETED:");
        first.clear();
        first.add(P.get(0));
        for (int i = 1; i <= k; i++) {
            first.add(P.get(i));
        }
        first.add(P.get(0));
//        System.out.println("BEFORE SECOND TRI IN RESURN");
        triPoly(first, triangles);
//        System.out.println("RESURN IS COMPLETED");
    }

    public static void LCShift(List<Vector2D> P) {
        P.add(P.get(1));
        P.remove(0);
    }

    public static List<Vector2D> minPoly(List<Vector2D> P) {
        int m = 0;
        for (int i = 1; i < P.size(); i++) {
            Vector2D V = Vector2D.minus(P.get(i), P.get(m));
            if (Vector2D.multipl(V, V) == 0) {
                continue;
            }
            if ((m == 0) || (nf2(P.get(m - 1), P.get(m), P.get(i)) != 0)) {
                m++;
                P.get(m).setVector2D(P.get(i));
                continue;
            }
            Vector2D W = Vector2D.minus(P.get(m), P.get(m - 1));
            if (Vector2D.multipl(V, W) > 0) {
                P.get(m).setVector2D(P.get(i));
                continue;
            }
            if (Vector2D.multipl(Vector2D.minus(P.get(i), P.get(m - 1)), W) > 0) {
                continue;
            }
            P.get(m - 1).setVector2D(P.get(m));
            P.get(m).setVector2D(P.get(i));
        }
        List<Vector2D> res = new ArrayList<>();
        if (nf2(P.get(m - 1), P.get(0), P.get(1)) == 0) {
            res.add(P.get(1));
            for (int j = 2; j < m; j++) {
                res.add(P.get(j));
            }
            res.add(P.get(1));
        } else {
            for (int j = 0; j <= m; j++) {
                res.add(P.get(j));
            }
        }
        return res;
    }

    public static int conv2(List<Vector2D> P) {
        int n = P.size() - 1;
        double s = nf2(P.get(P.size() - 2), P.get(0), P.get(1));
        double f;
        //////????
        for (int i = 1; i < n; i++) {
            f = nf2(P.get(i - 1), P.get(i), P.get(i + 1));
            if (s * f > 0) {
                continue;
            }
            if (s * f == 0) {
                return -1;
            }
            return 0;
        }

        return 1;
    }

    public static int dirTest(List<Vector2D> P) {
        Vector2D Qgr = Vector2D.add(P.get(0), P.get(P.size() - 1));
        double sum = 0;
        for (int i = 0; i < P.size() - 1; i++) {
            sum += ang(Vector2D.minus(P.get(i), Qgr), Vector2D.minus(P.get(i + 1), Qgr));
        }
        return sign(sum);
    }

    public static double ang(Vector2D V, Vector2D W) {
        boolean positive = false;
        if (MatrixOperations.det(new double[][]{{V.x, V.y}, {W.x, W.y}}) >= 0) {
            positive = true;
        }
        return (positive ? 1 : -1) * Math.acos(Vector2D.multipl(V, W) / (V.module() * W.module()));
    }

    public static double nf2(Vector2D a, Vector2D b, Vector2D p) {
        double[][] first = new double[1][2];
        first[0][0] = p.getX() - a.getX();
        first[0][1] = p.getY() - a.getY();

        double[][] second = new double[2][1];
        second[0][0] = b.getY() - a.getY();
        second[1][0] = -(b.getX() - a.getX());

        double[][] res = MatrixOperations.multiply(first, second);
        return res[0][0];
    }

    public static int sign(double x) {
        if (x > 0) {
            return 1;
        }
        if (x < 0) {
            return -1;
        }
        return 0;
    }

    private int octTest(List<Vector2D> P, Vector2D q) {
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
}
