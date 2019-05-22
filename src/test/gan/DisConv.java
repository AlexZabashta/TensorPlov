package test.gan;
import  static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;
public class DisConv extends VectorDiffStruct {
    public DisConv() {
        super(64, 2640, 32, 384, 384);
    }
    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 2112; i < 2624; i++)
                w[i] = 0.17677669529663687 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 0; i < 512; i++)
                w[i] = 0.17677669529663687 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 528; i < 1040; i++)
                w[i] = 0.17677669529663687 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1056; i < 1568; i++)
                w[i] = 0.17677669529663687 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1584; i < 2096; i++)
                w[i] = 0.17677669529663687 * random.nextGaussian();
        }
    }
    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            int tp = 0;
            for (int fp = 0; fp < 16; fp++)
                f[tp++] += x[fp];
            for (int fp = 32; fp < 48; fp++)
                f[tp++] += x[fp];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++)
                f[i * 16 + k + 32] += f[i * 32 + j + 0] * w[j * 16 + k + 2112];
        }
        {
            int ap = 32, bp = 2624;
            for (int cp = 48; cp < 64; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 48, yp = 64;
            for (int i = 0; i < 16; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++)
                f[i * 16 + k + 80] += f[i * 32 + j + 0] * w[j * 16 + k + 0];
        }
        {
            int ap = 80, bp = 512;
            for (int cp = 96; cp < 112; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 96, yp = 112;
            for (int i = 0; i < 16; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            int ap = 112, bp = 16;
            for (int cp = 128; cp < 144; cp++)
                f[cp] += f[ap++] * x[bp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++)
                f[i * 16 + k + 144] += f[i * 32 + j + 0] * w[j * 16 + k + 528];
        }
        {
            int ap = 144, bp = 1040;
            for (int cp = 160; cp < 176; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 160, yp = 176;
            for (int i = 0; i < 16; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            int ap = 176, bp = 48;
            for (int cp = 192; cp < 208; cp++)
                f[cp] += f[ap++] * x[bp++];
        }
        {
            int ap = 128, bp = 192;
            for (int cp = 208; cp < 224; cp++)
                f[cp] += f[ap++] + f[bp++];
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++)
                f[i * 16 + k + 224] += f[i * 32 + j + 0] * w[j * 16 + k + 1056];
        }
        {
            int ap = 224, bp = 1568;
            for (int cp = 240; cp < 256; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 240, yp = 256;
            for (int i = 0; i < 16; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++)
                f[i * 16 + k + 272] += f[i * 32 + j + 0] * w[j * 16 + k + 1584];
        }
        {
            int ap = 272, bp = 2096;
            for (int cp = 288; cp < 304; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 288, yp = 304;
            for (int i = 0; i < 16; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int ap = 256, bp = 304;
            for (int cp = 320; cp < 336; cp++)
                f[cp] += f[ap++] * f[bp++];
        }
        {
            int ap = 208, bp = 320;
            for (int cp = 336; cp < 352; cp++)
                f[cp] += f[ap++] + f[bp++];
        }
        {
            int xp = 336, yp = 352;
            for (int i = 0; i < 16; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int ap = 64, bp = 352;
            for (int cp = 368; cp < 384; cp++)
                f[cp] += f[ap++] * f[bp++];
        }
        {
            int tp = 0;
            for (int fp = 368; fp < 384; fp++)
                y[tp++] += f[fp];
            for (int fp = 336; fp < 352; fp++)
                y[tp++] += f[fp];
        }
    }
    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int dtp = 0;
            for (int dfp = 368; dfp < 384; dfp++)
                f[dfp] += y[dtp++];
            for (int dfp = 336; dfp < 352; dfp++)
                f[dfp] += y[dtp++];
        }
        {
            int ap = 64, bp = 352;
            int dap = 304, dbp = 16;
            for (int dcp = 0; dcp < 16; dcp++) {
                b[dap++] += b[dcp] * f[bp++];
                b[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 336, yp = 352, dxp = 32, dyp = 16;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 160, dbp = 48;
            for (int dcp = 32; dcp < 48; dcp++) {
                b[dap++] += b[dcp];
                b[dbp++] += b[dcp];
            }
        }
        {
            int ap = 256, bp = 304;
            int dap = 112, dbp = 64;
            for (int dcp = 48; dcp < 64; dcp++) {
                b[dap++] += b[dcp] * f[bp++];
                b[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 288, yp = 304, dxp = 80, dyp = 64;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 96, dbp = 2096;
            for (int dcp = 80; dcp < 96; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++) {
                b[i * 32 + j + 352] += b[i * 16 + k + 96] * w[j * 16 + k + 1584];
                dw[j * 16 + k + 1584] += f[i * 32 + j + 0] * b[i * 16 + k + 96];
            }
        }
        {
            int xp = 240, yp = 256, dxp = 128, dyp = 112;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 144, dbp = 1568;
            for (int dcp = 128; dcp < 144; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++) {
                b[i * 32 + j + 352] += b[i * 16 + k + 144] * w[j * 16 + k + 1056];
                dw[j * 16 + k + 1056] += f[i * 32 + j + 0] * b[i * 16 + k + 144];
            }
        }
        {
            int dap = 240, dbp = 176;
            for (int dcp = 160; dcp < 176; dcp++) {
                b[dap++] += b[dcp];
                b[dbp++] += b[dcp];
            }
        }
        {
            int ap = 176, bp = 48;
            int dap = 192, dbp = 48;
            for (int dcp = 176; dcp < 192; dcp++) {
                b[dap++] += b[dcp] * x[bp++];
                dx[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 160, yp = 176, dxp = 208, dyp = 192;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 224, dbp = 1040;
            for (int dcp = 208; dcp < 224; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++) {
                b[i * 32 + j + 352] += b[i * 16 + k + 224] * w[j * 16 + k + 528];
                dw[j * 16 + k + 528] += f[i * 32 + j + 0] * b[i * 16 + k + 224];
            }
        }
        {
            int ap = 112, bp = 16;
            int dap = 256, dbp = 16;
            for (int dcp = 240; dcp < 256; dcp++) {
                b[dap++] += b[dcp] * x[bp++];
                dx[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 96, yp = 112, dxp = 272, dyp = 256;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 288, dbp = 512;
            for (int dcp = 272; dcp < 288; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++) {
                b[i * 32 + j + 352] += b[i * 16 + k + 288] * w[j * 16 + k + 0];
                dw[j * 16 + k + 0] += f[i * 32 + j + 0] * b[i * 16 + k + 288];
            }
        }
        {
            int xp = 48, yp = 64, dxp = 320, dyp = 304;
            for (int i = 0; i < 16; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 336, dbp = 2624;
            for (int dcp = 320; dcp < 336; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
            for (int j = 0; j < 32; j++)
            for (int k = 0; k < 16; k++) {
                b[i * 32 + j + 352] += b[i * 16 + k + 336] * w[j * 16 + k + 2112];
                dw[j * 16 + k + 2112] += f[i * 32 + j + 0] * b[i * 16 + k + 336];
            }
        }
        {
            int dtp = 0;
            for (int dfp = 0; dfp < 16; dfp++)
                x[dfp] += f[dtp++];
            for (int dfp = 32; dfp < 48; dfp++)
                x[dfp] += f[dtp++];
        }
    }
}
