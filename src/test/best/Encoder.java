package test.best;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Encoder extends VectorDiffStruct {
    public Encoder() {
        super(2, 1272, 48, 168, 168);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 48; i++)
                w[i] = 0.7071067811865475 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 72; i < 1224; i++)
                w[i] = 0.20412414523193154 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 24; k++)
                f[i * 24 + k + 0] += x[i * 2 + j + 0] * w[j * 24 + k + 0];
        }
        {
            int ap = 0, bp = 48;
            for (int cp = 24; cp < 48; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 24, yp = 48;
            for (int i = 0; i < 24; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 24; j++)
            for (int k = 0; k < 48; k++)
                f[i * 48 + k + 72] += f[i * 24 + j + 48] * w[j * 48 + k + 72];
        }
        {
            int ap = 72, bp = 1224;
            for (int cp = 120; cp < 168; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 120, yp = 0;
            for (int i = 0; i < 48; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 120, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 48; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 48, dbp = 1224;
            for (int dcp = 0; dcp < 48; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 24; j++)
            for (int k = 0; k < 48; k++) {
                b[i * 24 + j + 96] += b[i * 48 + k + 48] * w[j * 48 + k + 72];
                dw[j * 48 + k + 72] += f[i * 24 + j + 48] * b[i * 48 + k + 48];
            }
        }
        {
            int xp = 24, yp = 48, dxp = 120, dyp = 96;
            for (int i = 0; i < 24; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 144, dbp = 48;
            for (int dcp = 120; dcp < 144; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 24; k++) {
                dx[i * 2 + j + 0] += b[i * 24 + k + 144] * w[j * 24 + k + 0];
                dw[j * 24 + k + 0] += x[i * 2 + j + 0] * b[i * 24 + k + 144];
            }
        }
    }
}
