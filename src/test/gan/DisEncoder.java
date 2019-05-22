package test.gan;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class DisEncoder extends VectorDiffStruct {
    public DisEncoder() {
        super(2, 592, 32, 112, 112);
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
            for (int i = 48; i < 560; i++)
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
            for (int k = 0; k < 32; k++)
                f[i * 32 + k + 48] += f[i * 16 + j + 32] * w[j * 32 + k + 48];
        }
        {
            int ap = 48, bp = 560;
            for (int cp = 80; cp < 112; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 80, yp = 0;
            for (int i = 0; i < 32; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 80, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 32; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 32, dbp = 560;
            for (int dcp = 0; dcp < 32; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 16; j++)
            for (int k = 0; k < 32; k++) {
                b[i * 16 + j + 64] += b[i * 32 + k + 32] * w[j * 32 + k + 48];
                dw[j * 32 + k + 48] += f[i * 16 + j + 32] * b[i * 32 + k + 32];
            }
        }
        {
            int xp = 16, yp = 32, dxp = 80, dyp = 64;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 96, dbp = 32;
            for (int dcp = 80; dcp < 96; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 16; k++) {
                dx[i * 2 + j + 0] += b[i * 16 + k + 96] * w[j * 16 + k + 0];
                dw[j * 16 + k + 0] += x[i * 2 + j + 0] * b[i * 16 + k + 96];
            }
        }
    }
}
