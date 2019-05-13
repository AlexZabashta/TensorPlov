package test.mnist;
import  static java.lang.Math.*;

import core.VectorDiffStruct;
public class DiffFunSimple extends VectorDiffStruct {
    public DiffFunSimple() {
        super(784, 151813, 10, 695, 695);
    }
    @Override
    public void init(double[] w) {
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 784; j++)
            for (int k = 0; k < 183; k++)
                f[i * 183 + k + 0] += x[i * 784 + j + 0] * w[j * 183 + k + 0];
        }
        {
            int ap = 0, bp = 143472;
            for (int cp = 183; cp < 366; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 183, yp = 366;
            for (int i = 0; i < 183; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 183; j++)
            for (int k = 0; k < 42; k++)
                f[i * 42 + k + 549] += f[i * 183 + j + 366] * w[j * 42 + k + 143655];
        }
        {
            int ap = 549, bp = 151341;
            for (int cp = 591; cp < 633; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 591, yp = 633;
            for (int i = 0; i < 42; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 42; j++)
            for (int k = 0; k < 10; k++)
                f[i * 10 + k + 675] += f[i * 42 + j + 633] * w[j * 10 + k + 151383];
        }
        {
            int ap = 675, bp = 151803;
            for (int cp = 685; cp < 695; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 685, yp = 0;
            for (int i = 0; i < 10; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 685, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 10; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 10, dbp = 151803;
            for (int dcp = 0; dcp < 10; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 42; j++)
            for (int k = 0; k < 10; k++) {
                b[i * 42 + j + 20] += b[i * 10 + k + 10] * w[j * 10 + k + 151383];
                dw[j * 10 + k + 151383] += f[i * 42 + j + 633] * b[i * 10 + k + 10];
            }
        }
        {
            int xp = 591, yp = 633, dxp = 62, dyp = 20;
            for (int i = 0; i < 42; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 104, dbp = 151341;
            for (int dcp = 62; dcp < 104; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 183; j++)
            for (int k = 0; k < 42; k++) {
                b[i * 183 + j + 146] += b[i * 42 + k + 104] * w[j * 42 + k + 143655];
                dw[j * 42 + k + 143655] += f[i * 183 + j + 366] * b[i * 42 + k + 104];
            }
        }
        {
            int xp = 183, yp = 366, dxp = 329, dyp = 146;
            for (int i = 0; i < 183; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 512, dbp = 143472;
            for (int dcp = 329; dcp < 512; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 784; j++)
            for (int k = 0; k < 183; k++) {
                dx[i * 784 + j + 0] += b[i * 183 + k + 512] * w[j * 183 + k + 0];
                dw[j * 183 + k + 0] += x[i * 784 + j + 0] * b[i * 183 + k + 512];
            }
        }
    }
}
