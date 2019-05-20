package test.best;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class HVFold extends VectorDiffStruct {
    public HVFold() {
        super(80, 7300, 40, 260, 260);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 4800; i++)
                w[i] = 0.11180339887498948 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 4860; i < 7260; i++)
                w[i] = 0.12909944487358055 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 80; j++)
            for (int k = 0; k < 60; k++)
                f[i * 60 + k + 0] += x[i * 80 + j + 0] * w[j * 60 + k + 0];
        }
        {
            int ap = 0, bp = 4800;
            for (int cp = 60; cp < 120; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 60, yp = 120;
            for (int i = 0; i < 60; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 60; j++)
            for (int k = 0; k < 40; k++)
                f[i * 40 + k + 180] += f[i * 60 + j + 120] * w[j * 40 + k + 4860];
        }
        {
            int ap = 180, bp = 7260;
            for (int cp = 220; cp < 260; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 220, yp = 0;
            for (int i = 0; i < 40; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 220, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 40; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 40, dbp = 7260;
            for (int dcp = 0; dcp < 40; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 60; j++)
            for (int k = 0; k < 40; k++) {
                b[i * 60 + j + 80] += b[i * 40 + k + 40] * w[j * 40 + k + 4860];
                dw[j * 40 + k + 4860] += f[i * 60 + j + 120] * b[i * 40 + k + 40];
            }
        }
        {
            int xp = 60, yp = 120, dxp = 140, dyp = 80;
            for (int i = 0; i < 60; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 200, dbp = 4800;
            for (int dcp = 140; dcp < 200; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 80; j++)
            for (int k = 0; k < 60; k++) {
                dx[i * 80 + j + 0] += b[i * 60 + k + 200] * w[j * 60 + k + 0];
                dw[j * 60 + k + 0] += x[i * 80 + j + 0] * b[i * 60 + k + 200];
            }
        }
    }
}
