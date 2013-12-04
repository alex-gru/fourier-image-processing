package bvafourier.fft;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    static final int SIZE = 256;

    public static void main(String[] args) throws Exception {

        double[] real = new double[SIZE * SIZE];
        double[] imag = new double[SIZE * SIZE];
        getDataFromImag(real, imag);

        FFT fft = new FFT();
        DFT dft = new DFT();

        System.out.println("Size of input: " + real.length);
        System.out.println("+----- Measure transform time using " + fft.name + " -----+");
        measureTransformTime(fft, real, imag);
        System.out.println("+----- Measure transform time using " + dft.name + " -----+");
        measureTransformTime(dft, real, imag);
    }

    private static void getDataFromImag(double[] real, double[] imag) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("akh0001-cut-1112629217893.png"));
        } catch (IOException e) {
        }

        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                real[i * 256 + j] = (float) getPixelData(img, j, i)[0];
                imag[i * 256 + j] = 0;
            }
        }
    }

    public static void measureTransformTime(FourierTransform transform, double[] real, double[] imag) {
        if (real.length != imag.length) {
            System.err.print("WARNING: array lengths are different...");
        }
        long start = System.nanoTime();
        transform.transform(real, imag);
        long finish = System.nanoTime();
        System.out.println("Transform took " + (finish - start) / Math.pow(10, 9) + " seconds.");
    }

    private static int[] getPixelData(BufferedImage img, int x, int y) {
        int argb = img.getRGB(x, y);

        int rgb[] = new int[]{
                (argb >> 16) & 0xff, //red
                (argb >> 8) & 0xff, //green
                (argb) & 0xff  //blue
        };
        int Y = (int) (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);

        int yuv[] = new int[]{
                Y,  //Y
                rgb[2] - Y, // U
                rgb[0] - Y   //V
        };
//        System.out.println("rgb: " + rgb[0] + " " + rgb[1] + " " + rgb[2]);
//        System.out.println("yuv: " + yuv[0] + " " + yuv[1] + " " + yuv[2]);
        return yuv;
    }
}
