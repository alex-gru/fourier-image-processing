package bvafourier.fft;

/*
 * Discrete Fourier transform
 * Copyright (c) 2012 Nayuki Minase
 *
 * http://nayuki.eigenstate.org/page/how-to-implement-the-discrete-fourier-transform
 */


public final class DFT implements FourierTransform {
    String name = "DFT";

    /*
     * Computes the discrete Fourier transform (DFT) of the given vector in place.
     * All the array arguments must have the same length.
     */
    @Override
    public void transform(double[] real, double[] imag) {
        int n = real.length;
        for (int k = 0; k < n; k++) {  // For each output element
            double sumreal = 0;
            double sumimag = 0;
            for (int t = 0; t < n; t++) {  // For each input element
                sumreal += real[t] * Math.cos(2 * Math.PI * t * k / n) + imag[t] * Math.sin(2 * Math.PI * t * k / n);
                sumimag += -real[t] * Math.sin(2 * Math.PI * t * k / n) + imag[t] * Math.cos(2 * Math.PI * t * k / n);
            }
            real[k] = sumreal;
            imag[k] = sumimag;
        }
    }
}