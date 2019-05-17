package test.best;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Simple extends VectorDiffStruct {
    public Simple() {
        super(29, 1027, 3, 204, 204);
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
            for (int i = 979; i < 1024; i++)
                w[i] = 0.2581988897471611 * random.nextGaussian();
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
            for (int k = 0; k < 3; k++)
                f[i * 3 + k + 198] += f[i * 15 + j + 183] * w[j * 3 + k + 979];
        }
        {
            int ap = 198, bp = 1024;
            for (int cp = 201; cp < 204; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 201, yp = 0;
            for (int i = 0; i < 3; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 201, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 3; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 3, dbp = 1024;
            for (int dcp = 0; dcp < 3; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 3; k++) {
                b[i * 15 + j + 6] += b[i * 3 + k + 3] * w[j * 3 + k + 979];
                dw[j * 3 + k + 979] += f[i * 15 + j + 183] * b[i * 3 + k + 3];
            }
        }
        {
            int xp = 168, mp = 964, dmp = 964, dyp = 6;
            for (int dxp = 21; dxp < 36; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 153, yp = 168, dxp = 36, dyp = 21;
            for (int i = 0; i < 15; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 51, dbp = 949;
            for (int dcp = 36; dcp < 51; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++) {
                b[i * 20 + j + 66] += b[i * 15 + k + 51] * w[j * 15 + k + 649];
                dw[j * 15 + k + 649] += f[i * 20 + j + 118] * b[i * 15 + k + 51];
            }
        }
        {
            int xp = 98, mp = 629, dmp = 629, dyp = 66;
            for (int dxp = 86; dxp < 106; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 78, yp = 98, dxp = 106, dyp = 86;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 126, dbp = 609;
            for (int dcp = 106; dcp < 126; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 29 + j + 146] += b[i * 20 + k + 126] * w[j * 20 + k + 29];
                dw[j * 20 + k + 29] += f[i * 29 + j + 29] * b[i * 20 + k + 126];
            }
        }
        {
            int sp = 0, dxp = 175, dsp = 0, dyp = 146;
            for (int yp = 29; yp < 58; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += 0.01 * (sq * (sq - 1));
                b[dxp++] += b[dyp++] * exp(w[sp++]);
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 175; i < 204; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 0, dyp = 175; dxp < 29; dxp++)
                dx[dxp] += b[dyp++] * inv;
        }
    }
}
