package test.meta;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class HVFold extends VectorDiffStruct {
    public HVFold() {
        super(58, 4165, 29, 304, 304);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 2726; i++)
                w[i] = 0.13130643285972254 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2773; i < 4136; i++)
                w[i] = 0.14586499149789456 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int xp = 0, yp = 0; yp < 58; yp++)
                f[yp] += x[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 58; j++)
            for (int k = 0; k < 47; k++)
                f[i * 47 + k + 58] += f[i * 58 + j + 0] * w[j * 47 + k + 0];
        }
        {
            int ap = 58, bp = 2726;
            for (int cp = 105; cp < 152; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 105, yp = 152;
            for (int i = 0; i < 47; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int xp = 152, yp = 199; yp < 246; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 47; j++)
            for (int k = 0; k < 29; k++)
                f[i * 29 + k + 246] += f[i * 47 + j + 199] * w[j * 29 + k + 2773];
        }
        {
            int ap = 246, bp = 4136;
            for (int cp = 275; cp < 304; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 275, yp = 0;
            for (int i = 0; i < 29; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 275, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 29; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 29, dbp = 4136;
            for (int dcp = 0; dcp < 29; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 47; j++)
            for (int k = 0; k < 29; k++) {
                b[i * 47 + j + 58] += b[i * 29 + k + 29] * w[j * 29 + k + 2773];
                dw[j * 29 + k + 2773] += f[i * 47 + j + 199] * b[i * 29 + k + 29];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 58; i < 105; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 105, dyp = 58; dxp < 152; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 105, yp = 152, dxp = 152, dyp = 105;
            for (int i = 0; i < 47; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 199, dbp = 2726;
            for (int dcp = 152; dcp < 199; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 58; j++)
            for (int k = 0; k < 47; k++) {
                b[i * 58 + j + 246] += b[i * 47 + k + 199] * w[j * 47 + k + 0];
                dw[j * 47 + k + 0] += f[i * 58 + j + 0] * b[i * 47 + k + 199];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 246; i < 304; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 0, dyp = 246; dxp < 58; dxp++)
                dx[dxp] += b[dyp++] * inv;
        }
    }
}
