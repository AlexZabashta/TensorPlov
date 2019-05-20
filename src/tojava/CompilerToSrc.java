package tojava;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import scheme.ApplyFunction;
import scheme.Concat;
import scheme.Context;
import scheme.HadamardProduct;
import scheme.Multiplication;
import scheme.Node;
import scheme.Sum;
import scheme.UnitDerivative;
import scheme.UnitStd;
import scheme.Variable;
import scheme.ZeroMean;

public class CompilerToSrc {

    public static final String TAB = "    ";
    public static final String DEF_INITW = "init(double[] w)";
    public static final String DEF_FRWRD = "forward(double[] x, double[] w, double[] y, double[] f)";
    public static final String DEF_BKWRD = "backward(double[] x, double[] w, double[] y, double[] dx, double[] dw, double[] dy, double[] f, double[] b)";

    static void addToList(String indent, List<String> dst, List<String> src) {
        if (src.isEmpty()) {
            return;
        }
        dst.add(indent + "{");
        for (String line : src) {
            dst.add(indent + TAB + line);
        }
        dst.add(indent + "}");
    }

    public static List<String> compile(String pac, String className, Context context) {

        List<Program> programs = new ArrayList<>();

        for (Node node : context.order) {
            Program subProgram = compileNode(node, context);
            if (subProgram != null) {
                programs.add(subProgram);
            }
        }

        int xSize = context.variableSize.getOrDefault("x", 0);
        int wSize = context.variableSize.getOrDefault("w", 0);
        int ySize = context.variableSize.getOrDefault("y", 0);
        int fSize = context.variableSize.getOrDefault("f", 0);
        int bSize = context.variableSize.getOrDefault("b", 0);

        List<String> program = new ArrayList<>();

        if (pac != null && !pac.isEmpty()) {
            program.add("package " + pac + ";");
        }

        Set<String> imprt = new TreeSet<>();
        imprt.add("core.VectorDiffStruct");
        for (Program subProgram : programs) {
            imprt.addAll(subProgram.imprt);
        }
        for (String string : imprt) {
            program.add("import " + string + ";");
        }

        program.add("public class " + className + " extends VectorDiffStruct {");
        {
            {
                program.add(TAB + "public " + className + "() {");
                program.add(TAB + TAB + "super(" + xSize + ", " + wSize + ", " + ySize + ", " + fSize + ", " + bSize + ");");
                program.add(TAB + "}");
            }
            {
                program.add(TAB + "@Override");
                program.add(TAB + "public void " + DEF_INITW + " {");
                for (Program subProgram : programs) {
                    addToList(TAB + TAB, program, subProgram.initw);
                }
                program.add(TAB + "}");
            }

            {
                program.add(TAB + "@Override");
                program.add(TAB + "public void " + DEF_FRWRD + " {");
                for (Program subProgram : programs) {
                    addToList(TAB + TAB, program, subProgram.frwrd);
                }
                program.add(TAB + "}");
            }
            {
                program.add(TAB + "@Override");
                program.add(TAB + "public void " + DEF_BKWRD + " {");
                for (int i = programs.size() - 1; i >= 0; i--) {
                    addToList(TAB + TAB, program, programs.get(i).bkwrd);
                }
                program.add(TAB + "}");
            }
        }
        program.add("}");

        return program;
    }

    static Program compileNode(Node node, Context context) {
        if (node instanceof Variable) {
            return null;
        }

        if (node instanceof ApplyFunction) {
            return compileAJF((ApplyFunction) node, context);
        }

        if (node instanceof Multiplication) {
            return compileMUL((Multiplication) node, context);
        }

        if (node instanceof Sum) {
            return compileSUM((Sum) node, context);
        }

        if (node instanceof UnitDerivative) {
            return compileUnitDer((UnitDerivative) node, context);
        }

        if (node instanceof UnitStd) {
            return compileUnitStd((UnitStd) node, context);
        }

        if (node instanceof ZeroMean) {
            return compileZeroMean((ZeroMean) node, context);
        }

        if (node instanceof Concat) {
            return compileConcat((Concat) node, context);
        }

        if (node instanceof HadamardProduct) {
            return compileHP((HadamardProduct) node, context);
        }

        throw new UnsupportedOperationException("Can't compile " + node.getClass().getSimpleName());
    }

    private static Program compileHP(HadamardProduct node, Context context) {

        Variable a = context.f.get(node.left);
        Variable b = context.f.get(node.right);
        Variable c = context.f.get(node);

        Variable da = context.b.get(node.left);
        Variable db = context.b.get(node.right);
        Variable dc = context.b.get(node);

        Program program = new Program();

        program.frwrd.add("int ap = " + a.from + ", bp = " + b.from + ";");
        program.frwrd.add("for (int cp = " + c.from + "; cp < " + c.to + "; cp++)");
        program.frwrd.add(TAB + c.base + "[cp] += " + a.base + "[ap++] * " + b.base + "[bp++];");

        program.bkwrd.add("int ap = " + a.from + ", bp = " + b.from + ";");
        program.bkwrd.add("int dap = " + da.from + ", dbp = " + db.from + ";");
        program.bkwrd.add("for (int dcp = " + dc.from + "; dcp < " + dc.to + "; dcp++) {");
        program.bkwrd.add(TAB + da.base + "[dap++] += " + dc.base + "[dcp] * " + b.base + "[bp++];");
        program.bkwrd.add(TAB + db.base + "[dbp++] += " + dc.base + "[dcp] * " + a.base + "[ap++];");
        program.bkwrd.add("}");

        return program;
    }

    private static Program compileConcat(Concat node, Context context) {

        int len = node.subNodes.length;

        Variable t = context.f.get(node);

        Program program = new Program();

        program.frwrd.add("int tp = " + t.from + ";");

        for (int i = 0; i < len; i++) {
            Variable f = context.f.get(node.subNodes[i]);
            program.frwrd.add("for (int fp = " + f.from + "; fp < " + f.to + "; fp++)");
            program.frwrd.add(TAB + t.base + "[tp++] += " + f.base + "[fp];");
        }

        Variable dt = context.f.get(node);
        program.bkwrd.add("int dtp = " + dt.from + ";");
        for (int i = 0; i < len; i++) {
            Variable df = context.f.get(node.subNodes[i]);
            program.bkwrd.add("for (int dfp = " + df.from + "; dfp < " + df.to + "; dfp++)");
            program.bkwrd.add(TAB + df.base + "[dfp] += " + dt.base + "[dtp++];");
        }

        return program;
    }

    private static Program compileUnitStd(UnitStd node, Context context) {
        Variable s = context.f.get(node.scale);
        Variable x = context.f.get(node.subNode);
        Variable y = context.f.get(node);

        Variable ds = context.b.get(node.scale);
        Variable dx = context.b.get(node.subNode);
        Variable dy = context.b.get(node);

        Program program = new Program();

        program.frwrd.add("int xp = " + x.from + ", sp = " + s.from + ";");
        program.frwrd.add("for (int yp = " + y.from + "; yp < " + y.to + "; yp++)");
        program.frwrd.add(TAB + y.base + "[yp] += " + x.base + "[xp++] * exp(" + s.base + "[sp++]);");

        program.bkwrd.add("int sp = " + s.from + ", dxp = " + dx.from + ", dsp = " + ds.from + ", dyp = " + dy.from + ";");
        program.bkwrd.add("for (int yp = " + y.from + "; yp < " + y.to + "; yp++) {");

        program.bkwrd.add(TAB + "double sq = " + y.base + "[yp] * " + y.base + "[yp];");
        program.bkwrd.add(TAB + ds.base + "[dsp++] += " + node.derivativeFactor + " * (sq * (sq - 1));");
        program.bkwrd.add(TAB + dx.base + "[dxp++] += " + dy.base + "[dyp++] * exp(" + s.base + "[sp++]);");

        program.bkwrd.add("}");

        return program;
    }

    private static Program compileZeroMean(ZeroMean node, Context context) {
        Variable m = context.f.get(node.offset);
        Variable x = context.f.get(node.subNode);
        Variable y = context.f.get(node);

        Variable dm = context.b.get(node.offset);
        Variable dx = context.b.get(node.subNode);
        Variable dy = context.b.get(node);

        Program program = new Program();

        program.frwrd.add("int xp = " + x.from + ", mp = " + m.from + ";");
        program.frwrd.add("for (int yp = " + y.from + "; yp < " + y.to + "; yp++)");
        program.frwrd.add(TAB + y.base + "[yp] += (" + x.base + "[xp++] - " + m.base + "[mp++]);");

        program.bkwrd.add("int xp = " + x.from + ", mp = " + m.from + ", dmp = " + dm.from + ", dyp = " + dy.from + ";");
        program.bkwrd.add("for (int dxp = " + dx.from + "; dxp < " + dx.to + "; dxp++) {");
        program.bkwrd.add(TAB + dm.base + "[dmp++] += " + node.derivativeFactor + " * (" + m.base + "[mp++] - " + x.base + "[xp++]);");
        program.bkwrd.add(TAB + dx.base + "[dxp] += " + dy.base + "[dyp++];");

        program.bkwrd.add("}");

        return program;
    }

    private static Program compileUnitDer(UnitDerivative node, Context context) {

        Variable x = context.f.get(node.subNode);
        Variable y = context.f.get(node);

        Variable dx = context.b.get(node.subNode);
        Variable dy = context.b.get(node);

        Program program = new Program();

        program.frwrd.add("for (int xp = " + x.from + ", yp = " + y.from + "; yp < " + y.to + "; yp++)");
        program.frwrd.add(TAB + y.base + "[yp] += " + x.base + "[xp++];");

        program.bkwrd.add("double sum = 0.0000001;");
        program.bkwrd.add("for (int i = " + dy.from + "; i < " + dy.to + "; i++)");
        program.bkwrd.add(TAB + " sum += " + dy.base + "[i] * " + dy.base + "[i];");
        program.bkwrd.add("double inv = 1 / sqrt(sum);");

        program.bkwrd.add("for (int dxp = " + dx.from + ", dyp = " + dy.from + "; dxp < " + dx.to + "; dxp++)");
        program.bkwrd.add(TAB + dx.base + "[dxp] += " + dy.base + "[dyp++] * inv;");

        return program;
    }

    static Program compileSUM(Sum node, Context context) {

        Variable a = context.f.get(node.left);
        Variable b = context.f.get(node.right);
        Variable c = context.f.get(node);

        Variable da = context.b.get(node.left);
        Variable db = context.b.get(node.right);
        Variable dc = context.b.get(node);

        Program program = new Program();

        program.frwrd.add("int ap = " + a.from + ", bp = " + b.from + ";");
        program.frwrd.add("for (int cp = " + c.from + "; cp < " + c.to + "; cp++)");
        program.frwrd.add(TAB + c.base + "[cp] += " + a.base + "[ap++] + " + b.base + "[bp++];");

        program.bkwrd.add("int dap = " + da.from + ", dbp = " + db.from + ";");
        program.bkwrd.add("for (int dcp = " + dc.from + "; dcp < " + dc.to + "; dcp++) {");
        program.bkwrd.add(TAB + da.base + "[dap++] += " + dc.base + "[dcp];");
        program.bkwrd.add(TAB + db.base + "[dbp++] += " + dc.base + "[dcp];");
        program.bkwrd.add("}");

        return program;
    }

    static Program compileAJF(ApplyFunction node, Context context) {
        Variable x = context.f.get(node.subNode);
        Variable y = context.f.get(node);
        Variable dx = context.b.get(node.subNode);
        Variable dy = context.b.get(node);

        Program program = new Program();

        node.function.imprt(program.imprt);

        program.frwrd.add("int xp = " + x.from + ", yp = " + y.from + ";");
        program.frwrd.add("for (int i = 0; i < " + node.dim + "; i++, xp++) {");
        program.frwrd.add(TAB + y.base + "[yp++] += " + node.function.forward(x.base + "[xp]") + ";");
        program.frwrd.add("}");

        program.bkwrd.add("int xp = " + x.from + ", yp = " + y.from + ", dxp = " + dx.from + ", dyp = " + dy.from + ";");
        program.bkwrd.add("for (int i = 0; i < " + node.dim + "; i++, xp++, yp++) {");
        program.bkwrd.add(TAB + dx.base + "[dxp++] += " + dy.base + "[dyp++] * " + node.function.backward(x.base + "[xp]", y.base + "[yp]") + ";");
        program.bkwrd.add("}");

        return program;
    }

    static Program compileMUL(Multiplication node, Context context) {

        Variable a = context.f.get(node.left);
        Variable b = context.f.get(node.right);
        Variable c = context.f.get(node);

        Variable da = context.b.get(node.left);
        Variable db = context.b.get(node.right);
        Variable dc = context.b.get(node);

        Program program = new Program();

        double scale = 1 / Math.sqrt(node.midDim);

        if (a.base.equals("w") || b.base.equals("w")) {
            program.imprt.add("java.util.Random");
            program.initw.add("Random random = new Random();");
        }

        if (a.base.equals("w")) {
            program.initw.add("for (int i = " + a.from + "; i < " + a.to + "; i++)");
            program.initw.add(TAB + a.base + "[i] = " + scale + " * random.nextGaussian();");
        }

        if (b.base.equals("w")) {
            program.initw.add("for (int i = " + b.from + "; i < " + b.to + "; i++)");
            program.initw.add(TAB + b.base + "[i] = " + scale + " * random.nextGaussian();");
        }

        program.frwrd.add("for (int i = 0; i < " + node.lftDim + "; i++)");
        program.frwrd.add("for (int j = 0; j < " + node.midDim + "; j++)");
        program.frwrd.add("for (int k = 0; k < " + node.rgtDim + "; k++)");

        StringBuilder line1 = new StringBuilder();
        line1.append(c.base + "[i * " + node.rgtDim + " + k + " + c.from + "] += ");
        line1.append(a.base + "[i * " + node.midDim + " + j + " + a.from + "] * ");
        line1.append(b.base + "[j * " + node.rgtDim + " + k + " + b.from + "];");
        program.frwrd.add(TAB + line1.toString());

        program.bkwrd.add("for (int i = 0; i < " + node.lftDim + "; i++)");
        program.bkwrd.add("for (int j = 0; j < " + node.midDim + "; j++)");
        program.bkwrd.add("for (int k = 0; k < " + node.rgtDim + "; k++) {");

        StringBuilder line2 = new StringBuilder();
        line2.append(da.base + "[i * " + node.midDim + " + j + " + da.from + "] += ");
        line2.append(dc.base + "[i * " + node.rgtDim + " + k + " + dc.from + "] * ");
        line2.append(b.base + "[j * " + node.rgtDim + " + k + " + b.from + "];");
        program.bkwrd.add(TAB + line2.toString());

        StringBuilder line3 = new StringBuilder();
        line3.append(db.base + "[j * " + node.rgtDim + " + k + " + db.from + "] += ");
        line3.append(a.base + "[i * " + node.midDim + " + j + " + a.from + "] * ");
        line3.append(dc.base + "[i * " + node.rgtDim + " + k + " + dc.from + "];");
        program.bkwrd.add(TAB + line3.toString());
        program.bkwrd.add("}");

        return program;
    }

}
