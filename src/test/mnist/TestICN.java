package test.mnist;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import core.Result;
import dataset.Convolution;
import dataset.FullConvolution;
import dataset.FullConvolution.Input;
import dataset.ImgConvolution;

public class TestICN extends JFrame {

    private static final long serialVersionUID = 1L;
    final int n = 10;
    private double[][] lm = new double[n][n];
    private double la = 1.0 / n;

    final double alpha = 0.95;

    public void draw(int iter, int[][] cm) {

        int all = 0;
        int cor = 0;

        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                sum += cm[i][j];
            }

            all += sum;
            cor += cm[i][i];

            if (sum > 0) {
                for (int j = 0; j < n; j++) {
                    lm[i][j] = lm[i][j] * alpha + cm[i][j] * (1.0 - alpha) / sum;
                }
            }
        }

        la = la * alpha + cor * (1.0 - alpha) / all;

        String state = String.format(Locale.ENGLISH, "%3d %8.3f", iter, la * 100);

        setTitle(state);
        System.out.println(state);

        int s = (getHeight() - 100) / n;
        int m = s * n;
        BufferedImage image = new BufferedImage(m, m, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < m; x++) {
            for (int y = 0; y < m; y++) {
                float g = (float) (1 - lm[y / s][x / s]);
                Color color = new Color(g, g, g);
                image.setRGB(x, y, color.getRGB());
            }
        }

        for (int i = 0; i < n; i++) {
            int rgb = Color.RED.getRGB();

            int x = i * s, y = i * s;

            for (int dx = 0; dx < s; dx++) {
                image.setRGB(x + dx, y + s - s, rgb);
                image.setRGB(x + dx, y + s - 1, rgb);
            }

            for (int dy = 0; dy < s; dy++) {
                image.setRGB(x + s - s, y + dy, rgb);
                image.setRGB(x + s - 1, y + dy, rgb);
            }

        }

        graph.setIcon(new ImageIcon(image));

        repaint();

    }

    class MNIST implements Runnable {
        final TestICN testImgs;

        public MNIST(TestICN testImgs) {
            this.testImgs = testImgs;
        }

        @Override
        public void run() {

            int trainSize = 60000;
            Digit[] train = new Digit[trainSize];

            int testSize = 10000;
            Digit[] test = new Digit[testSize];

            try {
                byte[] rawValues = Files.readAllBytes(new File("mnist\\train-images.idx3-ubyte").toPath());
                byte[] rawLabels = Files.readAllBytes(new File("mnist\\train-labels.idx1-ubyte").toPath());
                int valuesPointer = 16;
                int labelsPointer = 8;

                for (int i = 0; i < trainSize; i++) {
                    double[] values = new double[784];
                    for (int j = 0; j < values.length; j++) {
                        values[j] = (rawValues[valuesPointer++] & 0xFF) / 255.0;
                    }
                    int label = rawLabels[labelsPointer++] & 0xFF;
                    train[i] = new Digit(values, label);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
            try {
                byte[] rawValues = Files.readAllBytes(new File("mnist\\t10k-images.idx3-ubyte").toPath());
                byte[] rawLabels = Files.readAllBytes(new File("mnist\\t10k-labels.idx1-ubyte").toPath());
                int valuesPointer = 16;
                int labelsPointer = 8;

                for (int i = 0; i < testSize; i++) {
                    double[] values = new double[784];
                    for (int j = 0; j < values.length; j++) {
                        values[j] = (rawValues[valuesPointer++] & 0xFF) / 255.0;
                    }
                    int label = rawLabels[labelsPointer++] & 0xFF;
                    test[i] = new Digit(values, label);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            HVFold hvFold = new HVFold();
            Convolution convolution = new ImgConvolution(40, hvFold, hvFold);

            Encoder encoder = new Encoder();
            Decoder decoder = new Decoder();

            FullConvolution net = new FullConvolution(9, encoder, convolution, decoder, 10);

            double[] enc = new double[encoder.numBoundVars()];
            double[] dec = new double[decoder.numBoundVars()];
            double[] hor = new double[hvFold.numBoundVars()];
            double[] ver = new double[hvFold.numBoundVars()];

            double[] menc = new double[enc.length];
            double[] mdec = new double[dec.length];
            double[] mhor = new double[hor.length];
            double[] mver = new double[ver.length];

            double[] venc = new double[enc.length];
            double[] vdec = new double[dec.length];
            double[] vhor = new double[hor.length];
            double[] vver = new double[ver.length];

            Arrays.fill(venc, 1.0);
            Arrays.fill(vdec, 1.0);
            Arrays.fill(vhor, 1.0);
            Arrays.fill(vver, 1.0);

            encoder.init(enc);
            decoder.init(dec);
            hvFold.init(hor);
            hvFold.init(ver);

            int batch = 20;
            double lr = 10;
            double gamma = 0.95;
            double beta1 = 0.95;
            double beta2 = 0.95;

            Random random = new Random();

            for (int iter = 1; iter <= 100000; iter++) {
                int[][] cm = new int[n][n];

                for (int cnt = 0; cnt < batch; cnt++) {

                    Digit digit = train[random.nextInt(train.length)];

                    int e = digit.label;

                    double[] x = digit.values;

                    double[] ty = new double[n];
                    Arrays.fill(ty, -0.9);
                    ty[e] = 0.9;

                    double[][][] obj = new double[28][28][9];
                    for (int row = 0; row < 28; row++) {
                        for (int col = 0; col < 28; col++) {

                            for (int i = 0, dr = -1; dr <= +1; dr++) {
                                for (int dc = -1; dc <= +1; dc++, i++) {
                                    double value = 0;

                                    int r = row + dr;
                                    int c = col + dc;

                                    if (0 <= r && r < 28) {
                                        if (0 <= c && c < 28) {
                                            value = x[r * 28 + c];
                                        }
                                    }

                                    obj[row][col][i] = value;

                                }
                            }

                        }
                    }

                    Result<Input, double[]> result = net.result(obj, enc, hor, ver, dec);

                    double[] y = result.value();
                    double[] dy = new double[n];

                    for (int i = 0; i < n; i++) {
                        dy[i] = y[i] - ty[i];
                    }

                    FullConvolution.Input delta = result.derivative().apply(dy);

                    for (int i = 0; i < enc.length; i++) {
                        menc[i] = beta1 * menc[i] + (1 - beta1) * delta.enc[i];
                        venc[i] = beta2 * venc[i] + (1 - beta2) * delta.enc[i] * delta.enc[i];
                        enc[i] -= lr * menc[i] / (Math.sqrt(venc[i]) + 1e-8);
                    }
                    for (int i = 0; i < dec.length; i++) {
                        mdec[i] = beta1 * mdec[i] + (1 - beta1) * delta.dec[i];
                        vdec[i] = beta2 * vdec[i] + (1 - beta2) * delta.dec[i] * delta.dec[i];
                        dec[i] -= lr * mdec[i] / (Math.sqrt(vdec[i]) + 1e-8);
                    }
                    for (int i = 0; i < hor.length; i++) {
                        mhor[i] = beta1 * mhor[i] + (1 - beta1) * delta.hor[i];
                        vhor[i] = beta2 * vhor[i] + (1 - beta2) * delta.hor[i] * delta.hor[i];
                        hor[i] -= lr * mhor[i] / (Math.sqrt(vhor[i]) + 1e-8);
                    }
                    for (int i = 0; i < ver.length; i++) {
                        mver[i] = beta1 * mver[i] + (1 - beta1) * delta.ver[i];
                        vver[i] = beta2 * vver[i] + (1 - beta2) * delta.ver[i] * delta.ver[i];
                        ver[i] -= lr * mver[i] / (Math.sqrt(vver[i]) + 1e-8);
                    }

                    int r = random.nextInt(n);

                    for (int i = 0; i < n; i++) {
                        if (y[i] > y[r]) {
                            r = i;
                        }
                    }

                    cm[e][r] += 1;

                }
                if (iter % 40 == 0) {
                    for (int i = 0; i < 20; i++) {
                        System.out.printf(Locale.ENGLISH, "%7.3f ", enc[i * 70]);
                    }
                    System.out.println();
                    for (int i = 0; i < 20; i++) {
                        System.out.printf(Locale.ENGLISH, "%7.3f ", hor[i * 250]);
                    }
                    System.out.println();
                    for (int i = 0; i < 20; i++) {
                        System.out.printf(Locale.ENGLISH, "%7.3f ", ver[i * 250]);
                    }
                    System.out.println();
                    for (int i = 0; i < 20; i++) {
                        System.out.printf(Locale.ENGLISH, "%7.3f ", dec[i * 107]);
                    }
                    System.out.println();
                }

                testImgs.draw(iter, cm);
            }
        }

    }

    JLabel graph = new JLabel();

    public TestICN() {

        // setLayout(null);

        graph.setHorizontalAlignment(JLabel.CENTER);
        graph.setVerticalAlignment(JLabel.CENTER);

        add(graph);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        setBounds(640, 320, 640, 640);
        setVisible(true);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                lm[i][j] = la;
            }
        }

        // (new Thread(new NNTester(this))).start();
        (new Thread(new MNIST(this))).start();
    }

    public static void main(String[] args) {
        new TestICN();

    }

}
