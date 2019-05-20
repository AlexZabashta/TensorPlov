package test.best;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class LSTM extends VectorDiffStruct {
    public LSTM() {
        super(80, 4100, 40, 480, 480);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 3280; i < 4080; i++)
                w[i] = 0.15811388300841897 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 0; i < 800; i++)
                w[i] = 0.15811388300841897 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 820; i < 1620; i++)
                w[i] = 0.15811388300841897 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1640; i < 2440; i++)
                w[i] = 0.15811388300841897 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2460; i < 3260; i++)
                w[i] = 0.15811388300841897 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            int tp = 0;
            for (int fp = 0; fp < 20; fp++)
                f[tp++] += x[fp];
            for (int fp = 40; fp < 60; fp++)
                f[tp++] += x[fp];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 40] += f[i * 40 + j + 0] * w[j * 20 + k + 3280];
        }
        {
            int ap = 40, bp = 4080;
            for (int cp = 60; cp < 80; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 60, yp = 80;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 100] += f[i * 40 + j + 0] * w[j * 20 + k + 0];
        }
        {
            int ap = 100, bp = 800;
            for (int cp = 120; cp < 140; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 120, yp = 140;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            int ap = 140, bp = 20;
            for (int cp = 160; cp < 180; cp++)
                f[cp] += f[ap++] * x[bp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 180] += f[i * 40 + j + 0] * w[j * 20 + k + 820];
        }
        {
            int ap = 180, bp = 1620;
            for (int cp = 200; cp < 220; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 200, yp = 220;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            int ap = 220, bp = 60;
            for (int cp = 240; cp < 260; cp++)
                f[cp] += f[ap++] * x[bp++];
        }
        {
            int ap = 160, bp = 240;
            for (int cp = 260; cp < 280; cp++)
                f[cp] += f[ap++] + f[bp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 280] += f[i * 40 + j + 0] * w[j * 20 + k + 1640];
        }
        {
            int ap = 280, bp = 2440;
            for (int cp = 300; cp < 320; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 300, yp = 320;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 340] += f[i * 40 + j + 0] * w[j * 20 + k + 2460];
        }
        {
            int ap = 340, bp = 3260;
            for (int cp = 360; cp < 380; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 360, yp = 380;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int ap = 320, bp = 380;
            for (int cp = 400; cp < 420; cp++)
                f[cp] += f[ap++] * f[bp++];
        }
        {
            int ap = 260, bp = 400;
            for (int cp = 420; cp < 440; cp++)
                f[cp] += f[ap++] + f[bp++];
        }
        {
            int xp = 420, yp = 440;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int ap = 80, bp = 440;
            for (int cp = 460; cp < 480; cp++)
                f[cp] += f[ap++] * f[bp++];
        }
        {
            int tp = 0;
            for (int fp = 460; fp < 480; fp++)
                y[tp++] += f[fp];
            for (int fp = 420; fp < 440; fp++)
                y[tp++] += f[fp];
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int dtp = 0;
            for (int dfp = 460; dfp < 480; dfp++)
                f[dfp] += y[dtp++];
            for (int dfp = 420; dfp < 440; dfp++)
                f[dfp] += y[dtp++];
        }
        {
            int ap = 80, bp = 440;
            int dap = 380, dbp = 20;
            for (int dcp = 0; dcp < 20; dcp++) {
                b[dap++] += b[dcp] * f[bp++];
                b[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 420, yp = 440, dxp = 40, dyp = 20;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 200, dbp = 60;
            for (int dcp = 40; dcp < 60; dcp++) {
                b[dap++] += b[dcp];
                b[dbp++] += b[dcp];
            }
        }
        {
            int ap = 320, bp = 380;
            int dap = 140, dbp = 80;
            for (int dcp = 60; dcp < 80; dcp++) {
                b[dap++] += b[dcp] * f[bp++];
                b[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 360, yp = 380, dxp = 100, dyp = 80;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 120, dbp = 3260;
            for (int dcp = 100; dcp < 120; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 40 + j + 440] += b[i * 20 + k + 120] * w[j * 20 + k + 2460];
                dw[j * 20 + k + 2460] += f[i * 40 + j + 0] * b[i * 20 + k + 120];
            }
        }
        {
            int xp = 300, yp = 320, dxp = 160, dyp = 140;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 180, dbp = 2440;
            for (int dcp = 160; dcp < 180; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 40 + j + 440] += b[i * 20 + k + 180] * w[j * 20 + k + 1640];
                dw[j * 20 + k + 1640] += f[i * 40 + j + 0] * b[i * 20 + k + 180];
            }
        }
        {
            int dap = 300, dbp = 220;
            for (int dcp = 200; dcp < 220; dcp++) {
                b[dap++] += b[dcp];
                b[dbp++] += b[dcp];
            }
        }
        {
            int ap = 220, bp = 60;
            int dap = 240, dbp = 60;
            for (int dcp = 220; dcp < 240; dcp++) {
                b[dap++] += b[dcp] * x[bp++];
                dx[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 200, yp = 220, dxp = 260, dyp = 240;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 280, dbp = 1620;
            for (int dcp = 260; dcp < 280; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 40 + j + 440] += b[i * 20 + k + 280] * w[j * 20 + k + 820];
                dw[j * 20 + k + 820] += f[i * 40 + j + 0] * b[i * 20 + k + 280];
            }
        }
        {
            int ap = 140, bp = 20;
            int dap = 320, dbp = 20;
            for (int dcp = 300; dcp < 320; dcp++) {
                b[dap++] += b[dcp] * x[bp++];
                dx[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 120, yp = 140, dxp = 340, dyp = 320;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 360, dbp = 800;
            for (int dcp = 340; dcp < 360; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 40 + j + 440] += b[i * 20 + k + 360] * w[j * 20 + k + 0];
                dw[j * 20 + k + 0] += f[i * 40 + j + 0] * b[i * 20 + k + 360];
            }
        }
        {
            int xp = 60, yp = 80, dxp = 400, dyp = 380;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 420, dbp = 4080;
            for (int dcp = 400; dcp < 420; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 40; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 40 + j + 440] += b[i * 20 + k + 420] * w[j * 20 + k + 3280];
                dw[j * 20 + k + 3280] += f[i * 40 + j + 0] * b[i * 20 + k + 420];
            }
        }
        {
            int dtp = 0;
            for (int dfp = 0; dfp < 20; dfp++)
                x[dfp] += f[dtp++];
            for (int dfp = 40; dfp < 60; dfp++)
                x[dfp] += f[dtp++];
        }
    }
}
