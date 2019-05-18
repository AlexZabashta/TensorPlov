package test.mnist.norm;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class HVFold extends VectorDiffStruct {
    public HVFold() {
        super(128, 16662, 64, 600, 600);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 11008; i++)
                w[i] = 0.08838834764831843 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 11094; i < 16598; i++)
                w[i] = 0.10783277320343841 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int xp = 0, yp = 0; yp < 128; yp++)
                f[yp] += x[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 128; j++)
            for (int k = 0; k < 86; k++)
                f[i * 86 + k + 128] += f[i * 128 + j + 0] * w[j * 86 + k + 0];
        }
        {
            int ap = 128, bp = 11008;
            for (int cp = 214; cp < 300; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 214, yp = 300;
            for (int i = 0; i < 86; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int xp = 300, yp = 386; yp < 472; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 86; j++)
            for (int k = 0; k < 64; k++)
                f[i * 64 + k + 472] += f[i * 86 + j + 386] * w[j * 64 + k + 11094];
        }
        {
            int ap = 472, bp = 16598;
            for (int cp = 536; cp < 600; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 536, yp = 0;
            for (int i = 0; i < 64; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 536, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 64; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 64, dbp = 16598;
            for (int dcp = 0; dcp < 64; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 86; j++)
            for (int k = 0; k < 64; k++) {
                b[i * 86 + j + 128] += b[i * 64 + k + 64] * w[j * 64 + k + 11094];
                dw[j * 64 + k + 11094] += f[i * 86 + j + 386] * b[i * 64 + k + 64];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 128; i < 214; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 214, dyp = 128; dxp < 300; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 214, yp = 300, dxp = 300, dyp = 214;
            for (int i = 0; i < 86; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 386, dbp = 11008;
            for (int dcp = 300; dcp < 386; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 128; j++)
            for (int k = 0; k < 86; k++) {
                b[i * 128 + j + 472] += b[i * 86 + k + 386] * w[j * 86 + k + 0];
                dw[j * 86 + k + 0] += f[i * 128 + j + 0] * b[i * 86 + k + 386];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 472; i < 600; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 0, dyp = 472; dxp < 128; dxp++)
                dx[dxp] += b[dyp++] * inv;
        }
    }
}
