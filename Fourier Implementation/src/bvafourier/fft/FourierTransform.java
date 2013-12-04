package bvafourier.fft;

/**
 * User: alexgru
 */
public interface FourierTransform {
    /*
     * The vector's length must be a power of 2.
     */

    public void transform(double[] real, double[] imag);
}
