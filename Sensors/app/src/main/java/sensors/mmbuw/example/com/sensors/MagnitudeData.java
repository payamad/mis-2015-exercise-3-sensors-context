package sensors.mmbuw.example.com.sensors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by payam on 5/25/15.
 */
public class MagnitudeData {
    private int mWindowSize;
    private FFT mFFT;
    private List<Double> mMagnitudeValues;
    private double[] mFFTTransformedResult;
    private double[] mAccelerometerData = {0.0d, 0.0d, 0.0d, 0.0d};

    public MagnitudeData(int windowSize){
        mWindowSize = (int)Math.pow(2,windowSize);
        mMagnitudeValues = new ArrayList<Double>();
        mAccelerometerData = new double[4];
        mFFT = new FFT(mWindowSize);
        for(int i=0 ; i < mWindowSize ; i++)
            mMagnitudeValues.add((0.0d));
        CalculateFFT();
    }

    public void AddToList(double value){
        if(mWindowSize <= mMagnitudeValues.size())
            mMagnitudeValues.remove(0);

        mMagnitudeValues.add(value);
        CalculateFFT();
    }

    public void ResetMagnitudeData(int windowSize){
        mWindowSize = (int)Math.pow(2,windowSize);
        if(mWindowSize < mMagnitudeValues.size())
            for(int i = 0 ; i < mMagnitudeValues.size() - mWindowSize ; i++ )
                mMagnitudeValues.remove(0);
        else
            for(int i = mMagnitudeValues.size() ; i < mWindowSize ; i++)
                mMagnitudeValues.add(0.0d);
        CalculateFFT();

    }
    private void CalculateFFT(){
        double[] actualMagnitude = new double[mWindowSize];
        double[] imaginaryMagnitude = new double[mWindowSize];

        for (int i=0 ; i < mWindowSize ; i++)
            actualMagnitude[i] = mMagnitudeValues.get(i);
        Arrays.fill(imaginaryMagnitude,0.0f);

        mFFT = new FFT(mWindowSize);
        mFFT.fft(actualMagnitude, imaginaryMagnitude);

        mFFTTransformedResult = new double[mWindowSize];
        for (int i=0 ; i < mWindowSize ; i++)
            mFFTTransformedResult[i] = Math.sqrt( actualMagnitude[i] * actualMagnitude[i] + imaginaryMagnitude[i] * imaginaryMagnitude[i]);

    }

    public double[] GetTransformedResult()
    {
        return mFFTTransformedResult;
    }

    public double[] GetAccelerometerData(){
        return mAccelerometerData;
    }

    public void setAccelerometerData(double x, double y, double z, double mag){
        mAccelerometerData[0] = x;
        mAccelerometerData[1] = y;
        mAccelerometerData[2] = z;
        mAccelerometerData[3] = mag;
    }

    public String ActivityRecognition()
    {
        double average = 0.0d;
        String result;
        for(int i = 0 ; i < mWindowSize ; i++){
            average += mFFTTransformedResult[i];
        }
        average = average / mWindowSize;
        if(average < 10.0)
            result = "idle";
        else if(average < 15.0)
            result = "walking";
        else
            result = "running";
        return average + " " +result;
    }

}
