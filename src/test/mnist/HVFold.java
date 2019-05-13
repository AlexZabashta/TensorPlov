package test.mnist;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class HVFold extends VectorDiffStruct {
    public HVFold() {
        super(40, 5000, 40, 460, 460);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 40; i < 2440; i++)
                w[i] = 0.15811388300841897 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2560; i < 4960; i++)
                w[i] = 0.12909944487358055 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            int xp = 0, sp = 0;
            for (int yp = 0; yp < 40; yp++)
                f[yp] += x[xp++] * exp(w[sp++]);
        }
        {
            for (int xp = 0, yp = 40; yp < 80; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 60; k++)
                f[i * 60 + k + 80] += f[i * 40 + j + 40] * w[j * 60 + k + 40];
        }
        {
            int ap = 80, bp = 2440;
            for (int cp = 140; cp < 200; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 140, yp = 200;
            for (int i = 0; i < 60; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 200, mp = 2500;
            for (int yp = 260; yp < 320; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int xp = 260, yp = 320; yp < 380; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 60; j++)
            for (int k = 0; k < 40; k++)
                f[i * 40 + k + 380] += f[i * 60 + j + 320] * w[j * 40 + k + 2560];
        }
        {
            int ap = 380, bp = 4960;
            for (int cp = 420; cp < 460; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 420, yp = 0;
            for (int i = 0; i < 40; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 420, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 40; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 40, dbp = 4960;
            for (int dcp = 0; dcp < 40; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 60; j++)
            for (int k = 0; k < 40; k++) {
                b[i * 60 + j + 80] += b[i * 40 + k + 40] * w[j * 40 + k + 2560];
                dw[j * 40 + k + 2560] += f[i * 60 + j + 320] * b[i * 40 + k + 40];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 80; i < 140; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 140, dyp = 80; dxp < 200; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 200, mp = 2500, dmp = 2500, dyp = 140;
            for (int dxp = 200; dxp < 260; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 140, yp = 200, dxp = 260, dyp = 200;
            for (int i = 0; i < 60; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 320, dbp = 2440;
            for (int dcp = 260; dcp < 320; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 60; k++) {
                b[i * 40 + j + 380] += b[i * 60 + k + 320] * w[j * 60 + k + 40];
                dw[j * 60 + k + 40] += f[i * 40 + j + 40] * b[i * 60 + k + 320];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 380; i < 420; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 420, dyp = 380; dxp < 460; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int sp = 0, dxp = 0, dsp = 0, dyp = 420;
            for (int yp = 0; yp < 40; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += 0.01 * (sq * (sq - 1));
                dx[dxp++] += b[dyp++] * exp(w[sp++]);
            }
        }
    }
}
