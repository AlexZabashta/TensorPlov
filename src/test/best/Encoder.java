package test.best;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Encoder extends VectorDiffStruct {
    public Encoder() {
        super(2, 2754, 64, 324, 324);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 2; i < 34; i++)
                w[i] = 0.7071067811865475 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 66; i < 578; i++)
                w[i] = 0.25 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 642; i < 2690; i++)
                w[i] = 0.17677669529663687 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int xp = 0, yp = 0; yp < 2; yp++)
                f[yp] += x[xp++];
        }
        {
            int xp = 0, sp = 0;
            for (int yp = 2; yp < 4; yp++)
                f[yp] += f[xp++] * exp(w[sp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 16; k++)
                f[i * 16 + k + 4] += f[i * 2 + j + 2] * w[j * 16 + k + 2];
        }
        {
            int ap = 4, bp = 34;
            for (int cp = 20; cp < 36; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 20, yp = 36;
            for (int i = 0; i < 16; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 36, mp = 50;
            for (int yp = 52; yp < 68; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 16; j++)
            for (int k = 0; k < 32; k++)
                f[i * 32 + k + 68] += f[i * 16 + j + 52] * w[j * 32 + k + 66];
        }
        {
            int ap = 68, bp = 578;
            for (int cp = 100; cp < 132; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 100, yp = 132;
            for (int i = 0; i < 32; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 132, mp = 610;
            for (int yp = 164; yp < 196; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 64; k++)
                f[i * 64 + k + 196] += f[i * 32 + j + 164] * w[j * 64 + k + 642];
        }
        {
            int ap = 196, bp = 2690;
            for (int cp = 260; cp < 324; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 260, yp = 0;
            for (int i = 0; i < 64; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 260, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 64; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 64, dbp = 2690;
            for (int dcp = 0; dcp < 64; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 64; k++) {
                b[i * 32 + j + 128] += b[i * 64 + k + 64] * w[j * 64 + k + 642];
                dw[j * 64 + k + 642] += f[i * 32 + j + 164] * b[i * 64 + k + 64];
            }
        }
        {
            int xp = 132, mp = 610, dmp = 610, dyp = 128;
            for (int dxp = 160; dxp < 192; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 100, yp = 132, dxp = 192, dyp = 160;
            for (int i = 0; i < 32; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 224, dbp = 578;
            for (int dcp = 192; dcp < 224; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 16; j++)
            for (int k = 0; k < 32; k++) {
                b[i * 16 + j + 256] += b[i * 32 + k + 224] * w[j * 32 + k + 66];
                dw[j * 32 + k + 66] += f[i * 16 + j + 52] * b[i * 32 + k + 224];
            }
        }
        {
            int xp = 36, mp = 50, dmp = 50, dyp = 256;
            for (int dxp = 272; dxp < 288; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 20, yp = 36, dxp = 288, dyp = 272;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 304, dbp = 34;
            for (int dcp = 288; dcp < 304; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 16; k++) {
                b[i * 2 + j + 320] += b[i * 16 + k + 304] * w[j * 16 + k + 2];
                dw[j * 16 + k + 2] += f[i * 2 + j + 2] * b[i * 16 + k + 304];
            }
        }
        {
            int sp = 0, dxp = 322, dsp = 0, dyp = 320;
            for (int yp = 2; yp < 4; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += 0.01 * (sq * (sq - 1));
                b[dxp++] += b[dyp++] * exp(w[sp++]);
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 322; i < 324; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 0, dyp = 322; dxp < 2; dxp++)
                dx[dxp] += b[dyp++] * inv;
        }
    }
}
