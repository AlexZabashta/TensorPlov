
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

public class TestCNN extends JFrame {

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

        int s = 800 / n;
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
        final TestCNN testImgs;

        public MNIST(TestCNN testImgs) {
            this.testImgs = testImgs;
        }

        @Override
        public void run() {

            int size = 60000;
            Digit[] data = new Digit[size];

            try {
                byte[] rawValues = Files.readAllBytes(new File("mnist\\train-images.idx3-ubyte").toPath());
                byte[] rawLabels = Files.readAllBytes(new File("mnist\\train-labels.idx1-ubyte").toPath());
                int valuesPointer = 16;
                int labelsPointer = 8;

                for (int i = 0; i < size; i++) {
                    double[] values = new double[784];
                    for (int j = 0; j < values.length; j++) {
                        values[j] = (rawValues[valuesPointer++] & 0xFF) * 1.0;
                    }
                    int label = rawLabels[labelsPointer++] & 0xFF;
                    data[i] = new Digit(values, label);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            DiffFun net = new DiffFun();
            int batch = 322;
            double lr = 0.01;

            Random random = new Random();
            double[] w = new double[net.wSize];
            for (int i = 0; i < net.wSize; i++) {
                w[i] = random.nextGaussian() / 10;
            }

            for (int iter = 1; iter <= 1000; iter++) {
                int[][] cm = new int[n][n];

                double[] dw = new double[net.wSize];

                for (int cnt = 0; cnt < batch; cnt++) {

//                    if (cnt % 10 == 11) {
//                        double[] array = w.clone();
//                        for (int i = 0; i < array.length; i++) {
//                            array[i] = Math.abs(array[i]);
//                        }
//                        Arrays.sort(array);
//                        System.out.println(Arrays.toString(Arrays.copyOfRange(array, array.length - 30, array.length)));
//
//                    }

                    Digit digit = data[random.nextInt(data.length)];

                    int e = digit.label;

                    double[] x = digit.values;

                    double[] ty = new double[n];
                    Arrays.fill(ty, -0.9999987654321);
                    ty[e] *= -1;

                    double[] f = new double[net.fSize];
                    double[] y = new double[net.ySize];
                    double[] dy = new double[net.ySize];

                    net.forward(x, w, y, f);

                    int r = random.nextInt(n);

                    for (int i = 0; i < n; i++) {

                        dy[i] = y[i] - ty[i];
                        if (y[i] > y[r]) {
                            r = i;
                        }
                    }

                    cm[e][r] += 1;

                    double[] b = new double[net.bSize];
                    double[] dx = new double[net.xSize];

                    net.backward(x, w, y, dx, dw, dy, f, b);

                }

                for (int i = 0; i < net.wSize; i++) {
                    w[i] -= lr * dw[i] / batch;
                }

                testImgs.draw(iter, cm);
            }
        }

    }

    JLabel graph = new JLabel();

    public TestCNN() {

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
        new TestCNN();

    }

}
