/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.saintcat.client.interpolcurves;

/**
 *
 * @author Chernyshov
 */
public class MatrixOperations {

    public static double[][] createScalingMatrix(double mx, double my) {
        double[][] res = new double[2][2];
        res[0][0] = mx;
        res[0][1] = 0;
        res[1][0] = 0;
        res[1][1] = my;
        return res;
    }

    public static double[][] createRotationMatrix(double angle) {
        double[][] res = new double[2][2];
        res[0][0] = Math.cos(angle);
        res[0][1] = Math.sin(angle);
        res[1][0] = -Math.sin(angle);
        res[1][1] = Math.cos(angle);
        return res;
    }

    public static double[][] createRotatinMatrix3(double angle) {
        double[][] res = new double[3][3];
        res[0][0] = Math.cos(angle);
        res[0][1] = -Math.sin(angle);
        res[1][2] = 0;
        res[1][0] = Math.sin(angle);
        res[1][1] = Math.cos(angle);
        res[1][2] = 0;
        res[2][0] = 0;
        res[2][1] = 0;
        res[2][2] = 1;

        return res;
    }

    public static double[][] multiply(double[][] mA, double[][] mB) {
        int m = mA.length;
        int n = mB[0].length;
        int o = mB.length;
        double[][] res = new double[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < o; k++) {
                    res[i][j] += mA[i][k] * mB[k][j];
                }
            }
        }
        return res;
    }

    public static double[][] rotatePoint(double x, double y, double sX, double sY, double angle) {
        double[][] s = new double[][]{{1, 0, 0}, {0, 1, 0}, {-sX, -sY, 1}};
        double[][] f = new double[][]{{1, 0, 0}, {0, 1, 0}, {sX, sY, 1}};

        double[][] asd = multiply(new double[][]{{x, y, 1}}, s);
        double[][] asd2 = multiply(asd, createRotatinMatrix3(angle));
        double[][] asd3 = multiply(asd2, f);

        return new double[][]{{asd3[0][0], asd3[0][1]}};
    }

    public static double[][] createTransferMatrix(double dX, double dY) {
        double[][] res = new double[3][3];

        res[0][0] = 1;
        res[0][1] = 0;
        res[0][2] = dX;
        res[1][0] = 0;
        res[1][1] = 1;
        res[1][2] = dY;
        res[2][0] = 0;
        res[2][1] = 0;
        res[2][2] = 1;

        return res;
    }

    public static double[][] transpose(double[][] matrix) throws IllegalArgumentException {
        if (matrix.length == 0) {
            throw new IllegalArgumentException("Empty array");
        }
        int rowLength = matrix[0].length;
        for (double[] ai : matrix) {
            if (rowLength != ai.length) {
                throw new IllegalArgumentException("Non-equal rows");
            }
        }

        double[][] tMatrix = new double[rowLength][];
        for (int i = 0; i < rowLength; i++) {
            tMatrix[i] = new double[matrix.length];
        }
        for (int i = 0; i < matrix.length; i++) {
            double[] tArr = matrix[i];
            for (int j = 0; j < rowLength; j++) {
                tMatrix[j][i] = tArr[j];
            }
        }
        return tMatrix;
    }

    public static double det(double[][] a) {
        final double eps = 1e-10;
        int n = a.length;
        double res = 1;
        boolean[] used = new boolean[n];
        for (int i = 0; i < n; i++) {
            int p;
            for (p = 0; p < n; p++) {
                if (!used[p] && Math.abs(a[p][i]) > eps) {
                    break;
                }
            }
            if (p >= n) {
                return 0;
            }
            res *= a[p][i];
            used[p] = true;
            double z = 1 / a[p][i];
            for (int j = 0; j < n; j++) {
                a[p][j] *= z;
            }
            for (int j = 0; j < n; ++j) {
                if (j != p) {
                    z = a[j][i];
                    for (int k = 0; k < n; ++k) {
                        a[j][k] -= z * a[p][k];
                    }
                }
            }
        }
        return res;
    }

    public static int invert(double A[][]) {
        int n = A.length;
        int row[] = new int[n];
        int col[] = new int[n];
        double temp[] = new double[n];
        int hold, I_pivot, J_pivot;
        double pivot, abs_pivot;

        if (A[0].length != n) {
            System.out.println("Error in Matrix.invert, inconsistent array sizes.");
        }
        // установиим row и column как вектор изменений.
        for (int k = 0; k < n; k++) {
            row[k] = k;
            col[k] = k;
        }
        // начало главного цикла
        for (int k = 0; k < n; k++) {
            // найдем наибольший элемент для основы
            pivot = A[row[k]][col[k]];
            I_pivot = k;
            J_pivot = k;
            for (int i = k; i < n; i++) {
                for (int j = k; j < n; j++) {
                    abs_pivot = Math.abs(pivot);
                    if (Math.abs(A[row[i]][col[j]]) > abs_pivot) {
                        I_pivot = i;
                        J_pivot = j;
                        pivot = A[row[i]][col[j]];
                    }
                }
            }
            if (Math.abs(pivot) < 1.0E-10) {
                System.out.println("Matrix is singular !");
                return 0;
            }
            //перестановка к-ой строки и к-ого столбца с стобцом и строкой, содержащий основной элемент(pivot основу)
            hold = row[k];
            row[k] = row[I_pivot];
            row[I_pivot] = hold;
            hold = col[k];
            col[k] = col[J_pivot];
            col[J_pivot] = hold;
            // k-ую строку с учетом перестановок делим на основной элемент
            A[row[k]][col[k]] = 1.0 / pivot;
            for (int j = 0; j < n; j++) {
                if (j != k) {
                    A[row[k]][col[j]] = A[row[k]][col[j]] * A[row[k]][col[k]];
                }
            }
            // внутренний цикл
            for (int i = 0; i < n; i++) {
                if (k != i) {
                    for (int j = 0; j < n; j++) {
                        if (k != j) {
                            A[row[i]][col[j]] = A[row[i]][col[j]] - A[row[i]][col[k]]
                                    * A[row[k]][col[j]];
                        }
                    }
                    A[row[i]][col[k]] = -A[row[i]][col[k]] * A[row[k]][col[k]];
                }
            }
        }
        // конец главного цикла

        // переставляем назад rows
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                temp[col[i]] = A[row[i]][j];
            }
            for (int i = 0; i < n; i++) {
                A[i][j] = temp[i];
            }
        }
        // переставляем назад columns
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                temp[row[j]] = A[i][col[j]];
            }
            for (int j = 0; j < n; j++) {
                A[i][j] = temp[j];
            }
        }
        return 1;
    }

}
