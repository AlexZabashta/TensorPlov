package test.mnist;

import static java.lang.Math.*;
import core.VectorDiffStruct;

import java.util.Locale;
import java.util.Random;

public class LSTM extends VectorDiffStruct {
    public LSTM() {
        super(56, 2030, 28, 336, 336);
    }

    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 1624; i < 2016; i++)
                w[i] = 0.1889822365046136 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 0; i < 392; i++)
                w[i] = 0.1889822365046136 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 406; i < 798; i++)
                w[i] = 0.1889822365046136 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 812; i < 1204; i++)
                w[i] = 0.1889822365046136 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1218; i < 1610; i++)
                w[i] = 0.1889822365046136 * random.nextGaussian();
        }
    }

    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            int tp = 0;
            for (int fp = 0; fp < 14; fp++)
                f[tp++] += x[fp];
            for (int fp = 28; fp < 42; fp++)
                f[tp++] += x[fp];
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 28; j++)
                    for (int k = 0; k < 14; k++)
                        f[i * 14 + k + 28] += f[i * 28 + j + 0] * w[j * 14 + k + 1624];
        }
        {
            int ap = 28, bp = 2016;
            for (int cp = 42; cp < 56; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 42, yp = 56;
            for (int i = 0; i < 14; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 28; j++)
                    for (int k = 0; k < 14; k++)
                        f[i * 14 + k + 70] += f[i * 28 + j + 0] * w[j * 14 + k + 0];
        }
        {
            int ap = 70, bp = 392;
            for (int cp = 84; cp < 98; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 84, yp = 98;
            for (int i = 0; i < 14; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            int ap = 98, bp = 14;
            for (int cp = 112; cp < 126; cp++)
                f[cp] += f[ap++] * x[bp++];
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 28; j++)
                    for (int k = 0; k < 14; k++)
                        f[i * 14 + k + 126] += f[i * 28 + j + 0] * w[j * 14 + k + 406];
        }
        {
            int ap = 126, bp = 798;
            for (int cp = 140; cp < 154; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 140, yp = 154;
            for (int i = 0; i < 14; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            int ap = 154, bp = 42;
            for (int cp = 168; cp < 182; cp++)
                f[cp] += f[ap++] * x[bp++];
        }
        {
            int ap = 112, bp = 168;
            for (int cp = 182; cp < 196; cp++)
                f[cp] += f[ap++] + f[bp++];
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 28; j++)
                    for (int k = 0; k < 14; k++)
                        f[i * 14 + k + 196] += f[i * 28 + j + 0] * w[j * 14 + k + 812];
        }
        {
            int ap = 196, bp = 1204;
            for (int cp = 210; cp < 224; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 210, yp = 224;
            for (int i = 0; i < 14; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 28; j++)
                    for (int k = 0; k < 14; k++)
                        f[i * 14 + k + 238] += f[i * 28 + j + 0] * w[j * 14 + k + 1218];
        }
        {
            int ap = 238, bp = 1610;
            for (int cp = 252; cp < 266; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 252, yp = 266;
            for (int i = 0; i < 14; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int ap = 224, bp = 266;
            for (int cp = 280; cp < 294; cp++)
                f[cp] += f[ap++] * f[bp++];
        }
        {
            int ap = 182, bp = 280;
            for (int cp = 294; cp < 308; cp++)
                f[cp] += f[ap++] + f[bp++];
        }
        {
            int xp = 294, yp = 308;
            for (int i = 0; i < 14; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int ap = 56, bp = 308;
            for (int cp = 322; cp < 336; cp++)
                f[cp] += f[ap++] * f[bp++];
        }
        {
            int tp = 0;
            for (int fp = 322; fp < 336; fp++)
                y[tp++] += f[fp];
            for (int fp = 294; fp < 308; fp++)
                y[tp++] += f[fp];
        }
    }

    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int dtp = 0;
            for (int dfp = 0; dfp < 14; dfp++)
                b[dfp] += dy[dtp++];
            for (int dfp = 28; dfp < 42; dfp++)
                b[dfp] += dy[dtp++];
        }
        {
            int ap = 56, bp = 308;
            int dap = 266, dbp = 14;
            for (int dcp = 0; dcp < 14; dcp++) {
                b[dap++] += b[dcp] * f[bp++];
                b[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 294, yp = 308, dxp = 28, dyp = 14;
            for (int i = 0; i < 14; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 140, dbp = 42;
            for (int dcp = 28; dcp < 42; dcp++) {
                b[dap++] += b[dcp];
                b[dbp++] += b[dcp];
            }
        }
        {
            int ap = 224, bp = 266;
            int dap = 98, dbp = 56;
            for (int dcp = 42; dcp < 56; dcp++) {
                b[dap++] += b[dcp] * f[bp++];
                b[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 252, yp = 266, dxp = 70, dyp = 56;
            for (int i = 0; i < 14; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 84, dbp = 1610;
            for (int dcp = 70; dcp < 84; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 28; j++)
                    for (int k = 0; k < 14; k++) {
                        b[i * 28 + j + 308] += b[i * 14 + k + 84] * w[j * 14 + k + 1218];
                        dw[j * 14 + k + 1218] += f[i * 28 + j + 0] * b[i * 14 + k + 84];
                    }
        }
        {
            int xp = 210, yp = 224, dxp = 112, dyp = 98;
            for (int i = 0; i < 14; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 126, dbp = 1204;
            for (int dcp = 112; dcp < 126; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 28; j++)
                    for (int k = 0; k < 14; k++) {
                        b[i * 28 + j + 308] += b[i * 14 + k + 126] * w[j * 14 + k + 812];
                        dw[j * 14 + k + 812] += f[i * 28 + j + 0] * b[i * 14 + k + 126];
                    }
        }
        {
            int dap = 210, dbp = 154;
            for (int dcp = 140; dcp < 154; dcp++) {
                b[dap++] += b[dcp];
                b[dbp++] += b[dcp];
            }
        }
        {
            int ap = 154, bp = 42;
            int dap = 168, dbp = 42;
            for (int dcp = 154; dcp < 168; dcp++) {
                b[dap++] += b[dcp] * x[bp++];
                dx[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 140, yp = 154, dxp = 182, dyp = 168;
            for (int i = 0; i < 14; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 196, dbp = 798;
            for (int dcp = 182; dcp < 196; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 28; j++)
                    for (int k = 0; k < 14; k++) {
                        b[i * 28 + j + 308] += b[i * 14 + k + 196] * w[j * 14 + k + 406];
                        dw[j * 14 + k + 406] += f[i * 28 + j + 0] * b[i * 14 + k + 196];
                    }
        }
        {
            int ap = 98, bp = 14;
            int dap = 224, dbp = 14;
            for (int dcp = 210; dcp < 224; dcp++) {
                b[dap++] += b[dcp] * x[bp++];
                dx[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 84, yp = 98, dxp = 238, dyp = 224;
            for (int i = 0; i < 14; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 252, dbp = 392;
            for (int dcp = 238; dcp < 252; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 28; j++)
                    for (int k = 0; k < 14; k++) {
                        b[i * 28 + j + 308] += b[i * 14 + k + 252] * w[j * 14 + k + 0];
                        dw[j * 14 + k + 0] += f[i * 28 + j + 0] * b[i * 14 + k + 252];
                    }
        }
        {
            int xp = 42, yp = 56, dxp = 280, dyp = 266;
            for (int i = 0; i < 14; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 294, dbp = 2016;
            for (int dcp = 280; dcp < 294; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 28; j++)
                    for (int k = 0; k < 14; k++) {
                        b[i * 28 + j + 308] += b[i * 14 + k + 294] * w[j * 14 + k + 1624];
                        dw[j * 14 + k + 1624] += f[i * 28 + j + 0] * b[i * 14 + k + 294];
                    }
        }
        {
            int dtp = 308;
            for (int dfp = 0; dfp < 14; dfp++)
                dx[dfp] += b[dtp++];
            for (int dfp = 28; dfp < 42; dfp++)
                dx[dfp] += b[dtp++];
        }

    }
}
