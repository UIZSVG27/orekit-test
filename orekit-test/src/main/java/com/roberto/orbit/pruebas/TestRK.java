package com.roberto.orbit.pruebas;

public class TestRK {

    public static void main(String[] args) {

        RK1_4.ODEFunction f = (t, y) -> new double[]{ -2 * y[0] };

        double[] y0 = {1.0};
        double t0 = 0.0;
        double tf = 2.0;
        double h = 0.1;

        RK1_4.Result res = RK1_4.integrate(f, t0, tf, y0, h, 4);

        for (int i = 0; i < res.tout.length; i++) {
            System.out.println("t = " + res.tout[i] +
                               "   y = " + res.yout[i][0]);
        }
    }
}
