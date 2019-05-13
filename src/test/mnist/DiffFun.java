package test.mnist;
import static java.lang.Math.*;

import core.VectorDiffStruct;

public class DiffFun extends VectorDiffStruct {
    public DiffFun() {
        super(784, 153831, 10, 3722, 3722);
    }

    @Override
    public void init(double[] w) {
    }

    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            int xp = 0, mp = 0;
            for (int yp = 0; yp < 784; yp++)
                f[yp] += (x[xp++] - w[mp++]);
        }
        {
            int xp = 0, sp = 784;
            for (int yp = 784; yp < 1568; yp++)
                f[yp] += f[xp++] * exp(w[sp++]);
        }
        {
            for (int xp = 784, yp = 1568; yp < 2352; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 784; j++)
                    for (int k = 0; k < 183; k++)
                        f[i * 183 + k + 2352] += f[i * 784 + j + 1568] * w[j * 183 + k + 1568];
        }
        {
            int ap = 2352, bp = 145040;
            for (int cp = 2535; cp < 2718; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 2535, yp = 2718;
            for (int i = 0; i < 183; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int xp = 2718, mp = 145223;
            for (int yp = 2901; yp < 3084; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            int xp = 2901, sp = 145406;
            for (int yp = 3084; yp < 3267; yp++)
                f[yp] += f[xp++] * exp(w[sp++]);
        }
        {
            for (int xp = 3084, yp = 3267; yp < 3450; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 183; j++)
                    for (int k = 0; k < 42; k++)
                        f[i * 42 + k + 3450] += f[i * 183 + j + 3267] * w[j * 42 + k + 145589];
        }
        {
            int ap = 3450, bp = 153275;
            for (int cp = 3492; cp < 3534; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 3492, yp = 3534;
            for (int i = 0; i < 42; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int xp = 3534, mp = 153317;
            for (int yp = 3576; yp < 3618; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            int xp = 3576, sp = 153359;
            for (int yp = 3618; yp < 3660; yp++)
                f[yp] += f[xp++] * exp(w[sp++]);
        }
        {
            for (int xp = 3618, yp = 3660; yp < 3702; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 42; j++)
                    for (int k = 0; k < 10; k++)
                        f[i * 10 + k + 3702] += f[i * 42 + j + 3660] * w[j * 10 + k + 153401];
        }
        {
            int ap = 3702, bp = 153821;
            for (int cp = 3712; cp < 3722; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 3712, yp = 0;
            for (int i = 0; i < 10; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }

    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 3712, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 10; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 10, dbp = 153821;
            for (int dcp = 0; dcp < 10; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 42; j++)
                    for (int k = 0; k < 10; k++) {
                        b[i * 42 + j + 20] += b[i * 10 + k + 10] * w[j * 10 + k + 153401];
                        dw[j * 10 + k + 153401] += f[i * 42 + j + 3660] * b[i * 10 + k + 10];
                    }
        }
        {
            double sum = 0.0000001;
            for (int i = 20; i < 62; i++)
                sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 62, dyp = 20; dxp < 104; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int sp = 153359, dxp = 104, dsp = 153359, dyp = 62;
            for (int yp = 3618; yp < 3660; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += 0.01 * (sq * (sq - 1));
                b[dxp++] += b[dyp++] * exp(w[sp++]);
            }
        }
        {
            int xp = 3534, mp = 153317, dmp = 153317, dyp = 104;
            for (int dxp = 146; dxp < 188; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 3492, yp = 3534, dxp = 188, dyp = 146;
            for (int i = 0; i < 42; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 230, dbp = 153275;
            for (int dcp = 188; dcp < 230; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 183; j++)
                    for (int k = 0; k < 42; k++) {
                        b[i * 183 + j + 272] += b[i * 42 + k + 230] * w[j * 42 + k + 145589];
                        dw[j * 42 + k + 145589] += f[i * 183 + j + 3267] * b[i * 42 + k + 230];
                    }
        }
        {
            double sum = 0.0000001;
            for (int i = 272; i < 455; i++)
                sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 455, dyp = 272; dxp < 638; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int sp = 145406, dxp = 638, dsp = 145406, dyp = 455;
            for (int yp = 3084; yp < 3267; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += 0.01 * (sq * (sq - 1));
                b[dxp++] += b[dyp++] * exp(w[sp++]);
            }
        }
        {
            int xp = 2718, mp = 145223, dmp = 145223, dyp = 638;
            for (int dxp = 821; dxp < 1004; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 2535, yp = 2718, dxp = 1004, dyp = 821;
            for (int i = 0; i < 183; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 1187, dbp = 145040;
            for (int dcp = 1004; dcp < 1187; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 784; j++)
                    for (int k = 0; k < 183; k++) {
                        b[i * 784 + j + 1370] += b[i * 183 + k + 1187] * w[j * 183 + k + 1568];
                        dw[j * 183 + k + 1568] += f[i * 784 + j + 1568] * b[i * 183 + k + 1187];
                    }
        }
        {
            double sum = 0.0000001;
            for (int i = 1370; i < 2154; i++)
                sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 2154, dyp = 1370; dxp < 2938; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int sp = 784, dxp = 2938, dsp = 784, dyp = 2154;
            for (int yp = 784; yp < 1568; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += 0.01 * (sq * (sq - 1));
                b[dxp++] += b[dyp++] * exp(w[sp++]);
            }
        }
        {
            int xp = 0, mp = 0, dmp = 0, dyp = 2938;
            for (int dxp = 0; dxp < 784; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - x[xp++]);
                dx[dxp] += b[dyp++];
            }
        }
    }
}
