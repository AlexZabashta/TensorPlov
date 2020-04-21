package dev;

import java.util.Arrays;

import dev.arrays.JavaProcessor;
import dev.arrays.SubArray;
import dev.multidim.dynamic.DynamicEvaluation;
import dev.multidim.dynamic.graph.DynNode;
import dev.multidim.dynamic.graph.Variable;
import dev.multidim.size.Dim;
import dev.multidim.size.Val;

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

        JavaProcessor javaProcessor = new JavaProcessor();
        DynamicEvaluation<double[]> builder = new DynamicEvaluation<double[]>(javaProcessor);

        Variable<double[]> a = builder.variable(dim, new SubArray<double[]>(arrayA, len));
        Variable<double[]> b = builder.variable(dim, new SubArray<double[]>(arrayB, len));

        DynNode<double[]> s = builder.sum(a, b);

        System.out.println(Arrays.toString(a.value.array));
        System.out.println(Arrays.toString(b.value.array));
        System.out.println(Arrays.toString(s.value.array));

    }
}
