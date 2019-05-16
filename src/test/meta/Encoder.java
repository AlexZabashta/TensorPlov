package test.meta;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Encoder extends VectorDiffStruct {
    public Encoder() {
        super(2, 859, 29, 180, 180);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 20; i++)
                w[i] = 0.7071067811865475 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 30; i < 230; i++)
                w[i] = 0.31622776601683794 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 250; i < 830; i++)
                w[i] = 0.22360679774997896 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int xp = 0, yp = 0; yp < 2; yp++)
                f[yp] += x[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 10; k++)
                f[i * 10 + k + 2] += f[i * 2 + j + 0] * w[j * 10 + k + 0];
        }
        {
            int ap = 2, bp = 20;
            for (int cp = 12; cp < 22; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 12, yp = 22;
            for (int i = 0; i < 10; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int xp = 22, yp = 32; yp < 42; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 42] += f[i * 10 + j + 32] * w[j * 20 + k + 30];
        }
        {
            int ap = 42, bp = 230;
            for (int cp = 62; cp < 82; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 62, yp = 82;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int xp = 82, yp = 102; yp < 122; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 29; k++)
                f[i * 29 + k + 122] += f[i * 20 + j + 102] * w[j * 29 + k + 250];
        }
        {
            int ap = 122, bp = 830;
            for (int cp = 151; cp < 180; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 151, yp = 0;
            for (int i = 0; i < 29; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 151, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 29; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 29, dbp = 830;
            for (int dcp = 0; dcp < 29; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 29; k++) {
                b[i * 20 + j + 58] += b[i * 29 + k + 29] * w[j * 29 + k + 250];
                dw[j * 29 + k + 250] += f[i * 20 + j + 102] * b[i * 29 + k + 29];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 58; i < 78; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 78, dyp = 58; dxp < 98; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 62, yp = 82, dxp = 98, dyp = 78;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 118, dbp = 230;
            for (int dcp = 98; dcp < 118; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 10 + j + 138] += b[i * 20 + k + 118] * w[j * 20 + k + 30];
                dw[j * 20 + k + 30] += f[i * 10 + j + 32] * b[i * 20 + k + 118];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 138; i < 148; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 148, dyp = 138; dxp < 158; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 12, yp = 22, dxp = 158, dyp = 148;
            for (int i = 0; i < 10; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 168, dbp = 20;
            for (int dcp = 158; dcp < 168; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 2; j++)
            for (int k = 0; k < 10; k++) {
                b[i * 2 + j + 178] += b[i * 10 + k + 168] * w[j * 10 + k + 0];
                dw[j * 10 + k + 0] += f[i * 2 + j + 0] * b[i * 10 + k + 168];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 178; i < 180; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 0, dyp = 178; dxp < 2; dxp++)
                dx[dxp] += b[dyp++] * inv;
        }
    }
}
