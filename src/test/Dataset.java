package test;

public class Dataset {
    public final String name;
    public final double[][][] dataset;
    public final double[] mf, lm;

    public Dataset(String name, double[][][] dataset, double[] mf, double[] lm) {
        this.name = name;
        this.dataset = dataset;
        this.mf = mf;
        this.lm = lm;
    }
}