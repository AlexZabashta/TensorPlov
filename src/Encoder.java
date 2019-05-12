import static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;

public class Encoder extends VectorDiffStruct {
    public Encoder() {
        super(9, 1426, 40, 233, 233);
    }

    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 9; i < 252; i++)
                w[i] = 0.3333333333333333 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 306; i < 1386; i++)
                w[i] = 0.19245008972987526 * random.nextGaussian();
        }
    }

    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            int xp = 0, mp = 0;
            for (int yp = 0; yp < 9; yp++)
                f[yp] += (x[xp++] - w[mp++]);
        }
        {
            for (int xp = 0, yp = 9; yp < 18; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 9; j++)
                    for (int k = 0; k < 27; k++)
                        f[i * 27 + k + 18] += f[i * 9 + j + 9] * w[j * 27 + k + 9];
        }
        {
            int ap = 18, bp = 252;
            for (int cp = 45; cp < 72; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 45, yp = 72;
            for (int i = 0; i < 27; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int xp = 72, mp = 279;
            for (int yp = 99; yp < 126; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int xp = 99, yp = 126; yp < 153; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 27; j++)
                    for (int k = 0; k < 40; k++)
                        f[i * 40 + k + 153] += f[i * 27 + j + 126] * w[j * 40 + k + 306];
        }
        {
            int ap = 153, bp = 1386;
            for (int cp = 193; cp < 233; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 193, yp = 0;
            for (int i = 0; i < 40; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }

    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 193, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 40; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 40, dbp = 1386;
            for (int dcp = 0; dcp < 40; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 27; j++)
                    for (int k = 0; k < 40; k++) {
                        b[i * 27 + j + 80] += b[i * 40 + k + 40] * w[j * 40 + k + 306];
                        dw[j * 40 + k + 306] += f[i * 27 + j + 126] * b[i * 40 + k + 40];
                    }
        }
        {
            double sum = 0.0000001;
            for (int i = 80; i < 107; i++)
                sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 107, dyp = 80; dxp < 134; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 72, mp = 279, dmp = 279, dyp = 107;
            for (int dxp = 134; dxp < 161; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 45, yp = 72, dxp = 161, dyp = 134;
            for (int i = 0; i < 27; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 188, dbp = 252;
            for (int dcp = 161; dcp < 188; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 9; j++)
                    for (int k = 0; k < 27; k++) {
                        b[i * 9 + j + 215] += b[i * 27 + k + 188] * w[j * 27 + k + 9];
                        dw[j * 27 + k + 9] += f[i * 9 + j + 9] * b[i * 27 + k + 188];
                    }
        }
        {
            double sum = 0.0000001;
            for (int i = 215; i < 224; i++)
                sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 224, dyp = 215; dxp < 233; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 0, mp = 0, dmp = 0, dyp = 224;
            for (int dxp = 0; dxp < 9; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - x[xp++]);
                dx[dxp] += b[dyp++];
            }
        }
    }
}
