package test.best;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class HVFold extends VectorDiffStruct {
    public HVFold() {
        super(128, 18816, 64, 768, 768);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 128; i < 12416; i++)
                w[i] = 0.08838834764831843 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 12608; i < 18752; i++)
                w[i] = 0.10206207261596577 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int xp = 0, yp = 0; yp < 128; yp++)
                f[yp] += x[xp++];
        }
        {
            int xp = 0, sp = 0;
            for (int yp = 128; yp < 256; yp++)
                f[yp] += f[xp++] * exp(w[sp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 128; j++)
            for (int k = 0; k < 96; k++)
                f[i * 96 + k + 256] += f[i * 128 + j + 128] * w[j * 96 + k + 128];
        }
        {
            int ap = 256, bp = 12416;
            for (int cp = 352; cp < 448; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 352, yp = 448;
            for (int i = 0; i < 96; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 448, mp = 12512;
            for (int yp = 544; yp < 640; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 96; j++)
            for (int k = 0; k < 64; k++)
                f[i * 64 + k + 640] += f[i * 96 + j + 544] * w[j * 64 + k + 12608];
        }
        {
            int ap = 640, bp = 18752;
            for (int cp = 704; cp < 768; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 704, yp = 0;
            for (int i = 0; i < 64; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 704, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 64; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 64, dbp = 18752;
            for (int dcp = 0; dcp < 64; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 96; j++)
            for (int k = 0; k < 64; k++) {
                b[i * 96 + j + 128] += b[i * 64 + k + 64] * w[j * 64 + k + 12608];
                dw[j * 64 + k + 12608] += f[i * 96 + j + 544] * b[i * 64 + k + 64];
            }
        }
        {
            int xp = 448, mp = 12512, dmp = 12512, dyp = 128;
            for (int dxp = 224; dxp < 320; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 352, yp = 448, dxp = 320, dyp = 224;
            for (int i = 0; i < 96; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 416, dbp = 12416;
            for (int dcp = 320; dcp < 416; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 128; j++)
            for (int k = 0; k < 96; k++) {
                b[i * 128 + j + 512] += b[i * 96 + k + 416] * w[j * 96 + k + 128];
                dw[j * 96 + k + 128] += f[i * 128 + j + 128] * b[i * 96 + k + 416];
            }
        }
        {
            int sp = 0, dxp = 640, dsp = 0, dyp = 512;
            for (int yp = 128; yp < 256; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += 0.01 * (sq * (sq - 1));
                b[dxp++] += b[dyp++] * exp(w[sp++]);
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 640; i < 768; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 0, dyp = 640; dxp < 128; dxp++)
                dx[dxp] += b[dyp++] * inv;
        }
    }
}
