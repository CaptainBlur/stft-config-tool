package com.vulpesnovis.StftFilter;

import com.android.sdklib.util.SparseIntArray;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class Processor {
    private final int sampleRate; //inHz
    private short winSize = 20; //in mS
    private int fftSize = 256;
    private float winSizeinSamples;
    private short windowsNumber;

    private double[] buffer;
    private double[] windowNResult;
    private final NewWindowListener givenListener;
    private final boolean dbOutput;
    private final boolean oneWindow;

    /**
     *
     * @param fftSize number of samples (bins) in the output array, given to the power of 2.
     *                Recommended from 6 to 13 (inclusively). Pass 0 for a default value which is 8 (265 bins).
     * @param winSize width of the STFT window in mS. Pass 0 for a default value which is 20.
     * @param dbOutput present output values normalized by dBFS
     * @param oneWindow use this to process only one first window. For testing purposes
     */
    public Processor(int sampleRate, int fftSize, int winSize, NewWindowListener listener, boolean dbOutput, boolean oneWindow){
        this.sampleRate = sampleRate;
        if(fftSize != 0) this.fftSize  = (int)Math.pow(2, fftSize);
        if (winSize != 0) this.winSize = (short)winSize;
        givenListener = listener;
        this.dbOutput = dbOutput;
        this.oneWindow = oneWindow;
    }

    /**
     * JTransforms processing library is just brilliant
     * (but I don't think there's some kinda magic involved).
     * It somehow detects the Nyquist frequency of the source and makes the whole output plot up to it.
     * Meanwhile, "n" argument of the constructor is our desired FFT size in bins.
     * And we just pass our windows one after another, to make a snapshot of the whole input array
     *
     * @param buffer array of decoded samples of the whole source file (or whatever we had in input)
     */
    public void process (double[] buffer){
        this.buffer = buffer;
        winSizeinSamples = ((float)winSize/1000) / (1/(float)sampleRate);
        int samplesCount = 0; //total samples counter through all windows
        short timeOfInput = winSize; //elapsed time in input signal
        float fftStep = (float)(sampleRate/2)/fftSize;

        for (int i = 0; i < buffer.length/(int)winSizeinSamples; i++) {//cycle for each window in whole output sequence
            if (oneWindow) i = buffer.length/(int)winSizeinSamples;

            windowNResult = new double[fftSize*2];
            SparseIntArray windowSnapshot = new SparseIntArray();

            System.arraycopy(buffer, samplesCount, windowNResult, 0, (int)winSizeinSamples);
            DoubleFFT_1D transformer = new DoubleFFT_1D(fftSize*2);
            transformer.realForward(windowNResult);
            float j = fftStep; //iterator for frequency val in output array (key)

            for (int k = 1; k < fftSize; k++) { //cycle for each bin in every single window transform
                double re = windowNResult[2*k];
                double im = windowNResult[2*k+1];
                double mag = Math.sqrt(re*re + im*im);

                if (dbOutput){//writing array in absolute values
                    double ref = 470; //dBFS reference value measured on pure sine wave with no window function applied
                    mag = 20 * Math.log10(mag/ref);
                }

                windowSnapshot.put((int)(j*100),(int)mag);

                j+=fftStep;
            }

            givenListener.onWindowComputed(timeOfInput, windowSnapshot);

            timeOfInput += winSize;
            samplesCount += winSizeinSamples;
        }
        givenListener.endOfStream();

    }
}
