package test.mnist;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Encoder extends VectorDiffStruct {
    public Encoder() {
        super(9, 2432, 64, 265, 265);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 288; i++)
                w[i] = 0.3333333333333333 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 320; i < 2368; i++)
                w[i] = 0.17677669529663687 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int xp = 0, yp = 0; yp < 9; yp++)
                f[yp] += x[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 9; j++)
            for (int k = 0; k < 32; k++)
                f[i * 32 + k + 9] += f[i * 9 + j + 0] * w[j * 32 + k + 0];
        }
        {
            int ap = 9, bp = 288;
            for (int cp = 41; cp < 73; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 41, yp = 73;
            for (int i = 0; i < 32; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int xp = 73, yp = 105; yp < 137; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 64; k++)
                f[i * 64 + k + 137] += f[i * 32 + j + 105] * w[j * 64 + k + 320];
        }
        {
            int ap = 137, bp = 2368;
            for (int cp = 201; cp < 265; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 201, yp = 0;
            for (int i = 0; i < 64; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 201, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 64; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 64, dbp = 2368;
            for (int dcp = 0; dcp < 64; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 64; k++) {
                b[i * 32 + j + 128] += b[i * 64 + k + 64] * w[j * 64 + k + 320];
                dw[j * 64 + k + 320] += f[i * 32 + j + 105] * b[i * 64 + k + 64];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 128; i < 160; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 160, dyp = 128; dxp < 192; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 41, yp = 73, dxp = 192, dyp = 160;
            for (int i = 0; i < 32; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 224, dbp = 288;
            for (int dcp = 192; dcp < 224; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 9; j++)
            for (int k = 0; k < 32; k++) {
                b[i * 9 + j + 256] += b[i * 32 + k + 224] * w[j * 32 + k + 0];
                dw[j * 32 + k + 0] += f[i * 9 + j + 0] * b[i * 32 + k + 224];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 256; i < 265; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 0, dyp = 256; dxp < 9; dxp++)
                dx[dxp] += b[dyp++] * inv;
        }
    }
}
