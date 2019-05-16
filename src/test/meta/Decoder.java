package test.meta;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Decoder extends VectorDiffStruct {
    public Decoder() {
        super(29, 1086, 1, 211, 211);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 580; i++)
                w[i] = 0.18569533817705186 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 600; i < 900; i++)
                w[i] = 0.22360679774997896 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 915; i < 1065; i++)
                w[i] = 0.2581988897471611 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1075; i < 1085; i++)
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
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++)
                f[i * 20 + k + 29] += f[i * 29 + j + 0] * w[j * 20 + k + 0];
        }
        {
            int ap = 29, bp = 580;
            for (int cp = 49; cp < 69; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 49, yp = 69;
            for (int i = 0; i < 20; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int xp = 69, yp = 89; yp < 109; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++)
                f[i * 15 + k + 109] += f[i * 20 + j + 89] * w[j * 15 + k + 600];
        }
        {
            int ap = 109, bp = 900;
            for (int cp = 124; cp < 139; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 124, yp = 139;
            for (int i = 0; i < 15; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int xp = 139, yp = 154; yp < 169; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 10; k++)
                f[i * 10 + k + 169] += f[i * 15 + j + 154] * w[j * 10 + k + 915];
        }
        {
            int ap = 169, bp = 1065;
            for (int cp = 179; cp < 189; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 179, yp = 189;
            for (int i = 0; i < 10; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int xp = 189, yp = 199; yp < 209; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 1; k++)
                f[i * 1 + k + 209] += f[i * 10 + j + 199] * w[j * 1 + k + 1075];
        }
        {
            int ap = 209, bp = 1085;
            for (int cp = 210; cp < 211; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 210, yp = 0;
            for (int i = 0; i < 1; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 210, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 1; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 1, dbp = 1085;
            for (int dcp = 0; dcp < 1; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 10; j++)
            for (int k = 0; k < 1; k++) {
                b[i * 10 + j + 2] += b[i * 1 + k + 1] * w[j * 1 + k + 1075];
                dw[j * 1 + k + 1075] += f[i * 10 + j + 199] * b[i * 1 + k + 1];
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
            int xp = 179, yp = 189, dxp = 22, dyp = 12;
            for (int i = 0; i < 10; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 32, dbp = 1065;
            for (int dcp = 22; dcp < 32; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 15; j++)
            for (int k = 0; k < 10; k++) {
                b[i * 15 + j + 42] += b[i * 10 + k + 32] * w[j * 10 + k + 915];
                dw[j * 10 + k + 915] += f[i * 15 + j + 154] * b[i * 10 + k + 32];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 42; i < 57; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 57, dyp = 42; dxp < 72; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 124, yp = 139, dxp = 72, dyp = 57;
            for (int i = 0; i < 15; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 87, dbp = 900;
            for (int dcp = 72; dcp < 87; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 20; j++)
            for (int k = 0; k < 15; k++) {
                b[i * 20 + j + 102] += b[i * 15 + k + 87] * w[j * 15 + k + 600];
                dw[j * 15 + k + 600] += f[i * 20 + j + 89] * b[i * 15 + k + 87];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 102; i < 122; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 122, dyp = 102; dxp < 142; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 49, yp = 69, dxp = 142, dyp = 122;
            for (int i = 0; i < 20; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 162, dbp = 580;
            for (int dcp = 142; dcp < 162; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 29; j++)
            for (int k = 0; k < 20; k++) {
                b[i * 29 + j + 182] += b[i * 20 + k + 162] * w[j * 20 + k + 0];
                dw[j * 20 + k + 0] += f[i * 29 + j + 0] * b[i * 20 + k + 162];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 182; i < 211; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 0, dyp = 182; dxp < 29; dxp++)
                dx[dxp] += b[dyp++] * inv;
        }
    }
}
