package test.mnist;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Encoder extends VectorDiffStruct {
    public Encoder() {
        super(9, 636, 28, 104, 104);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 144; i++)
                w[i] = 0.3333333333333333 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 160; i < 608; i++)
                w[i] = 0.25 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 9; j++)
            for (int k = 0; k < 16; k++)
                f[i * 16 + k + 0] += x[i * 9 + j + 0] * w[j * 16 + k + 0];
        }
        {
            int ap = 0, bp = 144;
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
            for (int k = 0; k < 28; k++)
                f[i * 28 + k + 48] += f[i * 16 + j + 32] * w[j * 28 + k + 160];
        }
        {
            int ap = 48, bp = 608;
            for (int cp = 76; cp < 104; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 76, yp = 0;
            for (int i = 0; i < 28; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 76, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 28; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 28, dbp = 608;
            for (int dcp = 0; dcp < 28; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 16; j++)
            for (int k = 0; k < 28; k++) {
                b[i * 16 + j + 56] += b[i * 28 + k + 28] * w[j * 28 + k + 160];
                dw[j * 28 + k + 160] += f[i * 16 + j + 32] * b[i * 28 + k + 28];
            }
        }
        {
            int xp = 16, yp = 32, dxp = 72, dyp = 56;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 88, dbp = 144;
            for (int dcp = 72; dcp < 88; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 9; j++)
            for (int k = 0; k < 16; k++) {
                dx[i * 9 + j + 0] += b[i * 16 + k + 88] * w[j * 16 + k + 0];
                dw[j * 16 + k + 0] += x[i * 9 + j + 0] * b[i * 16 + k + 88];
            }
        }
    }
}
