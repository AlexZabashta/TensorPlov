import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import scheme.Builder;
import scheme.Node;
import tojava.CompilerToSrc;

public class BuildEHVD {

    public static void buildAndSave(String name, int... sizes) {
        Node node = Builder.buildLayers(sizes);
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
        buildAndSave("Encoder", 2, 23, 40);
        buildAndSave("HVFold", 80, 60, 40);
        buildAndSave("Decoder", 40, 30, 20, 1);
        buildAndSave("Simple", 29, 20, 15, 10, 1);
    }
}
