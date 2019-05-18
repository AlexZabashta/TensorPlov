package test.meta;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class HVFold extends VectorDiffStruct {
    public HVFold() {
        super(128, 18592, 64, 416, 416);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 12288; i++)
                w[i] = 0.08838834764831843 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 12384; i < 18528; i++)
                w[i] = 0.10206207261596577 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 128; j++)
            for (int k = 0; k < 96; k++)
                f[i * 96 + k + 0] += x[i * 128 + j + 0] * w[j * 96 + k + 0];
        }
        {
            int ap = 0, bp = 12288;
            for (int cp = 96; cp < 192; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 96, yp = 192;
            for (int i = 0; i < 96; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 96; j++)
            for (int k = 0; k < 64; k++)
                f[i * 64 + k + 288] += f[i * 96 + j + 192] * w[j * 64 + k + 12384];
        }
        {
            int ap = 288, bp = 18528;
            for (int cp = 352; cp < 416; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 352, yp = 0;
            for (int i = 0; i < 64; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 352, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 64; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 64, dbp = 18528;
            for (int dcp = 0; dcp < 64; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 96; j++)
            for (int k = 0; k < 64; k++) {
                b[i * 96 + j + 128] += b[i * 64 + k + 64] * w[j * 64 + k + 12384];
                dw[j * 64 + k + 12384] += f[i * 96 + j + 192] * b[i * 64 + k + 64];
            }
        }
        {
            int xp = 96, yp = 192, dxp = 224, dyp = 128;
            for (int i = 0; i < 96; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 320, dbp = 12288;
            for (int dcp = 224; dcp < 320; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 128; j++)
            for (int k = 0; k < 96; k++) {
                dx[i * 128 + j + 0] += b[i * 96 + k + 320] * w[j * 96 + k + 0];
                dw[j * 96 + k + 0] += x[i * 128 + j + 0] * b[i * 96 + k + 320];
            }
        }
    }
}
