package test.meta;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import clsf.Dataset;
import mfextraction.KNNLandMark;

public class GetDataScore {
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

        double avg = 0;

        for (Dataset dataset : train) {
            double fscore = knnScore.applyAsDouble(dataset) * 2;
            avg += fscore;
        }
        avg /= train.size();
        System.out.println(avg);

        double mse = 0;

        for (Dataset dataset : test) {
            double fscore = knnScore.applyAsDouble(dataset) * 2;
            double diff = fscore - avg;
            mse += diff * diff;
        }
        mse /= test.size();

        System.out.println(Math.sqrt(mse));

    }
}
