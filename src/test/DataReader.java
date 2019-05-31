package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.Pair;

import ru.ifmo.ctddev.ml.mfe.JointDecMF;
import ru.ifmo.ctddev.ml.mfe.Landmarks;
import ru.ifmo.ctddev.ml.mfe.MetaFeatures;

public class DataReader {
    public static final int numFeatures = 16;
    public static final int numObjectsPerClass = 64;

    static final String[] classNames = { "zero", "one" };

    public static final int numClasses = classNames.length;
    public static final int numObjects = numObjectsPerClass * numClasses;
    public static final int numMetaFeatures = MetaFeatures.LENGTH; // 23
    public static final int numLandmarks = Landmarks.LENGTH; // 3
    public static final int numJointMF = JointDecMF.LENGTH; // 26

    public static List<Dataset> readZipData(String path, ExecutorService executor) throws InterruptedException, ExecutionException, IOException {
        Map<String, double[][]> subObjects = new HashMap<>();
        Set<String> datasetNames = new HashSet<>();

        String[] header = new String[numFeatures];
        for (int f = 0; f < numFeatures; f++) {
            header[f] = "f" + f;
        }

        try (ZipFile zipFile = new ZipFile("csv.zip")) {
            zipFile.stream().forEach(new Consumer<ZipEntry>() {
                @Override
                public void accept(ZipEntry zipEntry) {
                    if (zipEntry.isDirectory()) {
                        datasetNames.add(zipEntry.getName());
                    } else {
                        double[][] subObject = new double[numObjectsPerClass][numFeatures];

                        try (CSVParser parser = new CSVParser(new InputStreamReader(zipFile.getInputStream(zipEntry)), CSVFormat.DEFAULT.withHeader(header))) {
                            int oid = 0;
                            for (CSVRecord record : parser) {
                                for (int fid = 0; fid < numFeatures; fid++) {
                                    subObject[oid][fid] = Double.parseDouble(record.get(fid));
                                }
                                ++oid;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        subObjects.put(zipEntry.getName(), subObject);
                    }
                }
            });
        }
        List<Future<Dataset>> futures = new ArrayList<>();

        for (String name : datasetNames) {
            double[][] data = new double[numObjects][numFeatures];
            int[] labels = new int[numObjects];

            for (int oid = 0, label = 0; label < numClasses; label++) {
                double[][] subObject = subObjects.get(name + classNames[label] + ".csv");

                for (int sid = 0; sid < numObjectsPerClass; sid++) {
                    for (int fid = 0; fid < numFeatures; fid++) {
                        data[oid][fid] = subObject[sid][fid];
                    }
                    labels[oid++] = label;
                }
            }

            futures.add(executor.submit(new Callable<Dataset>() {

                @Override
                public Dataset call() throws Exception {
                    double[][][] obj = new double[numObjects][numFeatures][2];
                    for (int oid = 0; oid < numObjects; oid++) {
                        for (int fid = 0; fid < numFeatures; fid++) {
                            obj[oid][fid][0] = data[oid][fid];
                            obj[oid][fid][1] = labels[oid] * 2 - 1;
                        }
                    }

                    double[] jointMF = JointDecMF.extract(numObjects, numFeatures, numClasses, data, labels);

                    // double[] jointMF = new double[numJointMF];

                    return new Dataset(name.substring(0, name.length() - 1), obj, Arrays.copyOfRange(jointMF, 0, numMetaFeatures), Arrays.copyOfRange(jointMF, numMetaFeatures, numJointMF));
                }
            }));
        }

        final List<Dataset> datasets = new ArrayList<>();

        for (Future<Dataset> future : futures) {
            datasets.add(future.get());
        }

        return datasets;
    }

    public static Pair<List<Dataset>, List<Dataset>> splitData(String testNamesFile, List<Dataset> datasets) throws IOException {
        Set<String> testNames = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(testNamesFile))) {
            String name;
            while ((name = reader.readLine()) != null && name.length() > 3) {
                testNames.add(name);
            }
        }

        List<Dataset> train = new ArrayList<>();
        List<Dataset> test = new ArrayList<>();

        for (Dataset dataset : datasets) {
            if (testNames.contains(dataset.name)) {
                test.add(dataset);
            } else {
                train.add(dataset);
            }
        }

        return Pair.of(train, test);

    }

    public static Pair<List<Dataset>, List<Dataset>> readData(String path, ExecutorService executor) throws InterruptedException, ExecutionException {
        List<Future<Dataset>> futures = new ArrayList<>();

        for (File datafolder : new File(path).listFiles()) {
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

                futures.add(executor.submit(new Callable<Dataset>() {
                    @Override
                    public Dataset call() throws Exception {
                        double[][][] obj = new double[numObjects][numFeatures][2];
                        for (int oid = 0; oid < numObjects; oid++) {
                            for (int fid = 0; fid < numFeatures; fid++) {
                                obj[oid][fid][0] = data[oid][fid];
                                obj[oid][fid][1] = labels[oid] * 2 - 1;
                            }
                        }

                        double[] jointMF = JointDecMF.extract(numObjects, numFeatures, numClasses, data, labels);
                        return new Dataset(datafolder.getName(), obj, Arrays.copyOfRange(jointMF, 0, numMetaFeatures), Arrays.copyOfRange(jointMF, numMetaFeatures, numJointMF));
                    }
                }));

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        final List<Dataset> datasets = new ArrayList<>();

        for (Future<Dataset> future : futures) {
            datasets.add(future.get());
        }

        List<Dataset> train = new ArrayList<>();
        List<Dataset> test = new ArrayList<>();
        Collections.sort(datasets, Comparator.comparing(dataset -> dataset.name));

        for (int i = 0; i < datasets.size(); i++) {
            if (i % 10 == 0) {
                test.add(datasets.get(i));
            } else {
                train.add(datasets.get(i));
            }
        }

        return Pair.of(train, test);
    }
}
