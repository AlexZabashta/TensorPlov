package test.mnist;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class Decoder extends VectorDiffStruct {
    public Decoder() {
        super(64, 2778, 10, 276, 276);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 0; i < 2048; i++)
                w[i] = 0.125 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2080; i < 2592; i++)
                w[i] = 0.17677669529663687 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 2608; i < 2768; i++)
                w[i] = 0.25 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            for (int xp = 0, yp = 0; yp < 64; yp++)
                f[yp] += x[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 64; j++)
            for (int k = 0; k < 32; k++)
                f[i * 32 + k + 64] += f[i * 64 + j + 0] * w[j * 32 + k + 0];
        }
        {
            int ap = 64, bp = 2048;
            for (int cp = 96; cp < 128; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 96, yp = 128;
            for (int i = 0; i < 32; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int xp = 128, yp = 160; yp < 192; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++)
                f[i * 16 + k + 192] += f[i * 32 + j + 160] * w[j * 16 + k + 2080];
        }
        {
            int ap = 192, bp = 2592;
            for (int cp = 208; cp < 224; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 208, yp = 224;
            for (int i = 0; i < 16; i++, xp++) {
                f[yp++] += max(0.001 * f[xp], f[xp]);
            }
        }
        {
            for (int xp = 224, yp = 240; yp < 256; yp++)
                f[yp] += f[xp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 16; j++)
            for (int k = 0; k < 10; k++)
                f[i * 10 + k + 256] += f[i * 16 + j + 240] * w[j * 10 + k + 2608];
        }
        {
            int ap = 256, bp = 2768;
            for (int cp = 266; cp < 276; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 266, yp = 0;
            for (int i = 0; i < 10; i++, xp++) {
                y[yp++] += tanh(f[xp]);
            }
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int xp = 266, yp = 0, dxp = 0, dyp = 0;
            for (int i = 0; i < 10; i++, xp++, yp++) {
                b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
            }
        }
        {
            int dap = 10, dbp = 2768;
            for (int dcp = 0; dcp < 10; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 16; j++)
            for (int k = 0; k < 10; k++) {
                b[i * 16 + j + 20] += b[i * 10 + k + 10] * w[j * 10 + k + 2608];
                dw[j * 10 + k + 2608] += f[i * 16 + j + 240] * b[i * 10 + k + 10];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 20; i < 36; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 36, dyp = 20; dxp < 52; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 208, yp = 224, dxp = 52, dyp = 36;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 68, dbp = 2592;
            for (int dcp = 52; dcp < 68; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++) {
                b[i * 32 + j + 84] += b[i * 16 + k + 68] * w[j * 16 + k + 2080];
                dw[j * 16 + k + 2080] += f[i * 32 + j + 160] * b[i * 16 + k + 68];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 84; i < 116; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 116, dyp = 84; dxp < 148; dxp++)
                b[dxp] += b[dyp++] * inv;
        }
        {
            int xp = 96, yp = 128, dxp = 148, dyp = 116;
            for (int i = 0; i < 32; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * ((f[xp] < 0) ? 0.001 : 1);
            }
        }
        {
            int dap = 180, dbp = 2048;
            for (int dcp = 148; dcp < 180; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 64; j++)
            for (int k = 0; k < 32; k++) {
                b[i * 64 + j + 212] += b[i * 32 + k + 180] * w[j * 32 + k + 0];
                dw[j * 32 + k + 0] += f[i * 64 + j + 0] * b[i * 32 + k + 180];
            }
        }
        {
            double sum = 0.0000001;
            for (int i = 212; i < 276; i++)
                 sum += b[i] * b[i];
            double inv = 1 / sqrt(sum);
            for (int dxp = 0, dyp = 212; dxp < 64; dxp++)
                dx[dxp] += b[dyp++] * inv;
        }
    }
}
