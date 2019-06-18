package test.best;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Simple extends VectorDiffStruct {
    public Simple() {
        super(23, 822, 3, 99, 99);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 529; i++)
                w[i] = 0.20851441405707477 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 552; i < 782; i++)
                w[i] = 0.20851441405707477 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 792; i < 822; i++)
                w[i] = 0.31622776601683794 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 23; j++)
            for (int k = 0; k < 23; k++)
                f[i * 23 + k + 0] += x[i * 23 + j + 0] * w[j * 23 + k + 0];
        }
        {
            int ap = 0, bp = 529;
            for (int cp = 23; cp < 46; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 23, yp = 46;
            for (int i = 0; i < 23; i++, xp++) {
                f[yp++] += max(0.01 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 23; j++)
            for (int k = 0; k < 10; k++)
                f[i * 10 + k + 69] += f[i * 23 + j + 46] * w[j * 10 + k + 552];
        }
        {
            int ap = 69, bp = 782;
            for (int cp = 79; cp < 89; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 79, yp = 89;
            for (int i = 0; i < 10; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 3; k++)
                y[i * 3 + k + 0] += f[i * 10 + j + 89] * w[j * 3 + k + 792];
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 3; k++) {
                b[i * 10 + j + 0] += dy[i * 3 + k + 0] * w[j * 3 + k + 792];
                dw[j * 3 + k + 792] += f[i * 10 + j + 89] * dy[i * 3 + k + 0];
            }
        }
        {
            int xp = 79, yp = 89, dxp = 10, dyp = 0;
            for (int i = 0; i < 10; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 20, dbp = 782;
            for (int dcp = 10; dcp < 20; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 23; j++)
            for (int k = 0; k < 10; k++) {
                b[i * 23 + j + 30] += b[i * 10 + k + 20] * w[j * 10 + k + 552];
                dw[j * 10 + k + 552] += f[i * 23 + j + 46] * b[i * 10 + k + 20];
            }
        }
        {
            int xp = 23, yp = 46, dxp = 53, dyp = 30;
            for (int i = 0; i < 23; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.01 : 1);
            }
        }
        {
            int dap = 76, dbp = 529;
            for (int dcp = 53; dcp < 76; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 23; j++)
            for (int k = 0; k < 23; k++) {
                dx[i * 23 + j + 0] += b[i * 23 + k + 76] * w[j * 23 + k + 0];
                dw[j * 23 + k + 0] += x[i * 23 + j + 0] * b[i * 23 + k + 76];
            }
        }
    }
}
