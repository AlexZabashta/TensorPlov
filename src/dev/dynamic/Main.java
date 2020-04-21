package dev.dynamic;

import java.util.Arrays;

import dev.graph.Builder;
import dev.ja.JavaProcessor;
import dev.size.Dim;
import dev.size.Val;

public class Main {
	public static void main(String[] args) {

		int len = 10;
		int offset = 0;

		double[] arrayA = new double[len];
		for (int i = 0; i < 10; i++) {
			arrayA[i] = Math.cos(i);
		}

		double[] arrayB = new double[len];
		for (int i = 0; i < 10; i++) {
			arrayB[i] = Math.sin(i);
		}

		Dim dim = new Val(len);

		Variable<double[]> a = new Variable<double[]>(arrayA, offset, dim);
		Variable<double[]> b = new Variable<double[]>(arrayB, offset, dim);

		Builder<double[]> builder = new Builder<double[]>(new JavaProcessor());

		Node<double[]> result = builder.sum(a, b);

		System.out.println(Arrays.toString(a.result));
		System.out.println(Arrays.toString(b.result));
		System.out.println(Arrays.toString(result.result));

	}
}
