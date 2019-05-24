package test.gan;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import core.MultiVarDiffStruct;
import core.ParallelVDiffStruct;
import core.Pipe;
import core.WrapVector;
import dataset.Convolution;
import dataset.Deconvolution;
import dataset.ReconstructDataset;
import dataset.SymConvolution;
import scheme.Builder;
import scheme.Concat;
import scheme.MemoryManager;
import scheme.Multiplication;
import scheme.Node;
import scheme.Relu;
import scheme.Tanh;
import scheme.Variable;
import test.DataReader;
import tojava.CompilerToSrc;

public class BuildGanUnits {

    public static Pipe<Pair<double[][][], double[]>, ?, double[]> discriminator() {
        DisConv convUnit = new DisConv();
        DisEncoder encoder = new DisEncoder();
        DisDecoder decoder = new DisDecoder();

        Convolution convolution = new SymConvolution(DataReader.numObjects, DataReader.numFeatures, convUnit, convUnit);

        MultiVarDiffStruct<double[][][], double[][][]> pencoder = MultiVarDiffStruct.convert(new ParallelVDiffStruct(encoder, DataReader.numObjects, DataReader.numFeatures));
        MultiVarDiffStruct<double[], double[]> mdecoder = MultiVarDiffStruct.convert(decoder);
        core.Concat<double[][][], double[]> concat = new core.Concat<>(Pipe.of(pencoder, convolution), MultiVarDiffStruct.convertFun(new WrapVector(23)));

        return Pipe.of(concat, mdecoder);
    }

    public static Pipe<double[], ?, double[][][]> generator() {

        GenDeconv decUnit = new GenDeconv();
        GenDecoder decoder = new GenDecoder();
        GenEncoder encoder = new GenEncoder();

        MultiVarDiffStruct<double[], double[]> mencoder = MultiVarDiffStruct.convert(encoder);

        MultiVarDiffStruct<double[], double[][][]> deconvolution = new Deconvolution(DataReader.numObjects, DataReader.numFeatures, decUnit, decUnit);

        MultiVarDiffStruct<double[][][], double[][][]> pdecoder = MultiVarDiffStruct.convert(new ParallelVDiffStruct(decoder, DataReader.numObjects, DataReader.numFeatures));
        MultiVarDiffStruct<double[][][], double[][][]> rec = MultiVarDiffStruct.convertFun(new ReconstructDataset(DataReader.numObjects, DataReader.numFeatures));

        return Pipe.of(mencoder, deconvolution, pdecoder, rec);
    }

    public static void buildAndSave(String name, Node root) {
        List<String> programm = CompilerToSrc.compile(null, name, root.preCompile());
        try (PrintWriter writer = new PrintWriter(name + ".java")) {
            for (String line : programm) {
                writer.println(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        // Builder.buildLayers(lastLinear, sizes);
        // boolean lastLinear, int... sizes

        // buildAndSave("GenEncoder", false, 2, 16, 32, 40);
        // buildAndSave("GenDecoder", true, 40, 29, 20, 15, 3);

        Tanh tanh = new Tanh();
        Relu relu = new Relu(0.001);

        int halfSize = 16;
        int fullSize = halfSize * 2;
        int metaFeatures = 23;

        buildAndSave("DisEncoder", Builder.buildLayers(false, 2, halfSize, fullSize));
        {
            Node node = new Variable("x", 0, fullSize + metaFeatures);
            MemoryManager mem = new MemoryManager("w");
            node = Builder.fullConnectedLayer(mem, node, 25, relu);
            node = Builder.fullConnectedLayer(mem, node, 10, tanh);
            Node lm = new Multiplication(1, 10, 3, node, mem.alloc(30));
            Node f = Builder.fullConnectedLayer(mem, node, 1, tanh);
            Node out = new Concat(lm, f);

            buildAndSave("DisDecoder", out);
        }
        buildAndSave("DisConv", Builder.conLstmLayer(new MemoryManager("w"), halfSize));

        buildAndSave("GenEncoder", Builder.buildLayers(false, 2 * metaFeatures, halfSize + metaFeatures, fullSize));
        buildAndSave("GenDecoder", Builder.buildLayers(false, fullSize, halfSize, 2));
        buildAndSave("GenDeconv", Builder.decLstmLayer(new MemoryManager("w"), halfSize));

    }
}
