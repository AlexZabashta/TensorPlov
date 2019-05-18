package test.bestcnct;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Simple extends VectorDiffStruct {
    public Simple() {
        super(29, 963, 3, 111, 111);
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
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++)
                f[i * 3 + k + 105] += f[i * 15 + j + 90] * w[j * 3 + k + 915];
        }
        {
            int ap = 105, bp = 960;
            for (int cp = 108; cp < 111; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 108, yp = 0;
            for (int i = 0; i < 3; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 108, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 3; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 3, dbp = 960;
            for (int dcp = 0; dcp < 3; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++) {
                b[i * 15 + j + 6] += b[i * 3 + k + 3] * w[j * 3 + k + 915];
                dw[j * 3 + k + 915] += f[i * 15 + j + 90] * b[i * 3 + k + 3];
            }
        }
        {
            int xp = 75, yp = 90, dxp = 21, dyp = 6;
            for (int i = 0; i < 15; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 36, dbp = 900;
            for (int dcp = 21; dcp < 36; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++) {
                b[i * 20 + j + 51] += b[i * 15 + k + 36] * w[j * 15 + k + 600];
                dw[j * 15 + k + 600] += f[i * 20 + j + 40] * b[i * 15 + k + 36];
            }
        }
        {
            int xp = 20, yp = 40, dxp = 71, dyp = 51;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 91, dbp = 580;
            for (int dcp = 71; dcp < 91; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++) {
                dx[i * 29 + j + 0] += b[i * 20 + k + 91] * w[j * 20 + k + 0];
                dw[j * 20 + k + 0] += x[i * 29 + j + 0] * b[i * 20 + k + 91];
            }
        }
    }
}
