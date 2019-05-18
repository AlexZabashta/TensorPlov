package test.bestcnct;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Decoder extends VectorDiffStruct {
    public Decoder() {
        super(64, 2848, 3, 198, 198);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 1856; i++)
                w[i] = 0.125 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1885; i < 2465; i++)
                w[i] = 0.18569533817705186 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2485; i < 2785; i++)
                w[i] = 0.22360679774997896 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2800; i < 2845; i++)
                w[i] = 0.2581988897471611 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 64; j++)
            for (int k = 0; k < 29; k++)
                f[i * 29 + k + 0] += x[i * 64 + j + 0] * w[j * 29 + k + 0];
        }
        {
            int ap = 0, bp = 1856;
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
                f[i * 20 + k + 87] += f[i * 29 + j + 58] * w[j * 20 + k + 1885];
        }
        {
            int ap = 87, bp = 2465;
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
                f[i * 15 + k + 147] += f[i * 20 + j + 127] * w[j * 15 + k + 2485];
        }
        {
            int ap = 147, bp = 2785;
            for (int cp = 162; cp < 177; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 162, yp = 177;
            for (int i = 0; i < 15; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++)
                f[i * 3 + k + 192] += f[i * 15 + j + 177] * w[j * 3 + k + 2800];
        }
        {
            int ap = 192, bp = 2845;
            for (int cp = 195; cp < 198; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 195, yp = 0;
            for (int i = 0; i < 3; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 195, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 3; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 3, dbp = 2845;
            for (int dcp = 0; dcp < 3; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++) {
                b[i * 15 + j + 6] += b[i * 3 + k + 3] * w[j * 3 + k + 2800];
                dw[j * 3 + k + 2800] += f[i * 15 + j + 177] * b[i * 3 + k + 3];
            }
        }
        {
            int xp = 162, yp = 177, dxp = 21, dyp = 6;
            for (int i = 0; i < 15; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 36, dbp = 2785;
            for (int dcp = 21; dcp < 36; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++) {
                b[i * 20 + j + 51] += b[i * 15 + k + 36] * w[j * 15 + k + 2485];
                dw[j * 15 + k + 2485] += f[i * 20 + j + 127] * b[i * 15 + k + 36];
            }
        }
        {
            int xp = 107, yp = 127, dxp = 71, dyp = 51;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 91, dbp = 2465;
            for (int dcp = 71; dcp < 91; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 29 + j + 111] += b[i * 20 + k + 91] * w[j * 20 + k + 1885];
                dw[j * 20 + k + 1885] += f[i * 29 + j + 58] * b[i * 20 + k + 91];
            }
        }
        {
            int xp = 29, yp = 58, dxp = 140, dyp = 111;
            for (int i = 0; i < 29; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 169, dbp = 1856;
            for (int dcp = 140; dcp < 169; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 64; j++)
            for (int k = 0; k < 29; k++) {
                dx[i * 64 + j + 0] += b[i * 29 + k + 169] * w[j * 29 + k + 0];
                dw[j * 29 + k + 0] += x[i * 64 + j + 0] * b[i * 29 + k + 169];
            }
        }
    }
}
