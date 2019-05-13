import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Encoder extends VectorDiffStruct {
    public Encoder() {
        super(2, 1054, 40, 199, 199);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 2; i < 48; i++)
                w[i] = 0.7071067811865475 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 94; i < 1014; i++)
                w[i] = 0.20851441405707477 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            int xp = 0, sp = 0;
            for (int yp = 0; yp < 2; yp++)
                f[yp] += x[xp++] * exp(w[sp++]);
        }
        {
            for (int xp = 0, yp = 2; yp < 4; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 23; k++)
                f[i * 23 + k + 4] += f[i * 2 + j + 2] * w[j * 23 + k + 2];
        }
        {
            int ap = 4, bp = 48;
            for (int cp = 27; cp < 50; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 27, yp = 50;
            for (int i = 0; i < 23; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 50, mp = 71;
            for (int yp = 73; yp < 96; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int xp = 73, yp = 96; yp < 119; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 23; j++)
            for (int k = 0; k < 40; k++)
                f[i * 40 + k + 119] += f[i * 23 + j + 96] * w[j * 40 + k + 94];
        }
        {
            int ap = 119, bp = 1014;
            for (int cp = 159; cp < 199; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 159, yp = 0;
            for (int i = 0; i < 40; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 159, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 40; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 40, dbp = 1014;
            for (int dcp = 0; dcp < 40; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 23; j++)
            for (int k = 0; k < 40; k++) {
                b[i * 23 + j + 80] += b[i * 40 + k + 40] * w[j * 40 + k + 94];
                dw[j * 40 + k + 94] += f[i * 23 + j + 96] * b[i * 40 + k + 40];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 80; i < 103; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 103, dyp = 80; dxp < 126; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 50, mp = 71, dmp = 71, dyp = 103;
            for (int dxp = 126; dxp < 149; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 27, yp = 50, dxp = 149, dyp = 126;
            for (int i = 0; i < 23; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 172, dbp = 48;
            for (int dcp = 149; dcp < 172; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 23; k++) {
                b[i * 2 + j + 195] += b[i * 23 + k + 172] * w[j * 23 + k + 2];
                dw[j * 23 + k + 2] += f[i * 2 + j + 2] * b[i * 23 + k + 172];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 195; i < 197; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 197, dyp = 195; dxp < 199; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int sp = 0, dxp = 0, dsp = 0, dyp = 197;
            for (int yp = 0; yp < 2; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += 0.01 * (sq * (sq - 1));
                dx[dxp++] += b[dyp++] * exp(w[sp++]);
            }
        }
    }
}
