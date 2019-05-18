package test.bestcnct;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Encoder extends VectorDiffStruct {
    public Encoder() {
        super(2, 643, 35, 118, 118);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 32; i++)
                w[i] = 0.7071067811865475 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 48; i < 608; i++)
                w[i] = 0.25 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 16; k++)
                f[i * 16 + k + 0] += x[i * 2 + j + 0] * w[j * 16 + k + 0];
        }
        {
            int ap = 0, bp = 32;
            for (int cp = 16; cp < 32; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 16, yp = 32;
            for (int i = 0; i < 16; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 16; j++)
            for (int k = 0; k < 35; k++)
                f[i * 35 + k + 48] += f[i * 16 + j + 32] * w[j * 35 + k + 48];
        }
        {
            int ap = 48, bp = 608;
            for (int cp = 83; cp < 118; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 83, yp = 0;
            for (int i = 0; i < 35; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 83, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 35; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 35, dbp = 608;
            for (int dcp = 0; dcp < 35; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 16; j++)
            for (int k = 0; k < 35; k++) {
                b[i * 16 + j + 70] += b[i * 35 + k + 35] * w[j * 35 + k + 48];
                dw[j * 35 + k + 48] += f[i * 16 + j + 32] * b[i * 35 + k + 35];
            }
        }
        {
            int xp = 16, yp = 32, dxp = 86, dyp = 70;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 102, dbp = 32;
            for (int dcp = 86; dcp < 102; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 16; k++) {
                dx[i * 2 + j + 0] += b[i * 16 + k + 102] * w[j * 16 + k + 0];
                dw[j * 16 + k + 0] += x[i * 2 + j + 0] * b[i * 16 + k + 102];
            }
        }
    }
}
