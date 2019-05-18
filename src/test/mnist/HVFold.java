package test.mnist;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class HVFold extends VectorDiffStruct {
    public HVFold() {
        super(56, 3598, 28, 182, 182);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 2352; i++)
                w[i] = 0.1336306209562122 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2394; i < 3570; i++)
                w[i] = 0.1543033499620919 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 56; j++)
            for (int k = 0; k < 42; k++)
                f[i * 42 + k + 0] += x[i * 56 + j + 0] * w[j * 42 + k + 0];
        }
        {
            int ap = 0, bp = 2352;
            for (int cp = 42; cp < 84; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 42, yp = 84;
            for (int i = 0; i < 42; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 42; j++)
            for (int k = 0; k < 28; k++)
                f[i * 28 + k + 126] += f[i * 42 + j + 84] * w[j * 28 + k + 2394];
        }
        {
            int ap = 126, bp = 3570;
            for (int cp = 154; cp < 182; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 154, yp = 0;
            for (int i = 0; i < 28; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 154, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 28; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 28, dbp = 3570;
            for (int dcp = 0; dcp < 28; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 42; j++)
            for (int k = 0; k < 28; k++) {
                b[i * 42 + j + 56] += b[i * 28 + k + 28] * w[j * 28 + k + 2394];
                dw[j * 28 + k + 2394] += f[i * 42 + j + 84] * b[i * 28 + k + 28];
            }
        }
        {
            int xp = 42, yp = 84, dxp = 98, dyp = 56;
            for (int i = 0; i < 42; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 140, dbp = 2352;
            for (int dcp = 98; dcp < 140; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 56; j++)
            for (int k = 0; k < 42; k++) {
                dx[i * 56 + j + 0] += b[i * 42 + k + 140] * w[j * 42 + k + 0];
                dw[j * 42 + k + 0] += x[i * 56 + j + 0] * b[i * 42 + k + 140];
            }
        }
    }
}
