package test.gan;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class DisDecoder extends VectorDiffStruct {
    public DisDecoder() {
        super(55, 1701, 4, 111, 111);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 1375; i++)
                w[i] = 0.13483997249264842 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1400; i < 1650; i++)
                w[i] = 0.2 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1660; i < 1690; i++)
                w[i] = 0.31622776601683794 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1690; i < 1700; i++)
                w[i] = 0.31622776601683794 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 55; j++)
            for (int k = 0; k < 25; k++)
                f[i * 25 + k + 0] += x[i * 55 + j + 0] * w[j * 25 + k + 0];
        }
        {
            int ap = 0, bp = 1375;
            for (int cp = 25; cp < 50; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 25, yp = 50;
            for (int i = 0; i < 25; i++, xp++) {
                f[yp++] += max(0.01 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 25; j++)
            for (int k = 0; k < 10; k++)
                f[i * 10 + k + 75] += f[i * 25 + j + 50] * w[j * 10 + k + 1400];
        }
        {
            int ap = 75, bp = 1650;
            for (int cp = 85; cp < 95; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 85, yp = 95;
            for (int i = 0; i < 10; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 3; k++)
                f[i * 3 + k + 105] += f[i * 10 + j + 95] * w[j * 3 + k + 1660];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 1; k++)
                f[i * 1 + k + 108] += f[i * 10 + j + 95] * w[j * 1 + k + 1690];
        }
        {
            int ap = 108, bp = 1700;
            for (int cp = 109; cp < 110; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 109, yp = 110;
            for (int i = 0; i < 1; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int tp = 0;
            for (int fp = 105; fp < 108; fp++)
                y[tp++] += f[fp];
            for (int fp = 110; fp < 111; fp++)
                y[tp++] += f[fp];
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int dtp = 0;
            for (int dfp = 3; dfp < 6; dfp++)
                b[dfp] += dy[dtp++];
            for (int dfp = 0; dfp < 1; dfp++)
                b[dfp] += dy[dtp++];
        }
        {
            int xp = 109, yp = 110, dxp = 1, dyp = 0;
            for (int i = 0; i < 1; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 2, dbp = 1700;
            for (int dcp = 1; dcp < 2; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 1; k++) {
                b[i * 10 + j + 6] += b[i * 1 + k + 2] * w[j * 1 + k + 1690];
                dw[j * 1 + k + 1690] += f[i * 10 + j + 95] * b[i * 1 + k + 2];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 3; k++) {
                b[i * 10 + j + 6] += b[i * 3 + k + 3] * w[j * 3 + k + 1660];
                dw[j * 3 + k + 1660] += f[i * 10 + j + 95] * b[i * 3 + k + 3];
            }
        }
        {
            int xp = 85, yp = 95, dxp = 16, dyp = 6;
            for (int i = 0; i < 10; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 26, dbp = 1650;
            for (int dcp = 16; dcp < 26; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 25; j++)
            for (int k = 0; k < 10; k++) {
                b[i * 25 + j + 36] += b[i * 10 + k + 26] * w[j * 10 + k + 1400];
                dw[j * 10 + k + 1400] += f[i * 25 + j + 50] * b[i * 10 + k + 26];
            }
        }
        {
            int xp = 25, yp = 50, dxp = 61, dyp = 36;
            for (int i = 0; i < 25; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.01 : 1);
            }
        }
        {
            int dap = 86, dbp = 1375;
            for (int dcp = 61; dcp < 86; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 55; j++)
            for (int k = 0; k < 25; k++) {
                dx[i * 55 + j + 0] += b[i * 25 + k + 86] * w[j * 25 + k + 0];
                dw[j * 25 + k + 0] += x[i * 55 + j + 0] * b[i * 25 + k + 86];
            }
        }
    }
}
