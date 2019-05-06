package tojava;

import scheme.Variable;

public enum Selector {
    SELECT_X {
        @Override
        public double[] select(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
            return x;
        }
    },

    SELECT_W {
        @Override
        public double[] select(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
            return w;
        }
    },

    SELECT_Y {
        @Override
        public double[] select(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
            return y;
        }
    },

    SELECT_DX {
        @Override
        public double[] select(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
            return dx;
        }
    },

    SELECT_DW {
        @Override
        public double[] select(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
            return dw;
        }
    },

    SELECT_DY {
        @Override
        public double[] select(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
            return dy;
        }
    },

    SELECT_F {
        @Override
        public double[] select(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
            return f;
        }
    },

    SELECT_B {
        @Override
        public double[] select(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
            return b;
        }
    };

    public static Selector get(Variable variable) {
        switch (variable.base) {
        case "x":
            return SELECT_X;
        case "w":
            return SELECT_W;
        case "y":
            return SELECT_Y;
        case "dx":
            return SELECT_DX;
        case "dw":
            return SELECT_DW;
        case "dy":
            return SELECT_DY;
        case "f":
            return SELECT_F;
        case "b":
            return SELECT_B;
        default:
            throw new IllegalArgumentException("Wrong variable " + variable);
        }
    }

    public abstract double[] select(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b);

}
