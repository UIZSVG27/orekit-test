package com.roberto.orbit.pruebas;

import java.util.ArrayList;
import java.util.List;

public class RK1_4 {

    public interface ODEFunction {
        double[] compute(double t, double[] y);
    }

    public static class Result {
        public double[] tout;
        public double[][] yout;
    }

    public static Result integrate(ODEFunction f, double t0, double tf,
                                   double[] y0, double h, int rk) {

        int nStages;
        double[] a;
        double[][] b;
        double[] c;

        switch (rk) {
            case 1:
                nStages = 1;
                a = new double[]{0};
                b = new double[][]{{0}};
                c = new double[]{1};
                break;

            case 2:
                nStages = 2;
                a = new double[]{0, 1};
                b = new double[][]{
                        {0, 0},
                        {1, 0}
                };
                c = new double[]{0.5, 0.5};
                break;

            case 3:
                nStages = 3;
                a = new double[]{0, 0.5, 1};
                b = new double[][]{
                        {0, 0, 0},
                        {0.5, 0, 0},
                        {-1, 2, 0}
                };
                c = new double[]{1.0 / 6.0, 2.0 / 3.0, 1.0 / 6.0};
                break;

            case 4:
                nStages = 4;
                a = new double[]{0, 0.5, 0.5, 1};
                b = new double[][]{
                        {0, 0, 0, 0},
                        {0.5, 0, 0, 0},
                        {0, 0.5, 0, 0},
                        {0, 0, 1, 0}
                };
                c = new double[]{1.0 / 6.0, 1.0 / 3.0, 1.0 / 3.0, 1.0 / 6.0};
                break;

            default:
                throw new IllegalArgumentException("rk debe ser 1, 2, 3 o 4");
        }

        double t = t0;
        double[] y = y0.clone();

        List<Double> toutList = new ArrayList<>();
        List<double[]> youtList = new ArrayList<>();

        toutList.add(t);
        youtList.add(y.clone());

        while (t < tf) {

            double ti = t;
            double[] yi = y.clone();

            double[][] fStages = new double[nStages][y.length];

            for (int i = 0; i < nStages; i++) {

                double tInner = ti + a[i] * h;
                double[] yInner = yi.clone();

                for (int j = 0; j < i; j++) {
                    for (int k = 0; k < y.length; k++) {
                        yInner[k] += h * b[i][j] * fStages[j][k];
                    }
                }

                fStages[i] = f.compute(tInner, yInner);
            }

            if (t + h > tf) {
                h = tf - t;
            }

            t = t + h;

            for (int k = 0; k < y.length; k++) {
                double sum = 0;
                for (int i = 0; i < nStages; i++) {
                    sum += c[i] * fStages[i][k];
                }
                y[k] = yi[k] + h * sum;
            }

            toutList.add(t);
            youtList.add(y.clone());
        }

        Result result = new Result();
        result.tout = toutList.stream().mapToDouble(Double::doubleValue).toArray();
        result.yout = youtList.toArray(new double[0][]);

        return result;
    }
}
