package test.gan;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.tuple.Pair;

import test.DataReader;
import test.Dataset;

public class CalcDist {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(6);
        List<Dataset> datasets = DataReader.readZipData("csv.zip", executor);
        executor.shutdown();

        Pair<List<Dataset>, List<Dataset>> tt = DataReader.splitData("test.txt", datasets);

        List<Dataset> train = tt.getLeft();
        List<Dataset> test = tt.getRight();

        double avgMin = 0;
        double avgMean = 0;

        for (Dataset target : test) {
            double best = Double.POSITIVE_INFINITY;
            double mean = 0;
            for (Dataset dataset : train) {
                double sum = 0;

                for (int i = 0; i < 23; i++) {
                    double diff = target.mf[i] - dataset.mf[i];
                    sum += diff * diff;
                }

                double dist = Math.sqrt(sum);
                best = Math.min(best, dist);
                mean += dist;
            }
            avgMin += best;
            avgMean += mean / train.size();
        }

        System.out.println(avgMin / test.size());
        System.out.println(avgMean / test.size());

    }

}
