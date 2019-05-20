package test.best;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Decoder extends VectorDiffStruct {
    public Decoder() {
        super(40, 2149, 3, 192, 192);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 1160; i++)
                w[i] = 0.15811388300841897 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1189; i < 1769; i++)
                w[i] = 0.18569533817705186 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1789; i < 2089; i++)
                w[i] = 0.22360679774997896 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2104; i < 2149; i++)
                w[i] = 0.2581988897471611 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 29; k++)
                f[i * 29 + k + 0] += x[i * 40 + j + 0] * w[j * 29 + k + 0];
        }
        {
            int ap = 0, bp = 1160;
            for (int cp = 29; cp < 58; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 29, yp = 58;
            for (int i = 0; i < 29; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 87] += f[i * 29 + j + 58] * w[j * 20 + k + 1189];
        }
        {
            int ap = 87, bp = 1769;
            for (int cp = 107; cp < 127; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 107, yp = 127;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++)
                f[i * 15 + k + 147] += f[i * 20 + j + 127] * w[j * 15 + k + 1789];
        }
        {
            int ap = 147, bp = 2089;
            for (int cp = 162; cp < 177; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 162, yp = 177;
            for (int i = 0; i < 15; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++)
                y[i * 3 + k + 0] += f[i * 15 + j + 177] * w[j * 3 + k + 2104];
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++) {
                b[i * 15 + j + 0] += dy[i * 3 + k + 0] * w[j * 3 + k + 2104];
                dw[j * 3 + k + 2104] += f[i * 15 + j + 177] * dy[i * 3 + k + 0];
            }
        }
        {
            int xp = 162, yp = 177, dxp = 15, dyp = 0;
            for (int i = 0; i < 15; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 30, dbp = 2089;
            for (int dcp = 15; dcp < 30; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++) {
                b[i * 20 + j + 45] += b[i * 15 + k + 30] * w[j * 15 + k + 1789];
                dw[j * 15 + k + 1789] += f[i * 20 + j + 127] * b[i * 15 + k + 30];
            }
        }
        {
            int xp = 107, yp = 127, dxp = 65, dyp = 45;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 85, dbp = 1769;
            for (int dcp = 65; dcp < 85; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 29 + j + 105] += b[i * 20 + k + 85] * w[j * 20 + k + 1189];
                dw[j * 20 + k + 1189] += f[i * 29 + j + 58] * b[i * 20 + k + 85];
            }
        }
        {
            int xp = 29, yp = 58, dxp = 134, dyp = 105;
            for (int i = 0; i < 29; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 163, dbp = 1160;
            for (int dcp = 134; dcp < 163; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 29; k++) {
                dx[i * 40 + j + 0] += b[i * 29 + k + 163] * w[j * 29 + k + 0];
                dw[j * 29 + k + 0] += x[i * 40 + j + 0] * b[i * 29 + k + 163];
            }
        }
    }
}
