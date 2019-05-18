package test.meta;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Encoder extends VectorDiffStruct {
    public Encoder() {
        super(2, 2704, 64, 272, 272);
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
        {
            Random random = new Random();
            for (int i = 592; i < 2640; i++)
                w[i] = 0.17677669529663687 * random.nextGaussian();
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
            int xp = 80, yp = 112;
            for (int i = 0; i < 32; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 64; k++)
                f[i * 64 + k + 144] += f[i * 32 + j + 112] * w[j * 64 + k + 592];
        }
        {
            int ap = 144, bp = 2640;
            for (int cp = 208; cp < 272; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 208, yp = 0;
            for (int i = 0; i < 64; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 208, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 64; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 64, dbp = 2640;
            for (int dcp = 0; dcp < 64; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 64; k++) {
                b[i * 32 + j + 128] += b[i * 64 + k + 64] * w[j * 64 + k + 592];
                dw[j * 64 + k + 592] += f[i * 32 + j + 112] * b[i * 64 + k + 64];
            }
        }
        {
            int xp = 80, yp = 112, dxp = 160, dyp = 128;
            for (int i = 0; i < 32; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 192, dbp = 560;
            for (int dcp = 160; dcp < 192; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 16; j++)
            for (int k = 0; k < 32; k++) {
                b[i * 16 + j + 224] += b[i * 32 + k + 192] * w[j * 32 + k + 48];
                dw[j * 32 + k + 48] += f[i * 16 + j + 32] * b[i * 32 + k + 192];
            }
        }
        {
            int xp = 16, yp = 32, dxp = 240, dyp = 224;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 256, dbp = 32;
            for (int dcp = 240; dcp < 256; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 16; k++) {
                dx[i * 2 + j + 0] += b[i * 16 + k + 256] * w[j * 16 + k + 0];
                dw[j * 16 + k + 0] += x[i * 2 + j + 0] * b[i * 16 + k + 256];
            }
        }
    }
}
