package test.best;

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
import dataset.UnPac;
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

public class BuildUnits {

    public static MultiVarDiffStruct<double[][][], double[]> discriminator() {
        Conv convUnit = new Conv();
        Encoder encoder = new Encoder();
        Decoder decoder = new Decoder();

        MultiVarDiffStruct<double[][][], double[][][]> pencoder = MultiVarDiffStruct.convert(new ParallelVDiffStruct(encoder, DataReader.numObjects, DataReader.numFeatures));

        MultiVarDiffStruct<double[][][], double[][][]> convolution = new SymConvolution(DataReader.numObjects, DataReader.numFeatures, 1, 1, convUnit, convUnit);

        MultiVarDiffStruct<double[][][], double[]> unpac = MultiVarDiffStruct.convertFun(new UnPac(convUnit.ySize));

        MultiVarDiffStruct<double[], double[]> mdecoder = MultiVarDiffStruct.convert(decoder);

        // core.Concat<double[][][], double[]> concat = new core.Concat<>(Pipe.of(pencoder, convolution), MultiVarDiffStruct.convertFun(new WrapVector(23)));

        return Pipe.of(pencoder, convolution, unpac, mdecoder);
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
        Relu relu = new Relu(0.01);

        int halfSize = 24;
        int fullSize = halfSize * 2;
        int metaFeatures = 23;

        buildAndSave("Encoder", Builder.buildLayers(false, 2, halfSize, fullSize));
        buildAndSave("Conv", Builder.conLstmLayer(new MemoryManager("w"), halfSize));
        {
            MemoryManager mem = new MemoryManager("w");

            Node node = new Variable("x", 0, fullSize);
            node = Builder.fullConnectedLayer(mem, node, metaFeatures, relu);
            node = Builder.fullConnectedLayer(mem, node, 10, tanh);
            node = new Multiplication(1, 10, 3, node, mem.alloc(30));
            buildAndSave("Decoder", node);
        }
        {
            MemoryManager mem = new MemoryManager("w");

            Node node = new Variable("x", 0, metaFeatures);
            node = Builder.fullConnectedLayer(mem, node, metaFeatures, relu);
            node = Builder.fullConnectedLayer(mem, node, 10, tanh);
            node = new Multiplication(1, 10, 3, node, mem.alloc(30));
            buildAndSave("Simple", node);
        }

    }
}
