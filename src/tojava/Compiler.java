package tojava;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import scheme.ApplyFunction;
import scheme.Context;
import scheme.Multiplication;
import scheme.Node;
import scheme.Variable;

public class Compiler {

    public static VectorDiffStruct compile(Context context) {
        List<VectorTransform> transforms = new ArrayList<>();

        for (Node node : context.order) {
            compileNode(transforms, node, context.f, context.b);
        }

        int xSize = context.variableSize.getOrDefault("x", 0);
        int wSize = context.variableSize.getOrDefault("w", 0);
        int ySize = context.variableSize.getOrDefault("y", 0);
        int fSize = context.variableSize.getOrDefault("f", 0);
        int bSize = context.variableSize.getOrDefault("b", 0);

        return new SeqVectorTransform(xSize, wSize, ySize, fSize, bSize, transforms.toArray(new VectorTransform[0]));
    }

    static void compileAJF(List<VectorTransform> transforms, ApplyFunction node, Map<Node, Variable> f, Map<Node, Variable> b) {
        Variable varX = f.get(node.subNode);
        Variable varY = f.get(node);
        Variable varDX = b.get(node.subNode);
        Variable varDY = b.get(node);

        Selector selectX = Selector.get(varX);
        Selector selectY = Selector.get(varY);
        Selector selectDX = Selector.get(varDX);
        Selector selectDY = Selector.get(varDY);

        transforms.add(new VectorTransform() {
            @Override
            public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
                double[] sx = selectX.select(x, w, y, dx, dw, dy, f, b);
                double[] sy = selectY.select(x, w, y, dx, dw, dy, f, b);
                double[] sdx = selectDX.select(x, w, y, dx, dw, dy, f, b);
                double[] sdy = selectDY.select(x, w, y, dx, dw, dy, f, b);

                for (int i = 0; i < node.dim; i++) {
                    sdx[i + varDX.from] += node.function.derivative(sx[i + varX.from], sy[i + varY.from]) * sdy[i + varDY.from];
                }
            }

            @Override
            public void forward(double[] x, double[] w, double[] y, double[] f) {
                double[] sx = selectX.select(x, w, y, null, null, null, f, null);
                double[] sy = selectY.select(x, w, y, null, null, null, f, null);
                for (int i = 0; i < node.dim; i++) {
                    sy[i + varY.from] += node.function.execute(sx[i + varX.from]);
                }
            }
        });
    }

    static void compileMUL(List<VectorTransform> transforms, Multiplication node, Map<Node, Variable> f, Map<Node, Variable> b) {
        Variable varA = f.get(node.left);
        Variable varB = f.get(node.right);
        Variable varC = f.get(node);

        Variable varDA = b.get(node.left);
        Variable varDB = b.get(node.right);
        Variable varDC = b.get(node);

        Selector selectA = Selector.get(varA);
        Selector selectB = Selector.get(varB);
        Selector selectC = Selector.get(varC);
        Selector selectDA = Selector.get(varDA);
        Selector selectDB = Selector.get(varDB);
        Selector selectDC = Selector.get(varDC);

        transforms.add(new VectorTransform() {
            @Override
            public void backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b) {
                double[] sa = selectA.select(x, w, y, dx, dw, dy, f, b);
                double[] sb = selectB.select(x, w, y, dx, dw, dy, f, b);

                double[] sda = selectDA.select(x, w, y, dx, dw, dy, f, b);
                double[] sdb = selectDB.select(x, w, y, dx, dw, dy, f, b);
                double[] sdc = selectDC.select(x, w, y, dx, dw, dy, f, b);

                for (int i = 0; i < node.lftDim; i++) {
                    for (int j = 0; j < node.midDim; j++) {
                        for (int k = 0; k < node.rgtDim; k++) {
                            sda[i * node.midDim + j + varDA.from] += sdc[i * node.rgtDim + k + varDC.from] * sb[j * node.rgtDim + k + varB.from];
                            sdb[j * node.rgtDim + k + varDB.from] += sa[i * node.midDim + j + varA.from] * sdc[i * node.rgtDim + k + varDC.from];
                        }
                    }
                }
            }

            @Override
            public void forward(double[] x, double[] w, double[] y, double[] f) {
                double[] sa = selectA.select(x, w, y, null, null, null, f, null);
                double[] sb = selectB.select(x, w, y, null, null, null, f, null);
                double[] sc = selectC.select(x, w, y, null, null, null, f, null);

                for (int i = 0; i < node.lftDim; i++) {
                    for (int j = 0; j < node.midDim; j++) {
                        for (int k = 0; k < node.rgtDim; k++) {
                            sc[i * node.rgtDim + k + varC.from] += sa[i * node.midDim + j + varA.from] * sb[j * node.rgtDim + k + varB.from];
                        }
                    }
                }

            }
        });
    }

    static void compileNode(List<VectorTransform> transforms, Node node, Map<Node, Variable> f, Map<Node, Variable> b) {
        if (node instanceof Variable) {
            return;
        }

        if (node instanceof ApplyFunction) {
            compileAJF(transforms, (ApplyFunction) node, f, b);
            return;
        }

        if (node instanceof Multiplication) {
            compileMUL(transforms, (Multiplication) node, f, b);
            return;
        }

        throw new UnsupportedOperationException("Can't compile " + node.getClass().getSimpleName());
    }

}
