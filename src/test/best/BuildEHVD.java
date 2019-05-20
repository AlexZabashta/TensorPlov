package test.best;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import scheme.Builder;
import scheme.Node;
import tojava.CompilerToSrc;

public class BuildEHVD {

    public static void buildAndSave(String name, boolean lastLinear, int... sizes) {
        Node node = Builder.buildLayers(lastLinear, sizes);
        List<String> programm = CompilerToSrc.compile(null, name, node.preCompile());
        try (PrintWriter writer = new PrintWriter(name + ".java")) {
            for (String line : programm) {
                writer.println(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        buildAndSave("Encoder", false, 2, 16, 32, 40);
        buildAndSave("HVFold", false, 80, 60, 40);
        buildAndSave("Decoder", true, 40, 29, 20, 15, 3);
        buildAndSave("Simple", true, 29, 20, 15, 3);
    }
}
