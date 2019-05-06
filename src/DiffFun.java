import static java.lang.Math.*;
import tojava.VectorDiffStruct;

public class DiffFun extends VectorDiffStruct {
	public DiffFun() {
		super(35, 36, 15, 30, 30);
	}

	@Override
	public void init(double[] w) {
	}

	@Override
	public void forward(double[] x, double[] w, double[] y, double[] f) {
		{
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 7; j++)
					for (int k = 0; k < 5; k++)
						f[i * 5 + k + 0] += w[i * 7 + j + 0] * x[j * 5 + k + 0];
		}
		{
			int ap = 0, bp = 21;
			for (int cp = 15; cp < 30; cp++)
				f[cp] += f[ap++] + w[bp++];
		}
		{
			int xp = 15, yp = 0;
			for (int i = 0; i < 15; i++, xp++) {
				y[yp++] += tanh(f[xp]);
			}
		}
	}

	@Override
	public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f,
			double[] b) {
		{
			int xp = 15, yp = 0, dxp = 0, dyp = 0;
			for (int i = 0; i < 15; i++, xp++, yp++) {
				b[dxp++] += dy[dyp++] * (1 - y[yp] * y[yp]);
			}
		}
		{
			int dap = 15, dbp = 21;
			for (int dcp = 0; dcp < 15; dcp++) {
				b[dap++] += b[dcp];
				dw[dbp++] += b[dcp];
			}
		}
		{
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 7; j++)
					for (int k = 0; k < 5; k++) {
						dw[i * 7 + j + 0] += b[i * 5 + k + 15] * x[j * 5 + k + 0];
						dx[j * 5 + k + 0] += w[i * 7 + j + 0] * b[i * 5 + k + 15];
					}
		}
	}
}
