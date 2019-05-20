package test.bestcnct;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Decoder extends VectorDiffStruct {
    public Decoder() {
        super(69, 3260, 3, 201, 201);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 2208; i++)
                w[i] = 0.1203858530857692 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2240; i < 2880; i++)
                w[i] = 0.17677669529663687 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2900; i < 3200; i++)
                w[i] = 0.22360679774997896 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 3215; i < 3260; i++)
                w[i] = 0.2581988897471611 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 69; j++)
            for (int k = 0; k < 32; k++)
                f[i * 32 + k + 0] += x[i * 69 + j + 0] * w[j * 32 + k + 0];
        }
        {
            int ap = 0, bp = 2208;
            for (int cp = 32; cp < 64; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 32, yp = 64;
            for (int i = 0; i < 32; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 96] += f[i * 32 + j + 64] * w[j * 20 + k + 2240];
        }
        {
            int ap = 96, bp = 2880;
            for (int cp = 116; cp < 136; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 116, yp = 136;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++)
                f[i * 15 + k + 156] += f[i * 20 + j + 136] * w[j * 15 + k + 2900];
        }
        {
            int ap = 156, bp = 3200;
            for (int cp = 171; cp < 186; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 171, yp = 186;
            for (int i = 0; i < 15; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++)
                y[i * 3 + k + 0] += f[i * 15 + j + 186] * w[j * 3 + k + 3215];
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++) {
                b[i * 15 + j + 0] += dy[i * 3 + k + 0] * w[j * 3 + k + 3215];
                dw[j * 3 + k + 3215] += f[i * 15 + j + 186] * dy[i * 3 + k + 0];
            }
        }
        {
            int xp = 171, yp = 186, dxp = 15, dyp = 0;
            for (int i = 0; i < 15; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 30, dbp = 3200;
            for (int dcp = 15; dcp < 30; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++) {
                b[i * 20 + j + 45] += b[i * 15 + k + 30] * w[j * 15 + k + 2900];
                dw[j * 15 + k + 2900] += f[i * 20 + j + 136] * b[i * 15 + k + 30];
            }
        }
        {
            int xp = 116, yp = 136, dxp = 65, dyp = 45;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 85, dbp = 2880;
            for (int dcp = 65; dcp < 85; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 32 + j + 105] += b[i * 20 + k + 85] * w[j * 20 + k + 2240];
                dw[j * 20 + k + 2240] += f[i * 32 + j + 64] * b[i * 20 + k + 85];
            }
        }
        {
            int xp = 32, yp = 64, dxp = 137, dyp = 105;
            for (int i = 0; i < 32; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 169, dbp = 2208;
            for (int dcp = 137; dcp < 169; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 69; j++)
            for (int k = 0; k < 32; k++) {
                dx[i * 69 + j + 0] += b[i * 32 + k + 169] * w[j * 32 + k + 0];
                dw[j * 32 + k + 0] += x[i * 69 + j + 0] * b[i * 32 + k + 169];
            }
        }
    }
}
