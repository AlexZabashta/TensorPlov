package test.meta;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Simple extends VectorDiffStruct {
    public Simple() {
        super(29, 1160, 1, 239, 239);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 29; i < 609; i++)
                w[i] = 0.18569533817705186 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 649; i < 949; i++)
                w[i] = 0.22360679774997896 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 979; i < 1129; i++)
                w[i] = 0.2581988897471611 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1149; i < 1159; i++)
                w[i] = 0.31622776601683794 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int xp = 0, yp = 0; yp < 29; yp++)
                f[yp] += x[xp++];
        }
        {
            int xp = 0, sp = 0;
            for (int yp = 29; yp < 58; yp++)
                f[yp] += f[xp++] * exp(w[sp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 58] += f[i * 29 + j + 29] * w[j * 20 + k + 29];
        }
        {
            int ap = 58, bp = 609;
            for (int cp = 78; cp < 98; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 78, yp = 98;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 98, mp = 629;
            for (int yp = 118; yp < 138; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++)
                f[i * 15 + k + 138] += f[i * 20 + j + 118] * w[j * 15 + k + 649];
        }
        {
            int ap = 138, bp = 949;
            for (int cp = 153; cp < 168; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 153, yp = 168;
            for (int i = 0; i < 15; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 168, mp = 964;
            for (int yp = 183; yp < 198; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 10; k++)
                f[i * 10 + k + 198] += f[i * 15 + j + 183] * w[j * 10 + k + 979];
        }
        {
            int ap = 198, bp = 1129;
            for (int cp = 208; cp < 218; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 208, yp = 218;
            for (int i = 0; i < 10; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int xp = 218, mp = 1139;
            for (int yp = 228; yp < 238; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 1; k++)
                f[i * 1 + k + 238] += f[i * 10 + j + 228] * w[j * 1 + k + 1149];
        }
        {
            int ap = 238, bp = 1159;
            for (int cp = 0; cp < 1; cp++)
                y[cp] += f[ap++] + w[bp++];
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int dap = 0, dbp = 1159;
            for (int dcp = 0; dcp < 1; dcp++) {
                b[dap++] += dy[dcp];
                dw[dbp++] += dy[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 1; k++) {
                b[i * 10 + j + 1] += b[i * 1 + k + 0] * w[j * 1 + k + 1149];
                dw[j * 1 + k + 1149] += f[i * 10 + j + 228] * b[i * 1 + k + 0];
            }
        }
        {
            int xp = 218, mp = 1139, dmp = 1139, dyp = 1;
            for (int dxp = 11; dxp < 21; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 208, yp = 218, dxp = 21, dyp = 11;
            for (int i = 0; i < 10; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 31, dbp = 1129;
            for (int dcp = 21; dcp < 31; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 10; k++) {
                b[i * 15 + j + 41] += b[i * 10 + k + 31] * w[j * 10 + k + 979];
                dw[j * 10 + k + 979] += f[i * 15 + j + 183] * b[i * 10 + k + 31];
            }
        }
        {
            int xp = 168, mp = 964, dmp = 964, dyp = 41;
            for (int dxp = 56; dxp < 71; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 153, yp = 168, dxp = 71, dyp = 56;
            for (int i = 0; i < 15; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 86, dbp = 949;
            for (int dcp = 71; dcp < 86; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++) {
                b[i * 20 + j + 101] += b[i * 15 + k + 86] * w[j * 15 + k + 649];
                dw[j * 15 + k + 649] += f[i * 20 + j + 118] * b[i * 15 + k + 86];
            }
        }
        {
            int xp = 98, mp = 629, dmp = 629, dyp = 101;
            for (int dxp = 121; dxp < 141; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 78, yp = 98, dxp = 141, dyp = 121;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 161, dbp = 609;
            for (int dcp = 141; dcp < 161; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 29 + j + 181] += b[i * 20 + k + 161] * w[j * 20 + k + 29];
                dw[j * 20 + k + 29] += f[i * 29 + j + 29] * b[i * 20 + k + 161];
            }
        }
        {
            int sp = 0, dxp = 210, dsp = 0, dyp = 181;
            for (int yp = 29; yp < 58; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += 0.01 * (sq * (sq - 1));
                b[dxp++] += b[dyp++] * exp(w[sp++]);
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 210; i < 239; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 0, dyp = 210; dxp < 29; dxp++)
                dx[dxp] += b[dyp++] * inv;
        }
    }
}
