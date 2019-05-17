package test.best;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Decoder extends VectorDiffStruct {
    public Decoder() {
        super(64, 2976, 3, 390, 390);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 64; i < 1920; i++)
                w[i] = 0.125 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1978; i < 2558; i++)
                w[i] = 0.18569533817705186 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2598; i < 2898; i++)
                w[i] = 0.22360679774997896 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2928; i < 2973; i++)
                w[i] = 0.2581988897471611 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int xp = 0, yp = 0; yp < 64; yp++)
                f[yp] += x[xp++];
        }
        {
            int xp = 0, sp = 0;
            for (int yp = 64; yp < 128; yp++)
                f[yp] += f[xp++] * exp(w[sp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 64; j++)
            for (int k = 0; k < 29; k++)
                f[i * 29 + k + 128] += f[i * 64 + j + 64] * w[j * 29 + k + 64];
        }
        {
            int ap = 128, bp = 1920;
            for (int cp = 157; cp < 186; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 157, yp = 186;
            for (int i = 0; i < 29; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 186, mp = 1949;
            for (int yp = 215; yp < 244; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 244] += f[i * 29 + j + 215] * w[j * 20 + k + 1978];
        }
        {
            int ap = 244, bp = 2558;
            for (int cp = 264; cp < 284; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 264, yp = 284;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 284, mp = 2578;
            for (int yp = 304; yp < 324; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++)
                f[i * 15 + k + 324] += f[i * 20 + j + 304] * w[j * 15 + k + 2598];
        }
        {
            int ap = 324, bp = 2898;
            for (int cp = 339; cp < 354; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 339, yp = 354;
            for (int i = 0; i < 15; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 354, mp = 2913;
            for (int yp = 369; yp < 384; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++)
                f[i * 3 + k + 384] += f[i * 15 + j + 369] * w[j * 3 + k + 2928];
        }
        {
            int ap = 384, bp = 2973;
            for (int cp = 387; cp < 390; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 387, yp = 0;
            for (int i = 0; i < 3; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 387, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 3; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 3, dbp = 2973;
            for (int dcp = 0; dcp < 3; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++) {
                b[i * 15 + j + 6] += b[i * 3 + k + 3] * w[j * 3 + k + 2928];
                dw[j * 3 + k + 2928] += f[i * 15 + j + 369] * b[i * 3 + k + 3];
            }
        }
        {
            int xp = 354, mp = 2913, dmp = 2913, dyp = 6;
            for (int dxp = 21; dxp < 36; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 339, yp = 354, dxp = 36, dyp = 21;
            for (int i = 0; i < 15; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 51, dbp = 2898;
            for (int dcp = 36; dcp < 51; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++) {
                b[i * 20 + j + 66] += b[i * 15 + k + 51] * w[j * 15 + k + 2598];
                dw[j * 15 + k + 2598] += f[i * 20 + j + 304] * b[i * 15 + k + 51];
            }
        }
        {
            int xp = 284, mp = 2578, dmp = 2578, dyp = 66;
            for (int dxp = 86; dxp < 106; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 264, yp = 284, dxp = 106, dyp = 86;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 126, dbp = 2558;
            for (int dcp = 106; dcp < 126; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 29 + j + 146] += b[i * 20 + k + 126] * w[j * 20 + k + 1978];
                dw[j * 20 + k + 1978] += f[i * 29 + j + 215] * b[i * 20 + k + 126];
            }
        }
        {
            int xp = 186, mp = 1949, dmp = 1949, dyp = 146;
            for (int dxp = 175; dxp < 204; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 157, yp = 186, dxp = 204, dyp = 175;
            for (int i = 0; i < 29; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 233, dbp = 1920;
            for (int dcp = 204; dcp < 233; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 64; j++)
            for (int k = 0; k < 29; k++) {
                b[i * 64 + j + 262] += b[i * 29 + k + 233] * w[j * 29 + k + 64];
                dw[j * 29 + k + 64] += f[i * 64 + j + 64] * b[i * 29 + k + 233];
            }
        }
        {
            int sp = 0, dxp = 326, dsp = 0, dyp = 262;
            for (int yp = 64; yp < 128; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += 0.01 * (sq * (sq - 1));
                b[dxp++] += b[dyp++] * exp(w[sp++]);
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 326; i < 390; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 0, dyp = 326; dxp < 64; dxp++)
                dx[dxp] += b[dyp++] * inv;
        }
    }
}
