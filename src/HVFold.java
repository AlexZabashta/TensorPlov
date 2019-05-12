import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class HVFold extends VectorDiffStruct {
    public HVFold() {
        super(80, 7440, 40, 540, 540);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 80; i < 4880; i++)
                w[i] = 0.11180339887498948 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 5000; i < 7400; i++)
                w[i] = 0.12909944487358055 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            int xp = 0, mp = 0;
            for (int yp = 0; yp < 80; yp++)
                f[yp] += (x[xp++] - w[mp++]);
        }
        {
            for (int xp = 0, yp = 80; yp < 160; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 80; j++)
            for (int k = 0; k < 60; k++)
                f[i * 60 + k + 160] += f[i * 80 + j + 80] * w[j * 60 + k + 80];
        }
        {
            int ap = 160, bp = 4880;
            for (int cp = 220; cp < 280; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 220, yp = 280;
            for (int i = 0; i < 60; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int xp = 280, mp = 4940;
            for (int yp = 340; yp < 400; yp++)
                f[yp] += (f[xp++] - w[mp++]);
        }
        {
            for (int xp = 340, yp = 400; yp < 460; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 60; j++)
            for (int k = 0; k < 40; k++)
                f[i * 40 + k + 460] += f[i * 60 + j + 400] * w[j * 40 + k + 5000];
        }
        {
            int ap = 460, bp = 7400;
            for (int cp = 500; cp < 540; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 500, yp = 0;
            for (int i = 0; i < 40; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 500, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 40; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 40, dbp = 7400;
            for (int dcp = 0; dcp < 40; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 60; j++)
            for (int k = 0; k < 40; k++) {
                b[i * 60 + j + 80] += b[i * 40 + k + 40] * w[j * 40 + k + 5000];
                dw[j * 40 + k + 5000] += f[i * 60 + j + 400] * b[i * 40 + k + 40];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 80; i < 140; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 140, dyp = 80; dxp < 200; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 280, mp = 4940, dmp = 4940, dyp = 140;
            for (int dxp = 200; dxp < 260; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - f[xp++]);
                b[dxp] += b[dyp++];
            }
        }
        {
            int xp = 220, yp = 280, dxp = 260, dyp = 200;
            for (int i = 0; i < 60; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 320, dbp = 4880;
            for (int dcp = 260; dcp < 320; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 80; j++)
            for (int k = 0; k < 60; k++) {
                b[i * 80 + j + 380] += b[i * 60 + k + 320] * w[j * 60 + k + 80];
                dw[j * 60 + k + 80] += f[i * 80 + j + 80] * b[i * 60 + k + 320];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 380; i < 460; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 460, dyp = 380; dxp < 540; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 0, mp = 0, dmp = 0, dyp = 460;
            for (int dxp = 0; dxp < 80; dxp++) {
                dw[dmp++] += 0.01 * (w[mp++] - x[xp++]);
                dx[dxp] += b[dyp++];
            }
        }
    }
}
