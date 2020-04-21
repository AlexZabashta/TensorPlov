package dev.ja;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import dev.dynamic.ArrayProcessor;

public class JavaProcessor implements ArrayProcessor<double[]> {

	@Override
	public void add(double[] src, int srcPos, double[] dest, int destPos, int length) {
		while (--length >= 0) {
			dest[destPos++] += src[srcPos++];
		}
	}

	@Override
	public double[] alloc(int length, double initialValue) {
		double[] array = new double[length];
		Arrays.fill(array, initialValue);
		return array;
	}

	@Override
	public void applyAdd(double[] src, int srcPos, double[] dest, int destPos, int length, Object object) {
		DoubleUnaryOperator function = (DoubleUnaryOperator) object;
		while (--length >= 0) {
			dest[destPos++] += function.applyAsDouble(src[srcPos++]);
		}
	}

	@Override
	public void fill(double[] array, int offset, int length, double value) {
		while (--length >= 0) {
			array[offset++] = value;
		}
	}

	@Override
	public void free(double[] array) {

	}

	@Override
	public void get(double[] src, int srcPos, double[] dest, int destPos, int length) {
		System.arraycopy(src, srcPos, dest, destPos, length);

	}

	@Override
	public void hadp(double[] src, int srcPos, double[] dest, int destPos, int length) {
		while (--length >= 0) {
			dest[destPos++] *= src[srcPos++];
		}
	}

	@Override
	public void move(double[] src, int srcPos, double[] dest, int destPos, int length) {
		System.arraycopy(src, srcPos, dest, destPos, length);
	}

	@Override
	public void mulAdd(double[] lft, int lftPos, double[] rht, int rhtPos, double[] res, int resPos, int l, int m, int r) {
		for (int x = 0; x < l; x++) {
			for (int z = 0; z < r; z++) {
				double sum = res[resPos];
				for (int y = 0; y < m; y++) {
					sum += lft[x * m + y + lftPos] * rht[y * r + z + rhtPos];
				}
				res[resPos++] = sum;
			}
		}
	}

	@Override
	public void set(double[] src, int srcPos, double[] dest, int destPos, int length) {
		System.arraycopy(src, srcPos, dest, destPos, length);
	}

	@Override
	public double[][] typeArray(int length) {
		return new double[length][];
	}

}
