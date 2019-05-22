package test.gan;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class GenEncoder extends VectorDiffStruct {
    public GenEncoder() {
        super(46, 3113, 32, 181, 181);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 1794; i++)
                w[i] = 0.14744195615489714 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1833; i < 3081; i++)
                w[i] = 0.16012815380508713 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 46; j++)
            for (int k = 0; k < 39; k++)
                f[i * 39 + k + 0] += x[i * 46 + j + 0] * w[j * 39 + k + 0];
        }
        {
            int ap = 0, bp = 1794;
            for (int cp = 39; cp < 78; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 39, yp = 78;
            for (int i = 0; i < 39; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 39; j++)
            for (int k = 0; k < 32; k++)
                f[i * 32 + k + 117] += f[i * 39 + j + 78] * w[j * 32 + k + 1833];
        }
        {
            int ap = 117, bp = 3081;
            for (int cp = 149; cp < 181; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 149, yp = 0;
            for (int i = 0; i < 32; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 149, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 32; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 32, dbp = 3081;
            for (int dcp = 0; dcp < 32; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 39; j++)
            for (int k = 0; k < 32; k++) {
                b[i * 39 + j + 64] += b[i * 32 + k + 32] * w[j * 32 + k + 1833];
                dw[j * 32 + k + 1833] += f[i * 39 + j + 78] * b[i * 32 + k + 32];
            }
        }
        {
            int xp = 39, yp = 78, dxp = 103, dyp = 64;
            for (int i = 0; i < 39; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 142, dbp = 1794;
            for (int dcp = 103; dcp < 142; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 46; j++)
            for (int k = 0; k < 39; k++) {
                dx[i * 46 + j + 0] += b[i * 39 + k + 142] * w[j * 39 + k + 0];
                dw[j * 39 + k + 0] += x[i * 46 + j + 0] * b[i * 39 + k + 142];
            }
        }
    }
}
