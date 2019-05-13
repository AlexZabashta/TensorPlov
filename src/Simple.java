import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Simple extends VectorDiffStruct {
    public Simple() {
        super(29, 1160, 1, 285, 285);
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
            int xp = 0, sp = 0;
            for (int yp = 0; yp < 29; yp++)
                f[yp] += x[xp++] * exp(w[sp++]);
        }
        {
            for (int xp = 0, yp = 29; yp < 58; yp++)
                f[yp] += f[xp++];
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
            for (int xp = 118, yp = 138; yp < 158; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++)
                f[i * 15 + k + 158] += f[i * 20 + j + 138] * w[j * 15 + k + 649];
        }
        {
            int ap = 158, bp = 949;
            for (int cp = 173; cp < 188; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 173, yp = 188;
            for (int i = 0; i < 15; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 188, mp = 964;
            for (int yp = 203; yp < 218; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int xp = 203, yp = 218; yp < 233; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 10; k++)
                f[i * 10 + k + 233] += f[i * 15 + j + 218] * w[j * 10 + k + 979];
        }
        {
            int ap = 233, bp = 1129;
            for (int cp = 243; cp < 253; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 243, yp = 253;
            for (int i = 0; i < 10; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            int xp = 253, mp = 1139;
            for (int yp = 263; yp < 273; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int xp = 263, yp = 273; yp < 283; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 1; k++)
                f[i * 1 + k + 283] += f[i * 10 + j + 273] * w[j * 1 + k + 1149];
        }
        {
            int ap = 283, bp = 1159;
            for (int cp = 284; cp < 285; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 284, yp = 0;
            for (int i = 0; i < 1; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 284, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 1; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 1, dbp = 1159;
            for (int dcp = 0; dcp < 1; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 1; k++) {
                b[i * 10 + j + 2] += b[i * 1 + k + 1] * w[j * 1 + k + 1149];
                dw[j * 1 + k + 1149] += f[i * 10 + j + 273] * b[i * 1 + k + 1];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 2; i < 12; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 12, dyp = 2; dxp < 22; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 253, mp = 1139, dmp = 1139, dyp = 12;
            for (int dxp = 22; dxp < 32; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 243, yp = 253, dxp = 32, dyp = 22;
            for (int i = 0; i < 10; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 42, dbp = 1129;
            for (int dcp = 32; dcp < 42; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 10; k++) {
                b[i * 15 + j + 52] += b[i * 10 + k + 42] * w[j * 10 + k + 979];
                dw[j * 10 + k + 979] += f[i * 15 + j + 218] * b[i * 10 + k + 42];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 52; i < 67; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 67, dyp = 52; dxp < 82; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 188, mp = 964, dmp = 964, dyp = 67;
            for (int dxp = 82; dxp < 97; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 173, yp = 188, dxp = 97, dyp = 82;
            for (int i = 0; i < 15; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 112, dbp = 949;
            for (int dcp = 97; dcp < 112; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++) {
                b[i * 20 + j + 127] += b[i * 15 + k + 112] * w[j * 15 + k + 649];
                dw[j * 15 + k + 649] += f[i * 20 + j + 138] * b[i * 15 + k + 112];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 127; i < 147; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 147, dyp = 127; dxp < 167; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 98, mp = 629, dmp = 629, dyp = 147;
            for (int dxp = 167; dxp < 187; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 78, yp = 98, dxp = 187, dyp = 167;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 207, dbp = 609;
            for (int dcp = 187; dcp < 207; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 29 + j + 227] += b[i * 20 + k + 207] * w[j * 20 + k + 29];
                dw[j * 20 + k + 29] += f[i * 29 + j + 29] * b[i * 20 + k + 207];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 227; i < 256; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 256, dyp = 227; dxp < 285; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int sp = 0, dxp = 0, dsp = 0, dyp = 256;
            for (int yp = 0; yp < 29; yp++) {
                double sq = f[yp] * f[yp];
                dw[dsp++] += 0.01 * (sq * (sq - 1));
                dx[dxp++] += b[dyp++] * exp(w[sp++]);
            }
        }
    }
}
