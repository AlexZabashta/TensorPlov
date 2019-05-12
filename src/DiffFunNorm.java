import static java.lang.Math.*;

import core.VectorDiffStruct;

public class DiffFunNorm extends VectorDiffStruct {
    public DiffFunNorm() {
        super(784, 153831, 10, 1704, 1704);
    }

    @Override
    public void init(double[] w) {
    }

    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            int xp = 0, mp = 0, sp = 784;
            for (int yp = 0; yp < 784; yp++)
                f[yp] += (x[xp++] - w[mp++]) * exp(w[sp++]);
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 784; j++)
                    for (int k = 0; k < 183; k++)
                        f[i * 183 + k + 784] += f[i * 784 + j + 0] * w[j * 183 + k + 1568];
        }
        {
            int ap = 784, bp = 145040;
            for (int cp = 967; cp < 1150; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 967, yp = 1150;
            for (int i = 0; i < 183; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int xp = 1150, mp = 145223, sp = 145406;
            for (int yp = 1333; yp < 1516; yp++)
                f[yp] += (f[xp++] - w[mp++]) * exp(w[sp++]);
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 183; j++)
                    for (int k = 0; k < 42; k++)
                        f[i * 42 + k + 1516] += f[i * 183 + j + 1333] * w[j * 42 + k + 145589];
        }
        {
            int ap = 1516, bp = 153275;
            for (int cp = 1558; cp < 1600; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 1558, yp = 1600;
            for (int i = 0; i < 42; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int xp = 1600, mp = 153317, sp = 153359;
            for (int yp = 1642; yp < 1684; yp++)
                f[yp] += (f[xp++] - w[mp++]) * exp(w[sp++]);
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 42; j++)
                    for (int k = 0; k < 10; k++)
                        f[i * 10 + k + 1684] += f[i * 42 + j + 1642] * w[j * 10 + k + 153401];
        }
        {
            int ap = 1684, bp = 153821;
            for (int cp = 1694; cp < 1704; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 1694, yp = 0;
            for (int i = 0; i < 10; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }

    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 1694, yp = 0, dxp = 0, dyp = 0;
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
                        dw[j * 10 + k + 153401] += f[i * 42 + j + 1642] * b[i * 10 + k + 10];
                    }
        }
        {
            double sum = 0.0000001;
            for (int i = 20; i < 62; i++)
                sum += b[i] * b[i];
            double inv = 1 / sum;
            int xp = 1600, mp = 153317, sp = 153359;
            int dxp = 62, dmp = 153317, dsp = 153359, dyp = 20;
            for (int yp = 1642; yp < 1684; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += sq * (sq - 1);
                dw[dmp++] += f[xp++] - w[mp++];
                b[dxp++] += b[dyp++] * exp(w[sp++]) * inv;
            }
        }
        {
            int xp = 1558, yp = 1600, dxp = 104, dyp = 62;
            for (int i = 0; i < 42; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 146, dbp = 153275;
            for (int dcp = 104; dcp < 146; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 183; j++)
                    for (int k = 0; k < 42; k++) {
                        b[i * 183 + j + 188] += b[i * 42 + k + 146] * w[j * 42 + k + 145589];
                        dw[j * 42 + k + 145589] += f[i * 183 + j + 1333] * b[i * 42 + k + 146];
                    }
        }
        {
            double sum = 0.0000001;
            for (int i = 188; i < 371; i++)
                sum += b[i] * b[i];
            double inv = 1 / sum;
            int xp = 1150, mp = 145223, sp = 145406;
            int dxp = 371, dmp = 145223, dsp = 145406, dyp = 188;
            for (int yp = 1333; yp < 1516; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += sq * (sq - 1);
                dw[dmp++] += f[xp++] - w[mp++];
                b[dxp++] += b[dyp++] * exp(w[sp++]) * inv;
            }
        }
        {
            int xp = 967, yp = 1150, dxp = 554, dyp = 371;
            for (int i = 0; i < 183; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 737, dbp = 145040;
            for (int dcp = 554; dcp < 737; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 784; j++)
                    for (int k = 0; k < 183; k++) {
                        b[i * 784 + j + 920] += b[i * 183 + k + 737] * w[j * 183 + k + 1568];
                        dw[j * 183 + k + 1568] += f[i * 784 + j + 0] * b[i * 183 + k + 737];
                    }
        }
        {
            double sum = 0.0000001;
            for (int i = 920; i < 1704; i++)
                sum += b[i] * b[i];
            double inv = 1 / sum;
            int xp = 0, mp = 0, sp = 784;
            int dxp = 0, dmp = 0, dsp = 784, dyp = 920;
            for (int yp = 0; yp < 784; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += sq * (sq - 1);
                dw[dmp++] += x[xp++] - w[mp++];
                dx[dxp++] += b[dyp++] * exp(w[sp++]) * inv;
            }
        }
    }
}
