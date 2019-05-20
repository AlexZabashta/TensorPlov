package test.bestcnct;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Simple extends VectorDiffStruct {
    public Simple() {
        super(29, 960, 3, 105, 105);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 580; i++)
                w[i] = 0.18569533817705186 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 600; i < 900; i++)
                w[i] = 0.22360679774997896 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 915; i < 960; i++)
                w[i] = 0.2581988897471611 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 0] += x[i * 29 + j + 0] * w[j * 20 + k + 0];
        }
        {
            int ap = 0, bp = 580;
            for (int cp = 20; cp < 40; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 20, yp = 40;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++)
                f[i * 15 + k + 60] += f[i * 20 + j + 40] * w[j * 15 + k + 600];
        }
        {
            int ap = 60, bp = 900;
            for (int cp = 75; cp < 90; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 75, yp = 90;
            for (int i = 0; i < 15; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++)
                y[i * 3 + k + 0] += f[i * 15 + j + 90] * w[j * 3 + k + 915];
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++) {
                b[i * 15 + j + 0] += dy[i * 3 + k + 0] * w[j * 3 + k + 915];
                dw[j * 3 + k + 915] += f[i * 15 + j + 90] * dy[i * 3 + k + 0];
            }
        }
        {
            int xp = 75, yp = 90, dxp = 15, dyp = 0;
            for (int i = 0; i < 15; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 30, dbp = 900;
            for (int dcp = 15; dcp < 30; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++) {
                b[i * 20 + j + 45] += b[i * 15 + k + 30] * w[j * 15 + k + 600];
                dw[j * 15 + k + 600] += f[i * 20 + j + 40] * b[i * 15 + k + 30];
            }
        }
        {
            int xp = 20, yp = 40, dxp = 65, dyp = 45;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 85, dbp = 580;
            for (int dcp = 65; dcp < 85; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++) {
                dx[i * 29 + j + 0] += b[i * 20 + k + 85] * w[j * 20 + k + 0];
                dw[j * 20 + k + 0] += x[i * 29 + j + 0] * b[i * 20 + k + 85];
            }
        }
    }
}
