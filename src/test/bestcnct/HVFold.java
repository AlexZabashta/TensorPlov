package test.bestcnct;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class HVFold extends VectorDiffStruct {
    public HVFold() {
        super(70, 5865, 35, 235, 235);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 3850; i++)
                w[i] = 0.11952286093343936 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 3905; i < 5830; i++)
                w[i] = 0.13483997249264842 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 70; j++)
            for (int k = 0; k < 55; k++)
                f[i * 55 + k + 0] += x[i * 70 + j + 0] * w[j * 55 + k + 0];
        }
        {
            int ap = 0, bp = 3850;
            for (int cp = 55; cp < 110; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 55, yp = 110;
            for (int i = 0; i < 55; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 55; j++)
            for (int k = 0; k < 35; k++)
                f[i * 35 + k + 165] += f[i * 55 + j + 110] * w[j * 35 + k + 3905];
        }
        {
            int ap = 165, bp = 5830;
            for (int cp = 200; cp < 235; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 200, yp = 0;
            for (int i = 0; i < 35; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 200, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 35; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 35, dbp = 5830;
            for (int dcp = 0; dcp < 35; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 55; j++)
            for (int k = 0; k < 35; k++) {
                b[i * 55 + j + 70] += b[i * 35 + k + 35] * w[j * 35 + k + 3905];
                dw[j * 35 + k + 3905] += f[i * 55 + j + 110] * b[i * 35 + k + 35];
            }
        }
        {
            int xp = 55, yp = 110, dxp = 125, dyp = 70;
            for (int i = 0; i < 55; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 180, dbp = 3850;
            for (int dcp = 125; dcp < 180; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 70; j++)
            for (int k = 0; k < 55; k++) {
                dx[i * 70 + j + 0] += b[i * 55 + k + 180] * w[j * 55 + k + 0];
                dw[j * 55 + k + 0] += x[i * 70 + j + 0] * b[i * 55 + k + 180];
            }
        }
    }
}
