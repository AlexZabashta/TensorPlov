package test.best;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Conv extends VectorDiffStruct {
    public Conv() {
        super(96, 5880, 48, 576, 576);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 4704; i < 5856; i++)
                w[i] = 0.14433756729740646 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 0; i < 1152; i++)
                w[i] = 0.14433756729740646 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1176; i < 2328; i++)
                w[i] = 0.14433756729740646 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2352; i < 3504; i++)
                w[i] = 0.14433756729740646 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 3528; i < 4680; i++)
                w[i] = 0.14433756729740646 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            int tp = 0;
            for (int fp = 0; fp < 24; fp++)
                f[tp++] += x[fp];
            for (int fp = 48; fp < 72; fp++)
                f[tp++] += x[fp];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 48; j++)
            for (int k = 0; k < 24; k++)
                f[i * 24 + k + 48] += f[i * 48 + j + 0] * w[j * 24 + k + 4704];
        }
        {
            int ap = 48, bp = 5856;
            for (int cp = 72; cp < 96; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 72, yp = 96;
            for (int i = 0; i < 24; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 48; j++)
            for (int k = 0; k < 24; k++)
                f[i * 24 + k + 120] += f[i * 48 + j + 0] * w[j * 24 + k + 0];
        }
        {
            int ap = 120, bp = 1152;
            for (int cp = 144; cp < 168; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 144, yp = 168;
            for (int i = 0; i < 24; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            int ap = 168, bp = 24;
            for (int cp = 192; cp < 216; cp++)
                f[cp] += f[ap++] * x[bp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 48; j++)
            for (int k = 0; k < 24; k++)
                f[i * 24 + k + 216] += f[i * 48 + j + 0] * w[j * 24 + k + 1176];
        }
        {
            int ap = 216, bp = 2328;
            for (int cp = 240; cp < 264; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 240, yp = 264;
            for (int i = 0; i < 24; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            int ap = 264, bp = 72;
            for (int cp = 288; cp < 312; cp++)
                f[cp] += f[ap++] * x[bp++];
        }
        {
            int ap = 192, bp = 288;
            for (int cp = 312; cp < 336; cp++)
                f[cp] += f[ap++] + f[bp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 48; j++)
            for (int k = 0; k < 24; k++)
                f[i * 24 + k + 336] += f[i * 48 + j + 0] * w[j * 24 + k + 2352];
        }
        {
            int ap = 336, bp = 3504;
            for (int cp = 360; cp < 384; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 360, yp = 384;
            for (int i = 0; i < 24; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 48; j++)
            for (int k = 0; k < 24; k++)
                f[i * 24 + k + 408] += f[i * 48 + j + 0] * w[j * 24 + k + 3528];
        }
        {
            int ap = 408, bp = 4680;
            for (int cp = 432; cp < 456; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 432, yp = 456;
            for (int i = 0; i < 24; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int ap = 384, bp = 456;
            for (int cp = 480; cp < 504; cp++)
                f[cp] += f[ap++] * f[bp++];
        }
        {
            int ap = 312, bp = 480;
            for (int cp = 504; cp < 528; cp++)
                f[cp] += f[ap++] + f[bp++];
        }
        {
            int xp = 504, yp = 528;
            for (int i = 0; i < 24; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int ap = 96, bp = 528;
            for (int cp = 552; cp < 576; cp++)
                f[cp] += f[ap++] * f[bp++];
        }
        {
            int tp = 0;
            for (int fp = 552; fp < 576; fp++)
                y[tp++] += f[fp];
            for (int fp = 504; fp < 528; fp++)
                y[tp++] += f[fp];
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int dtp = 0;
            for (int dfp = 0; dfp < 24; dfp++)
                b[dfp] += dy[dtp++];
            for (int dfp = 48; dfp < 72; dfp++)
                b[dfp] += dy[dtp++];
        }
        {
            int ap = 96, bp = 528;
            int dap = 456, dbp = 24;
            for (int dcp = 0; dcp < 24; dcp++) {
                b[dap++] += b[dcp] * f[bp++];
                b[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 504, yp = 528, dxp = 48, dyp = 24;
            for (int i = 0; i < 24; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 240, dbp = 72;
            for (int dcp = 48; dcp < 72; dcp++) {
                b[dap++] += b[dcp];
                b[dbp++] += b[dcp];
            }
        }
        {
            int ap = 384, bp = 456;
            int dap = 168, dbp = 96;
            for (int dcp = 72; dcp < 96; dcp++) {
                b[dap++] += b[dcp] * f[bp++];
                b[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 432, yp = 456, dxp = 120, dyp = 96;
            for (int i = 0; i < 24; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 144, dbp = 4680;
            for (int dcp = 120; dcp < 144; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 48; j++)
            for (int k = 0; k < 24; k++) {
                b[i * 48 + j + 528] += b[i * 24 + k + 144] * w[j * 24 + k + 3528];
                dw[j * 24 + k + 3528] += f[i * 48 + j + 0] * b[i * 24 + k + 144];
            }
        }
        {
            int xp = 360, yp = 384, dxp = 192, dyp = 168;
            for (int i = 0; i < 24; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 216, dbp = 3504;
            for (int dcp = 192; dcp < 216; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 48; j++)
            for (int k = 0; k < 24; k++) {
                b[i * 48 + j + 528] += b[i * 24 + k + 216] * w[j * 24 + k + 2352];
                dw[j * 24 + k + 2352] += f[i * 48 + j + 0] * b[i * 24 + k + 216];
            }
        }
        {
            int dap = 360, dbp = 264;
            for (int dcp = 240; dcp < 264; dcp++) {
                b[dap++] += b[dcp];
                b[dbp++] += b[dcp];
            }
        }
        {
            int ap = 264, bp = 72;
            int dap = 288, dbp = 72;
            for (int dcp = 264; dcp < 288; dcp++) {
                b[dap++] += b[dcp] * x[bp++];
                dx[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 240, yp = 264, dxp = 312, dyp = 288;
            for (int i = 0; i < 24; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 336, dbp = 2328;
            for (int dcp = 312; dcp < 336; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 48; j++)
            for (int k = 0; k < 24; k++) {
                b[i * 48 + j + 528] += b[i * 24 + k + 336] * w[j * 24 + k + 1176];
                dw[j * 24 + k + 1176] += f[i * 48 + j + 0] * b[i * 24 + k + 336];
            }
        }
        {
            int ap = 168, bp = 24;
            int dap = 384, dbp = 24;
            for (int dcp = 360; dcp < 384; dcp++) {
                b[dap++] += b[dcp] * x[bp++];
                dx[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 144, yp = 168, dxp = 408, dyp = 384;
            for (int i = 0; i < 24; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 432, dbp = 1152;
            for (int dcp = 408; dcp < 432; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 48; j++)
            for (int k = 0; k < 24; k++) {
                b[i * 48 + j + 528] += b[i * 24 + k + 432] * w[j * 24 + k + 0];
                dw[j * 24 + k + 0] += f[i * 48 + j + 0] * b[i * 24 + k + 432];
            }
        }
        {
            int xp = 72, yp = 96, dxp = 480, dyp = 456;
            for (int i = 0; i < 24; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 504, dbp = 5856;
            for (int dcp = 480; dcp < 504; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 48; j++)
            for (int k = 0; k < 24; k++) {
                b[i * 48 + j + 528] += b[i * 24 + k + 504] * w[j * 24 + k + 4704];
                dw[j * 24 + k + 4704] += f[i * 48 + j + 0] * b[i * 24 + k + 504];
            }
        }
        {
            int dtp = 528;
            for (int dfp = 0; dfp < 24; dfp++)
                dx[dfp] += b[dtp++];
            for (int dfp = 48; dfp < 72; dfp++)
                dx[dfp] += b[dtp++];
        }
    }
}
