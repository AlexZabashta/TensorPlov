import static java.lang.Math.*;
import core.VectorDiffStruct;
import java.util.Random;

public class LSTM extends VectorDiffStruct {
    public LSTM() {
        super(130, 9870, 84, 970, 970);
    }

    @Override
    public void init(double[] w) {
        {
            Random random = new Random();
            for (int i = 7896; i < 9828; i++)
                w[i] = 0.14744195615489714 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 0; i < 1932; i++)
                w[i] = 0.14744195615489714 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 1974; i < 3906; i++)
                w[i] = 0.14744195615489714 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 3948; i < 5880; i++)
                w[i] = 0.14744195615489714 * random.nextGaussian();
        }
        {
            Random random = new Random();
            for (int i = 5922; i < 7854; i++)
                w[i] = 0.14744195615489714 * random.nextGaussian();
        }
    }

    @Override
    public void forward(double[] x, double[] w, double[] y, double[] f) {
        {
            int tp = 0;
            for (int fp = 0; fp < 23; fp++)
                f[tp++] += x[fp];
            for (int fp = 65; fp < 88; fp++)
                f[tp++] += x[fp];
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 46; j++)
                    for (int k = 0; k < 42; k++)
                        f[i * 42 + k + 46] += f[i * 46 + j + 0] * w[j * 42 + k + 7896];
        }
        {
            int ap = 46, bp = 9828;
            for (int cp = 88; cp < 130; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 88, yp = 130;
            for (int i = 0; i < 42; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 46; j++)
                    for (int k = 0; k < 42; k++)
                        f[i * 42 + k + 172] += f[i * 46 + j + 0] * w[j * 42 + k + 0];
        }
        {
            int ap = 172, bp = 1932;
            for (int cp = 214; cp < 256; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 214, yp = 256;
            for (int i = 0; i < 42; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            int ap = 256, bp = 23;
            for (int cp = 298; cp < 340; cp++)
                f[cp] += f[ap++] * x[bp++];
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 46; j++)
                    for (int k = 0; k < 42; k++)
                        f[i * 42 + k + 340] += f[i * 46 + j + 0] * w[j * 42 + k + 1974];
        }
        {
            int ap = 340, bp = 3906;
            for (int cp = 382; cp < 424; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 382, yp = 424;
            for (int i = 0; i < 42; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            int ap = 424, bp = 88;
            for (int cp = 466; cp < 508; cp++)
                f[cp] += f[ap++] * x[bp++];
        }
        {
            int ap = 298, bp = 466;
            for (int cp = 508; cp < 550; cp++)
                f[cp] += f[ap++] + f[bp++];
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 46; j++)
                    for (int k = 0; k < 42; k++)
                        f[i * 42 + k + 550] += f[i * 46 + j + 0] * w[j * 42 + k + 3948];
        }
        {
            int ap = 550, bp = 5880;
            for (int cp = 592; cp < 634; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 592, yp = 634;
            for (int i = 0; i < 42; i++, xp++) {
                f[yp++] += (1 / (1 + exp(-(f[xp]))));
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 46; j++)
                    for (int k = 0; k < 42; k++)
                        f[i * 42 + k + 676] += f[i * 46 + j + 0] * w[j * 42 + k + 5922];
        }
        {
            int ap = 676, bp = 7854;
            for (int cp = 718; cp < 760; cp++)
                f[cp] += f[ap++] + w[bp++];
        }
        {
            int xp = 718, yp = 760;
            for (int i = 0; i < 42; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int ap = 634, bp = 760;
            for (int cp = 802; cp < 844; cp++)
                f[cp] += f[ap++] * f[bp++];
        }
        {
            int ap = 508, bp = 802;
            for (int cp = 844; cp < 886; cp++)
                f[cp] += f[ap++] + f[bp++];
        }
        {
            int xp = 844, yp = 886;
            for (int i = 0; i < 42; i++, xp++) {
                f[yp++] += tanh(f[xp]);
            }
        }
        {
            int ap = 130, bp = 886;
            for (int cp = 928; cp < 970; cp++)
                f[cp] += f[ap++] * f[bp++];
        }
        {
            int tp = 0;
            for (int fp = 928; fp < 970; fp++)
                y[tp++] += f[fp];
            for (int fp = 844; fp < 886; fp++)
                y[tp++] += f[fp];
        }
    }

    @Override
    public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
        {
            int dtp = 0;
            for (int dfp = 928; dfp < 970; dfp++)
                f[dfp] += y[dtp++];
            for (int dfp = 844; dfp < 886; dfp++)
                f[dfp] += y[dtp++];
        }
        {
            int ap = 130, bp = 886;
            int dap = 798, dbp = 42;
            for (int dcp = 0; dcp < 42; dcp++) {
                b[dap++] += b[dcp] * f[bp++];
                b[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 844, yp = 886, dxp = 84, dyp = 42;
            for (int i = 0; i < 42; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 420, dbp = 126;
            for (int dcp = 84; dcp < 126; dcp++) {
                b[dap++] += b[dcp];
                b[dbp++] += b[dcp];
            }
        }
        {
            int ap = 634, bp = 760;
            int dap = 294, dbp = 168;
            for (int dcp = 126; dcp < 168; dcp++) {
                b[dap++] += b[dcp] * f[bp++];
                b[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 718, yp = 760, dxp = 210, dyp = 168;
            for (int i = 0; i < 42; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - f[yp] * f[yp]);
            }
        }
        {
            int dap = 252, dbp = 7854;
            for (int dcp = 210; dcp < 252; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 46; j++)
                    for (int k = 0; k < 42; k++) {
                        b[i * 46 + j + 924] += b[i * 42 + k + 252] * w[j * 42 + k + 5922];
                        dw[j * 42 + k + 5922] += f[i * 46 + j + 0] * b[i * 42 + k + 252];
                    }
        }
        {
            int xp = 592, yp = 634, dxp = 336, dyp = 294;
            for (int i = 0; i < 42; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 378, dbp = 5880;
            for (int dcp = 336; dcp < 378; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 46; j++)
                    for (int k = 0; k < 42; k++) {
                        b[i * 46 + j + 924] += b[i * 42 + k + 378] * w[j * 42 + k + 3948];
                        dw[j * 42 + k + 3948] += f[i * 46 + j + 0] * b[i * 42 + k + 378];
                    }
        }
        {
            int dap = 630, dbp = 462;
            for (int dcp = 420; dcp < 462; dcp++) {
                b[dap++] += b[dcp];
                b[dbp++] += b[dcp];
            }
        }
        {
            int ap = 424, bp = 88;
            int dap = 504, dbp = 88;
            for (int dcp = 462; dcp < 504; dcp++) {
                b[dap++] += b[dcp] * x[bp++];
                dx[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 382, yp = 424, dxp = 546, dyp = 504;
            for (int i = 0; i < 42; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 588, dbp = 3906;
            for (int dcp = 546; dcp < 588; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 46; j++)
                    for (int k = 0; k < 42; k++) {
                        b[i * 46 + j + 924] += b[i * 42 + k + 588] * w[j * 42 + k + 1974];
                        dw[j * 42 + k + 1974] += f[i * 46 + j + 0] * b[i * 42 + k + 588];
                    }
        }
        {
            int ap = 256, bp = 23;
            int dap = 672, dbp = 23;
            for (int dcp = 630; dcp < 672; dcp++) {
                b[dap++] += b[dcp] * x[bp++];
                dx[dbp++] += b[dcp] * f[ap++];
            }
        }
        {
            int xp = 214, yp = 256, dxp = 714, dyp = 672;
            for (int i = 0; i < 42; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 756, dbp = 1932;
            for (int dcp = 714; dcp < 756; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 46; j++)
                    for (int k = 0; k < 42; k++) {
                        b[i * 46 + j + 924] += b[i * 42 + k + 756] * w[j * 42 + k + 0];
                        dw[j * 42 + k + 0] += f[i * 46 + j + 0] * b[i * 42 + k + 756];
                    }
        }
        {
            int xp = 88, yp = 130, dxp = 840, dyp = 798;
            for (int i = 0; i < 42; i++, xp++, yp++) {
                b[dxp++] += b[dyp++] * (1 - (f[yp])) * (f[yp]);
            }
        }
        {
            int dap = 882, dbp = 9828;
            for (int dcp = 840; dcp < 882; dcp++) {
                b[dap++] += b[dcp];
                dw[dbp++] += b[dcp];
            }
        }
        {
            for (int i = 0; i < 1; i++)
                for (int j = 0; j < 46; j++)
                    for (int k = 0; k < 42; k++) {
                        b[i * 46 + j + 924] += b[i * 42 + k + 882] * w[j * 42 + k + 7896];
                        dw[j * 42 + k + 7896] += f[i * 46 + j + 0] * b[i * 42 + k + 882];
                    }
        }
        {
            int dtp = 0;
            for (int dfp = 0; dfp < 23; dfp++)
                x[dfp] += f[dtp++];
            for (int dfp = 65; dfp < 88; dfp++)
                x[dfp] += f[dtp++];
        }
    }
}
