package test.best;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import clsf.Dataset;
import clsf.WekaConverter;
import mfextraction.KNNLandMark;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class GetDataScore {

    static final ToDoubleFunction<Dataset> knnScore = new ToDoubleFunction<Dataset>() {
        @Override
        public double applyAsDouble(Dataset dataset) {
            try {
                Instances instances = WekaConverter.convert(dataset);
                Evaluation evaluation = new Evaluation(instances);
                evaluation.crossValidateModel(new IBk(), instances, 8, new Random(42));
                return evaluation.weightedFMeasure();
            } catch (Exception e) {
                return 0;
            }
        }
    };

    static final ToDoubleFunction<Dataset> svmScore = new ToDoubleFunction<Dataset>() {
        @Override
        public double applyAsDouble(Dataset dataset) {
            try {
                Instances instances = WekaConverter.convert(dataset);
                Evaluation evaluation = new Evaluation(instances);
                evaluation.crossValidateModel(new SMO(), instances, 8, new Random(42));
                return evaluation.weightedFMeasure();
            } catch (Exception e) {
                return 0;
            }
        }
    };

    static final ToDoubleFunction<Dataset> rfrScore = new ToDoubleFunction<Dataset>() {
        @Override
        public double applyAsDouble(Dataset dataset) {
            try {
                Instances instances = WekaConverter.convert(dataset);
                Evaluation evaluation = new Evaluation(instances);
                evaluation.crossValidateModel(new J48(), instances, 8, new Random(42));
                return evaluation.weightedFMeasure();
            } catch (Exception e) {
                return 0;
            }
        }
    };

    static final Function<Dataset, double[]> relScore = new Function<Dataset, double[]>() {

        @Override
        public double[] apply(Dataset dataset) {

            double[] score = { knnScore.applyAsDouble(dataset), svmScore.applyAsDouble(dataset), rfrScore.applyAsDouble(dataset) };
            double maxScore = Double.NEGATIVE_INFINITY;
            for (double val : score) {
                maxScore = Math.max(maxScore, val);
            }

            for (int i = 0; i < 3; i++) {
                if (score[i] >= maxScore - 0.03) {
                    score[i] = +1;
                } else {
                    score[i] = -1;
                }
            }

            return score;
        }
    };

    public static void main(String[] args) throws IOException, InterruptedException {

        final int numFeatures = 16;
        final int numObjectsPerClass = 64;

        String[] classNames = { "zero", "one" };

        final int numClasses = classNames.length;
        final int numObjects = numObjectsPerClass * numClasses;

        ToDoubleFunction<Dataset> knnScore = new KNNLandMark();
        List<Dataset> datasets = new ArrayList<>();

        for (File datafolder : new File("csv").listFiles()) {
            try {
                double[][] data = new double[numObjects][numFeatures];
                int[] labels = new int[numObjects];

                String[] header = new String[numFeatures];

                for (int f = 0; f < numFeatures; f++) {
                    header[f] = "f" + f;
                }

                for (int oid = 0, label = 0; label < numClasses; label++) {
                    try (CSVParser parser = new CSVParser(new FileReader(datafolder.getPath() + File.separator + classNames[label] + ".csv"), CSVFormat.DEFAULT.withHeader(header))) {
                        for (CSVRecord record : parser) {
                            for (int fid = 0; fid < numFeatures; fid++) {
                                data[oid][fid] = Double.parseDouble(record.get(fid));
                            }
                            labels[oid++] = label;
                        }
                    }
                }
                Dataset dataset = new Dataset(datafolder.getName(), true, data, false, labels);

                datasets.add(dataset);

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        Collections.sort(datasets, Comparator.comparing(dataset -> dataset.name));

        List<Dataset> train = new ArrayList<>();
        List<Dataset> test = new ArrayList<>();

        for (int i = 0; i < datasets.size(); i++) {
            if (i % 10 == 0) {
                test.add(datasets.get(i));
            } else {
                train.add(datasets.get(i));
            }
        }

        double[] avg = { 0, 0, 0 };

        for (Dataset dataset : train) {
            double[] score = relScore.apply(dataset);
            for (int i = 0; i < 3; i++) {
                avg[i] += score[i];
            }
        }

        for (int i = 0; i < 3; i++) {
            avg[i] /= train.size();
        }

        System.out.println(Arrays.toString(avg));

        double mse = 0;

        for (Dataset dataset : test) {
            double[] score = relScore.apply(dataset);
            for (int i = 0; i < 3; i++) {
                double diff = avg[i] - score[i];
                mse += diff * diff;
            }
        }
        mse /= test.size();
        mse /= 3;

        System.out.println(Math.sqrt(mse));

    }
}
