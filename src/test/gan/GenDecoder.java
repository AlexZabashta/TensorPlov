package test.gan;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class GenDecoder extends VectorDiffStruct {
    public GenDecoder() {
        super(32, 562, 2, 52, 52);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 512; i++)
                w[i] = 0.17677669529663687 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 528; i < 560; i++)
                w[i] = 0.25 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++)
                f[i * 16 + k + 0] += x[i * 32 + j + 0] * w[j * 16 + k + 0];
        }
        {
            int ap = 0, bp = 512;
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
            for (int k = 0; k < 2; k++)
                f[i * 2 + k + 48] += f[i * 16 + j + 32] * w[j * 2 + k + 528];
        }
        {
            int ap = 48, bp = 560;
            for (int cp = 50; cp < 52; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 50, yp = 0;
            for (int i = 0; i < 2; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 50, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 2; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 2, dbp = 560;
            for (int dcp = 0; dcp < 2; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 16; j++)
            for (int k = 0; k < 2; k++) {
                b[i * 16 + j + 4] += b[i * 2 + k + 2] * w[j * 2 + k + 528];
                dw[j * 2 + k + 528] += f[i * 16 + j + 32] * b[i * 2 + k + 2];
            }
        }
        {
            int xp = 16, yp = 32, dxp = 20, dyp = 4;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 36, dbp = 512;
            for (int dcp = 20; dcp < 36; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++) {
                dx[i * 32 + j + 0] += b[i * 16 + k + 36] * w[j * 16 + k + 0];
                dw[j * 16 + k + 0] += x[i * 32 + j + 0] * b[i * 16 + k + 36];
            }
        }
    }
}
